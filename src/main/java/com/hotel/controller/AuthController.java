package com.hotel.controller;

import com.hotel.dto.RegisterRequest;
import com.hotel.model.TaiKhoan;
import com.hotel.service.AuthService;
import com.hotel.service.TaiKhoanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Locale;

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
            model.addAttribute("adminMode", false);
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
            model.addAttribute("tenDangNhap", tenDangNhap);
            return "staff/login";
        }
        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();
        request.getSession(true).setAttribute("currentUser", user);
        return "redirect:" + destination(user);
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult result, Model model) {
        if (request.getMatKhau() != null
                && !request.getMatKhau().equals(request.getXacNhanMatKhau())) {
            result.rejectValue("xacNhanMatKhau", "password.mismatch", "Mật khẩu xác nhận không khớp.");
        }
        if (result.hasErrors()) {
            return "register";
        }
        try {
            authService.register(request);
            model.addAttribute("adminMode", false);
            model.addAttribute("success", "Đăng ký thành công. Vui lòng đăng nhập.");
            model.addAttribute("tenDangNhap", request.getTenDangNhap());
            return "staff/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
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
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    private String destination(TaiKhoan user) {
        String role = user.getVaiTro() == null ? "" : user.getVaiTro().trim().toUpperCase(Locale.ROOT);
        if ("ADMIN".equals(role)) return "/admin/dashboard";
        if ("CUSTOMER".equals(role)) return "/";
        return "/staff/dashboard";
    }
}
