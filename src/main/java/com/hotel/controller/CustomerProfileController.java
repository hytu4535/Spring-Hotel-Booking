// --- START OF FILE CustomerProfileController.java ---
package com.hotel.controller;

import com.hotel.dto.KhachHangProfileDTO;
import com.hotel.model.KhachHang;
import com.hotel.model.TaiKhoan;
import com.hotel.service.KhachHangService;
import com.hotel.service.DatPhongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Collections;

@Controller
public class CustomerProfileController {

    private final KhachHangService khachHangService;
    private final DatPhongService datPhongService; // Bổ sung DatPhongService để lấy lịch sử và xử lý hủy

    // Cập nhật lại constructor để inject DatPhongService
    public CustomerProfileController(KhachHangService khachHangService, DatPhongService datPhongService) {
        this.khachHangService = khachHangService;
        this.datPhongService = datPhongService;
    }

    // ==========================================
    // 1. QUẢN LÝ HỒ SƠ CÁ NHÂN (Cũ - Giữ nguyên)
    // ==========================================
    @GetMapping("/customer/profile")
    public String profile(HttpServletRequest request, Model model) {
        TaiKhoan account = currentCustomer(request);
        if (account == null)
            return "redirect:/login";
        KhachHang customer = khachHangService.getForAccount(account.getMaTk());
        if (customer == null) {
            customer = new KhachHang();
            customer.setHoTen(account.getHoTen());
            customer.setEmail(account.getEmail());
            customer.setSoDienThoai(account.getSoDienThoai());
        }
        model.addAttribute("profile", toDto(customer));
        return "customer/profile";
    }

    @PostMapping("/customer/profile")
    public String update(@Valid @ModelAttribute("profile") KhachHangProfileDTO profile,
            BindingResult result, HttpServletRequest request, Model model,
            RedirectAttributes redirectAttributes) {
        TaiKhoan account = currentCustomer(request);
        if (account == null)
            return "redirect:/login";
        if (result.hasErrors())
            return "customer/profile";
        try {
            KhachHang customer = khachHangService.updateProfile(account.getMaTk(), profile);
            account.setHoTen(customer.getHoTen());
            account.setEmail(customer.getEmail());
            account.setSoDienThoai(customer.getSoDienThoai());
            request.getSession(false).setAttribute("currentUser", account);
            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công.");
            return "redirect:/customer/profile";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("globalError", ex.getMessage());
            return "customer/profile";
        }
    }

    // ==========================================
    // 2. LỊCH SỬ ĐẶT PHÒNG & HỦY ĐƠN (Thêm Mới)
    // ==========================================

    // TASK #12: Xem lịch sử đặt phòng
    @GetMapping("/customer/lich-su-dat-phong")
    public String bookingHistory(HttpServletRequest request, Model model) {
        TaiKhoan account = currentCustomer(request);
        if (account == null)
            return "redirect:/login";

        KhachHang customer = khachHangService.getForAccount(account.getMaTk());
        if (customer != null) {
            // Lấy danh sách lịch sử truyền ra View (biến historyList)
            model.addAttribute("historyList", khachHangService.history(customer.getMaKh()));
        } else {
            // Nếu chưa có hồ sơ khách hàng thì gửi list rỗng
            model.addAttribute("historyList", Collections.emptyList());
        }

        return "customer/lich-su-dat-phong";
    }

    // TASK #13: Khách hàng tự hủy đơn
    @PostMapping("/customer/lich-su-dat-phong/huy")
    public String cancelBooking(@RequestParam int maDp, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        TaiKhoan account = currentCustomer(request);
        if (account == null)
            return "redirect:/login";

        try {
            // Gọi hàm hủy đơn bên DatPhongService (đã có tích hợp bắn event Kafka ở trong)
            datPhongService.huyDonPhong(maDp);
            redirectAttributes.addFlashAttribute("success", "Đã hủy đơn đặt phòng thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi hủy đơn: " + e.getMessage());
        }

        return "redirect:/customer/lich-su-dat-phong";
    }

    // ==========================================
    // UTILS
    // ==========================================
    private TaiKhoan currentCustomer(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        TaiKhoan account = session == null ? null : (TaiKhoan) session.getAttribute("currentUser");
        return account != null && "CUSTOMER".equals(account.getVaiTro()) ? account : null;
    }

    private KhachHangProfileDTO toDto(KhachHang customer) {
        KhachHangProfileDTO dto = new KhachHangProfileDTO();
        dto.setHoTen(customer.getHoTen());
        dto.setEmail(customer.getEmail());
        dto.setSoDienThoai(customer.getSoDienThoai());
        dto.setCccd(customer.getCccd());
        dto.setDiaChi(customer.getDiaChi());
        return dto;
    }
}