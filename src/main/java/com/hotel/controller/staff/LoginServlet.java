package com.hotel.controller.staff;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.model.TaiKhoan;
import com.hotel.service.AuthService;
import javax.servlet.http.HttpSession;

public class LoginServlet {

    private final AuthService authService;

    public LoginServlet(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String doGet(HttpSession session, Model model) {
        TaiKhoan user = (TaiKhoan) session.getAttribute("currentUser");
        if (user != null) {
            return redirectByRole(user);
        }

        model.addAttribute("adminMode", false);
        return "staff/login";
    }

    @PostMapping("/login")
    public String doPost(@RequestParam String tenDangNhap, @RequestParam String matKhau,
                         HttpSession session, Model model) {

        TaiKhoan tk = authService.login(tenDangNhap, matKhau);
        if (tk == null || !"CUSTOMER".equals(tk.getVaiTro())) {
            model.addAttribute("error", "Tên đăng nhập không đúng hoặc tài khoản không có quyền khách hàng.");
            model.addAttribute("tenDangNhap", tenDangNhap);
            return "staff/login";
        }

        session.setAttribute("currentUser", tk);
        return redirectByRole(tk);
    }

    private String redirectByRole(TaiKhoan user) {
        if ("ADMIN".equals(user.getVaiTro())) {
            return "redirect:/admin/dashboard";
        } else if ("CUSTOMER".equals(user.getVaiTro())) {
            return "redirect:/customer/profile";
        } else {
            return "redirect:/staff/quan-ly-don";
        }
    }
}
