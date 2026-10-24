package com.hotel.controller;

import com.hotel.model.TaiKhoan;
import com.hotel.service.TaiKhoanService;
import com.hotel.util.PasswordUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class StaffAccountController {
    private final TaiKhoanService service;
    public StaffAccountController(TaiKhoanService service) { this.service = service; }

    @GetMapping({"/profile", "/staff/profile", "/admin/profile"})
    public String profile(HttpServletRequest request, Model model) {
        TaiKhoan user = staffUser(request);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", service.findById(user.getMaTk()));
        return isAdminPath(request) ? "admin/profile" : "staff/profile";
    }

    @PostMapping({"/profile", "/staff/profile", "/admin/profile"})
    public String update(@RequestParam String hoTen, @RequestParam String email,
                         @RequestParam(required = false) String soDienThoai,
                         HttpServletRequest request, Model model) {
        TaiKhoan user = staffUser(request);
        if (user == null) return "redirect:/login";
        user.setHoTen(hoTen.trim());
        user.setEmail(email.trim());
        user.setSoDienThoai(soDienThoai == null ? null : soDienThoai.trim());
        if (service.update(user)) {
            request.getSession().setAttribute("currentUser", user);
            model.addAttribute("success", "Cập nhật hồ sơ thành công!");
        } else model.addAttribute("error", "Cập nhật hồ sơ thất bại.");
        model.addAttribute("user", user);
        return isAdminPath(request) ? "admin/profile" : "staff/profile";
    }

    @GetMapping({"/doi-mat-khau", "/staff/doi-mat-khau", "/admin/doi-mat-khau"})
    public String passwordForm(HttpServletRequest request) {
        return staffUser(request) == null ? "redirect:/login"
                : (isAdminPath(request) ? "admin/changePassword" : "staff/changePassword");
    }

    @PostMapping({"/doi-mat-khau", "/staff/doi-mat-khau", "/admin/doi-mat-khau"})
    public String changePassword(@RequestParam String matKhauCu, @RequestParam String matKhauMoi,
                                 @RequestParam String xacNhanMatKhauMoi,
                                 HttpServletRequest request, Model model) {
        TaiKhoan user = staffUser(request);
        if (user == null) return "redirect:/login";
        if (!matKhauMoi.equals(xacNhanMatKhauMoi) || matKhauMoi.length() < 6
                || !PasswordUtil.checkPassword(matKhauCu, user.getMatKhau())) {
            model.addAttribute("error", "Mật khẩu cũ không đúng hoặc mật khẩu mới không hợp lệ.");
            return isAdminPath(request) ? "admin/changePassword" : "staff/changePassword";
        }
        user.setMatKhau(PasswordUtil.hash(matKhauMoi));
        service.update(user);
        request.getSession().setAttribute("currentUser", user);
        model.addAttribute("success", "Đổi mật khẩu thành công.");
        return isAdminPath(request) ? "admin/changePassword" : "staff/changePassword";
    }

    private TaiKhoan staffUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        TaiKhoan user = session == null ? null : (TaiKhoan) session.getAttribute("currentUser");
        return user != null && ("ADMIN".equals(user.getVaiTro()) || "NHANVIEN".equals(user.getVaiTro()))
                ? user : null;
    }

    private boolean isAdminPath(HttpServletRequest request) {
        return request.getServletPath().startsWith("/admin/");
    }
}
