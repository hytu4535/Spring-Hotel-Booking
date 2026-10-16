package com.hotel.controller.staff;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hotel.model.TaiKhoan;
import java.io.IOException;

/**
 * Servlet để hiển thị trang chính của nhân viên
 * Đây là landing page sau khi nhân viên đăng nhập
 */
@WebServlet("/staff/quan-ly-don")
public class QuanLyDonServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra đăng nhập và vai trò
        if (user == null ||
                (!"ADMIN".equals(user.getVaiTro()) && !"NHANVIEN".equals(user.getVaiTro()))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Forward tới trang JSP cho nhân viên
        request.getRequestDispatcher("/WEB-INF/jsp/staff/quanLyDon.jsp").forward(request, response);
    }
}