package com.hotel.controller.staff;

import com.hotel.model.DatPhong;
import com.hotel.model.TaiKhoan;
import com.hotel.service.DatPhongStaffService;
import com.hotel.service.DatDichVuService;
import com.hotel.service.DichVuService;
import com.hotel.service.ThanhToanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Date;

@Controller
public class StaffOrderController {

    private final DatPhongStaffService datPhongStaffService;
    private final ThanhToanService thanhToanService;
    private final DatDichVuService datDichVuService;
    private final DichVuService dichVuService;
    // PhongService của Ý làm, bạn chỉ cần gọi hàm getSoDoPhong() của nó.
    // private final PhongService phongService;

    public StaffOrderController(DatPhongStaffService datPhongStaffService, ThanhToanService thanhToanService,
                                DatDichVuService datDichVuService, DichVuService dichVuService) {
        this.datPhongStaffService = datPhongStaffService;
        this.thanhToanService = thanhToanService;
        this.datDichVuService = datDichVuService;
        this.dichVuService = dichVuService;
    }

    // Legacy staff entry points now converge on the database-backed dashboard.
    @GetMapping({"/staff", "/staff/", "/staff/quan-ly-don"})
    public String staffDashboard() {
        return "redirect:/staff/dashboard";
    }

    // ============== TASK #20, #21, #36: QUẢN LÝ ĐƠN ĐẶT PHÒNG ==============
    @GetMapping({ "/staff/don", "/admin/dat-phong" })
    public String listOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String tuNgay,
            @RequestParam(required = false) String denNgay,
            HttpServletRequest request, Model model) {

        model.addAttribute("danhSachDon", datPhongStaffService.searchAdvanced(keyword, trangThai, tuNgay, denNgay));
        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("tuNgay", tuNgay);
        model.addAttribute("denNgay", denNgay);

        boolean isAdmin = request.getServletPath().startsWith("/admin/");
        model.addAttribute("isAdminView", isAdmin);
        model.addAttribute("bookingBasePath", isAdmin ? "/admin/dat-phong" : "/staff/don");
        return "staff/quan-ly-don";
    }

    @PostMapping({ "/staff/don", "/admin/dat-phong" })
    public String processOrder(@RequestParam String action,
            @RequestParam int maDp,
            @RequestParam String trangThaiMoi,
            HttpSession session, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        boolean success = datPhongStaffService.updateTrangThaiTransaction(maDp, trangThaiMoi, currentUser.getMaTk());

        if (success) {
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật trạng thái đơn thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Có lỗi xảy ra khi cập nhật!");
        }

        String basePath = request.getServletPath().startsWith("/admin/") ? "/admin/dat-phong" : "/staff/don";
        return "redirect:" + basePath;
    }

    @GetMapping({ "/staff/don/{maDp}", "/admin/dat-phong/{maDp}" })
    public String orderDetail(@PathVariable int maDp, HttpServletRequest request, Model model) {
        DatPhong dp = datPhongStaffService.getDatPhongDetail(maDp);
        if (dp == null)
            return "redirect:/staff/don";

        model.addAttribute("donDatPhong", dp);
        model.addAttribute("chiTietList", datPhongStaffService.getChiTiet(maDp));
        model.addAttribute("danhSachDichVu", datDichVuService.findByBooking(maDp));
        model.addAttribute("dichVuList", dichVuService.search(null).stream()
                .filter(dichVu -> dichVu.getTrangThai() == 1)
                .collect(java.util.stream.Collectors.toList()));
        model.addAttribute("danhSachThanhToan", thanhToanService.getThanhToanByMaDp(maDp));
        BigDecimal tongDaThanhToan = thanhToanService.calculatePaidAmount(maDp);
        BigDecimal tongTien = dp.getTongTien() == null ? BigDecimal.ZERO : dp.getTongTien();
        model.addAttribute("tongTienBooking", tongTien);
        model.addAttribute("tongDaThanhToan", tongDaThanhToan);
        model.addAttribute("tienConLai", tongTien.subtract(tongDaThanhToan).max(BigDecimal.ZERO));

        boolean isAdmin = request.getServletPath().startsWith("/admin/");
        model.addAttribute("isAdminView", isAdmin);
        model.addAttribute("bookingBasePath", isAdmin ? "/admin/dat-phong" : "/staff/don");
        return "staff/chi-tiet-don";
    }

    @PostMapping({"/staff/don/{maDp}/dich-vu", "/admin/dat-phong/{maDp}/dich-vu"})
    public String addService(@PathVariable int maDp, @RequestParam int maDv,
                             @RequestParam int soLuong, @RequestParam(required = false) String ngaySuDung,
                             HttpServletRequest request, HttpSession session,
                             RedirectAttributes redirectAttributes) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        try {
            Date usageDate = ngaySuDung == null || ngaySuDung.trim().isEmpty()
                    ? null : Date.valueOf(ngaySuDung);
            datDichVuService.addForBooking(maDp, maDv, soLuong, usageDate,
                    currentUser == null ? null : currentUser.getMaTk());
            redirectAttributes.addFlashAttribute("successMsg", "Đã thêm dịch vụ vào đơn đặt phòng.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());
        }
        String basePath = request.getServletPath().startsWith("/admin/")
                ? "/admin/dat-phong" : "/staff/don";
        return "redirect:" + basePath + "/" + maDp;
    }

    @PostMapping({"/staff/don/{maDp}/dich-vu/{maDatDv}/xoa",
            "/admin/dat-phong/{maDp}/dich-vu/{maDatDv}/xoa"})
    public String removeService(@PathVariable int maDp, @PathVariable int maDatDv,
                                HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            datDichVuService.removeFromBooking(maDatDv, maDp);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa dịch vụ khỏi đơn đặt phòng.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());
        }
        String basePath = request.getServletPath().startsWith("/admin/")
                ? "/admin/dat-phong" : "/staff/don";
        return "redirect:" + basePath + "/" + maDp;
    }

    // ============== TASK #22, #23, #24: SƠ ĐỒ PHÒNG & CHECK-IN / CHECK-OUT
    // ==============
    // (Note: Cần gọi PhongService của Ý để render sơ đồ phòng)
    /*
     * @GetMapping({"/staff/nhan-tra-phong", "/admin/nhan-tra-phong"})
     * public String roomMap(HttpServletRequest request, Model model) {
     * model.addAttribute("danhSachPhong", phongService.getSoDoPhong());
     * boolean isAdmin = request.getServletPath().startsWith("/admin/");
     * model.addAttribute("isAdminView", isAdmin);
     * model.addAttribute("roomBasePath", isAdmin ? "/admin/nhan-tra-phong" :
     * "/staff/nhan-tra-phong");
     * return "staff/so-do-phong";
     * }
     * 
     * @PostMapping({"/staff/nhan-tra-phong", "/admin/nhan-tra-phong"})
     * public String processCheckInOut(@RequestParam String action, @RequestParam
     * int maDp,
     * HttpSession session, HttpServletRequest request, RedirectAttributes
     * redirectAttributes) {
     * TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
     * boolean success = false;
     * 
     * if ("check_in".equals(action)) {
     * success = datPhongStaffService.updateTrangThaiTransaction(maDp,
     * "DA_NHAN_PHONG", currentUser.getMaTk());
     * redirectAttributes.addFlashAttribute(success ? "successMsg" : "errorMsg",
     * success ? "Check-in thành công!" : "Lỗi Check-in");
     * } else if ("check_out".equals(action)) {
     * success = datPhongStaffService.updateTrangThaiTransaction(maDp,
     * "DA_TRA_PHONG", currentUser.getMaTk());
     * redirectAttributes.addFlashAttribute(success ? "successMsg" : "errorMsg",
     * success ? "Check-out thành công!" : "Lỗi Check-out");
     * }
     * 
     * String basePath = request.getServletPath().startsWith("/admin/") ?
     * "/admin/nhan-tra-phong" : "/staff/nhan-tra-phong";
     * return "redirect:" + basePath;
     * }
     */
}