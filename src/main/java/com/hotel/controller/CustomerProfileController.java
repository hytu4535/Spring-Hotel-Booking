package com.hotel.controller;

import com.hotel.dto.KhachHangProfileDTO;
import com.hotel.model.KhachHang;
import com.hotel.model.TaiKhoan;
import com.hotel.service.KhachHangService;
import com.hotel.service.DatPhongService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class CustomerProfileController {
    private final KhachHangService khachHangService;
    private final DatPhongService datPhongService;

    public CustomerProfileController(KhachHangService khachHangService, DatPhongService datPhongService) {
        this.khachHangService = khachHangService;
        this.datPhongService = datPhongService;
    }

    @GetMapping("/customer/profile")
    public String profile(HttpSession session, Model model) {
        TaiKhoan account = currentCustomer(session);
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
            BindingResult result, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        TaiKhoan account = currentCustomer(session);
        if (account == null)
            return "redirect:/login";
        if (result.hasErrors())
            return "customer/profile";
        try {
            KhachHang customer = khachHangService.updateProfile(account.getMaTk(), profile);
            account.setHoTen(customer.getHoTen());
            account.setEmail(customer.getEmail());
            account.setSoDienThoai(customer.getSoDienThoai());
            session.setAttribute("currentUser", account);
            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công.");
            return "redirect:/customer/profile";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("globalError", ex.getMessage());
            return "customer/profile";
        }
    }

    @GetMapping("/customer/lich-su-dat-phong")
    public String bookingHistory(HttpSession session, Model model) {
        TaiKhoan account = currentCustomer(session);
        if (account == null)
            return "redirect:/login";
        KhachHang customer = khachHangService.getForAccount(account.getMaTk());
        if (customer == null) {
            model.addAttribute("historyList", List.of());
            return "customer/lich-su-dat-phong";
        }
        model.addAttribute("historyList", khachHangService.history(customer.getMaKh()));
        return "customer/lich-su-dat-phong";
    }

    @PostMapping("/customer/lich-su-dat-phong/huy")
    public String cancelBooking(@org.springframework.web.bind.annotation.RequestParam int maDp,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        TaiKhoan account = currentCustomer(session);
        if (account == null)
            return "redirect:/login";
        boolean canceled = datPhongService.huyDonPhongCuaKhach(maDp, account.getMaTk());
        if (canceled) {
            redirectAttributes.addFlashAttribute("success", "Đã hủy đơn đặt phòng thành công.");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Không thể hủy đơn. Đơn không thuộc tài khoản này hoặc đã được xử lý.");
        }
        return "redirect:/customer/lich-su-dat-phong";
    }

    private TaiKhoan currentCustomer(HttpSession session) {
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
