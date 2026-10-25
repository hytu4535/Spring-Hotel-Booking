package com.hotel.controller;

import com.hotel.model.TaiKhoan;
import com.hotel.service.AuthService;
import com.hotel.service.TaiKhoanService;
import com.hotel.util.PasswordUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class AuthController {
    private final AuthService authService;
    private final TaiKhoanService taiKhoanService;

    public AuthController(AuthService authService, TaiKhoanService taiKhoanService) {
        this.authService = authService;
        this.taiKhoanService = taiKhoanService;
    }

    @GetMapping({"/login", "/admin", "/admin/login"})
    public String loginForm(HttpServletRequest request,
                            @RequestParam(required = false) String oauthError, Model model) {
        model.addAttribute("adminMode", request.getServletPath().startsWith("/admin"));
        if (oauthError != null) model.addAttribute("error", oauthError);
        return "staff/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String tenDangNhap, @RequestParam String matKhau,
                        HttpServletRequest request, Model model) {
        TaiKhoan user = authService.login(tenDangNhap, matKhau);
        if (user == null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
            model.addAttribute("tenDangNhap", tenDangNhap);
            return "staff/login";
        }
        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();
        request.getSession(true).setAttribute("currentUser", user);
        return "redirect:" + destination(user);
    }

    @PostMapping({"/admin", "/admin/login"})
    public String adminLogin(@RequestParam String tenDangNhap, @RequestParam String matKhau,
                             HttpSession session, Model model) {
        TaiKhoan user = authService.login(tenDangNhap, matKhau);
        if (user == null || "CUSTOMER".equals(user.getVaiTro())) {
            model.addAttribute("adminMode", true);
            model.addAttribute("error", "Tài khoản không có quyền quản trị.");
            return "staff/login";
        }
        session.setAttribute("currentUser", user);
        return "redirect:" + destination(user);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private String destination(TaiKhoan user) {
        if ("ADMIN".equals(user.getVaiTro())) return "/admin/dashboard";
        if ("CUSTOMER".equals(user.getVaiTro())) return "/";
        return "/staff/quan-ly-don";
    }
}
