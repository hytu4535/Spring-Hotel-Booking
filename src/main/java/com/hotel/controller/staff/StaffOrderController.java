package com.hotel.controller.staff;

import com.hotel.model.TaiKhoan;
import com.hotel.service.DatPhongStaffService;
import com.hotel.service.PhongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class StaffOrderController {

    private final DatPhongStaffService datPhongStaffService;
    // PhongService của Ý làm, bạn chỉ cần gọi hàm getSoDoPhong() của nó.
    // private final PhongService phongService;

    public StaffOrderController(DatPhongStaffService datPhongStaffService) {
        this.datPhongStaffService = datPhongStaffService;
    }

    // ============== TASK #19 (B): DASHBOARD NHÂN VIÊN (Dữ liệu giả lập giao diện)
    // ==============
    @GetMapping("/staff/quan-ly-don")
    public String staffDashboard() {
        return "staff/quanLyDon";
    }

    // ============== TASK #20, #21, #36: QUẢN LÝ ĐƠN ĐẶT PHÒNG ==============
    @GetMapping({ "/staff/don", "/admin/dat-phong" })
    public String listOrders(HttpServletRequest request, Model model) {
        model.addAttribute("danhSachDon", datPhongStaffService.findAllWithKhachHang());
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