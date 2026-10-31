package com.hotel.controller.staff;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.model.TaiKhoan;
import com.hotel.service.AuthService;

import javax.servlet.http.HttpSession;

public class AdminLoginServlet {

    private final AuthService authService;

    public AdminLoginServlet(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping({"/admin", "/admin/login"})
    public String doGet(HttpSession session, Model model) {
        TaiKhoan user = session == null ? null : (TaiKhoan) session.getAttribute("currentUser");
        if (user != null && !"CUSTOMER".equals(user.getVaiTro())) {
            return redirectByRole(user);
        }
        model.addAttribute("adminMode", true);
        return "staff/login";
    }

    @PostMapping({"/admin", "/admin/login"})
    public String doPost(@RequestParam String tenDangNhap, @RequestParam String matKhau,
                         HttpSession session, Model model) {
        TaiKhoan user = authService.login(tenDangNhap, matKhau);
        if (user == null || "CUSTOMER".equals(user.getVaiTro())) {
            model.addAttribute("adminMode", true);
            model.addAttribute("tenDangNhap", tenDangNhap);
            model.addAttribute("error", "Tên đăng nhập không đúng hoặc tài khoản không có quyền nhân viên/quản trị.");
            return "staff/login";
        }

        session.setAttribute("currentUser", user);
        return redirectByRole(user);
    }

    private String redirectByRole(TaiKhoan user) {
        if ("ADMIN".equals(user.getVaiTro())) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/staff/quan-ly-don";
        }
    }
}
