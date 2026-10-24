package com.hotel.controller.staff;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

import com.hotel.model.TaiKhoan;

/**
 * Servlet để hiển thị trang chính của nhân viên
 * Đây là landing page sau khi nhân viên đăng nhập
 */
@Controller
public class QuanLyDonServlet {

    @GetMapping("/staff/quan-ly-don")
    public String doGet(HttpSession session) {
        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra đăng nhập và vai trò
        if (user == null ||
                (!"ADMIN".equals(user.getVaiTro()) && !"NHANVIEN".equals(user.getVaiTro()))) {
            return "redirect:/login";

        }

        // Forward tới trang JSP cho nhân viên
        return "staff/quanLyDon";
    }
}