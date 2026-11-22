package com.hotel.controller.staff;

import com.hotel.model.TaiKhoan;
import com.hotel.model.ThanhToan;
import com.hotel.model.DatPhong;
import com.hotel.repository.DatPhongRepository;
import com.hotel.service.ThanhToanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

@Controller
public class ThanhToanManagementController {

    private final ThanhToanService thanhToanService;
    private final DatPhongRepository datPhongRepository;

    public ThanhToanManagementController(ThanhToanService thanhToanService, DatPhongRepository datPhongRepository) {
        this.thanhToanService = thanhToanService;
        this.datPhongRepository = datPhongRepository;
    }

    @GetMapping({"/staff/dich-vu-thanh-toan/tao", "/admin/thanh-toan/tao"})
    public String createForm(@RequestParam Integer maDp, HttpServletRequest request, Model model,
                             RedirectAttributes redirectAttributes) {
        DatPhong booking = datPhongRepository.findById(maDp).orElse(null);
        if (booking == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy đơn đặt phòng.");
            return "redirect:" + bookingListPath(request);
        }
        model.addAttribute("booking", booking);
        model.addAttribute("isAdminView", request.getServletPath().startsWith("/admin/"));
        model.addAttribute("paymentBasePath", basePath(request));
        return "staff/form-tao-thanh-toan";
    }

    @PostMapping({"/staff/dich-vu-thanh-toan/tao", "/admin/thanh-toan/tao"})
    public String create(@RequestParam Integer maDp, @RequestParam BigDecimal soTien,
                         @RequestParam String loaiThanhToan, @RequestParam String phuongThuc,
                         @RequestParam(required = false) String maGiaoDich,
                         @RequestParam(required = false) String ghiChu,
                         HttpServletRequest request, HttpSession session,
                         RedirectAttributes redirectAttributes) {
        try {
            TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
            thanhToanService.ghiNhanThanhToanThuCong(maDp, soTien, loaiThanhToan,
                    phuongThuc, maGiaoDich, ghiChu, currentUser);
            redirectAttributes.addFlashAttribute("successMsg", "Đã ghi nhận thanh toán tại quầy.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());
        }
        return "redirect:" + bookingListPath(request);
    }

    @GetMapping({"/staff/dich-vu-thanh-toan/{maTt}",
            "/admin/thanh-toan/{maTt}"})
    public String detail(@PathVariable int maTt, HttpServletRequest request, Model model,
                         RedirectAttributes redirectAttributes) {
        ThanhToan payment = thanhToanService.getById(maTt);
        if (payment == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy giao dịch thanh toán.");
            return "redirect:" + bookingListPath(request);
        }
        model.addAttribute("payment", payment);
        model.addAttribute("isAdminView", request.getServletPath().startsWith("/admin/"));
        model.addAttribute("paymentBasePath", basePath(request));
        return "staff/chi-tiet-thanh-toan";
    }

    @GetMapping({"/staff/dich-vu-thanh-toan/{maTt}/sua",
            "/admin/thanh-toan/{maTt}/sua"})
    public String edit(@PathVariable int maTt, HttpServletRequest request, Model model,
                       RedirectAttributes redirectAttributes) {
        ThanhToan payment = thanhToanService.getById(maTt);
        if (payment == null || "VNPAY".equalsIgnoreCase(payment.getPhuongThuc())) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    payment == null ? "Không tìm thấy giao dịch thanh toán." : "Không được sửa giao dịch VNPay.");
            return "redirect:" + bookingListPath(request);
        }
        model.addAttribute("payment", payment);
        model.addAttribute("isAdminView", request.getServletPath().startsWith("/admin/"));
        model.addAttribute("paymentBasePath", basePath(request));
        return "staff/form-thanh-toan";
    }

    @PostMapping({"/staff/dich-vu-thanh-toan/{maTt}/sua",
            "/admin/thanh-toan/{maTt}/sua"})
    public String update(@PathVariable int maTt, @RequestParam BigDecimal soTien,
                         @RequestParam String loaiThanhToan, @RequestParam String phuongThuc,
                         @RequestParam(required = false) String maGiaoDich,
                         @RequestParam(required = false) String ghiChu,
                         HttpServletRequest request, HttpSession session,
                         RedirectAttributes redirectAttributes) {
        try {
            TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
            thanhToanService.updateManualPayment(maTt, soTien, loaiThanhToan, phuongThuc,
                    maGiaoDich, ghiChu, currentUser);
            redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật giao dịch tại quầy.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());
        }
        return "redirect:" + bookingListPath(request);
    }

    private String basePath(HttpServletRequest request) {
        return request.getServletPath().startsWith("/admin/")
                ? "/admin/thanh-toan" : "/staff/dich-vu-thanh-toan";
    }

    private String bookingListPath(HttpServletRequest request) {
        return request.getServletPath().startsWith("/admin/")
                ? "/admin/dat-phong" : "/staff/don";
    }
}
