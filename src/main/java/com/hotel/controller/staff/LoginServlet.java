package com.hotel.controller.staff;

import com.hotel.model.TaiKhoan;
import com.hotel.service.AuthService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;
        if (user != null) {
            redirectByRole(request, response, user);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/jsp/staff/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tenDangNhap = request.getParameter("tenDangNhap");
        String matKhau = request.getParameter("matKhau");

        TaiKhoan tk = authService.login(tenDangNhap, matKhau);
        if (tk == null) {
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng, hoặc tài khoản đã bị khóa.");
            request.setAttribute("tenDangNhap", tenDangNhap);
            request.getRequestDispatcher("/WEB-INF/jsp/staff/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", tk);
        redirectByRole(request, response, tk);
    }

    private void redirectByRole(HttpServletRequest request, HttpServletResponse response, TaiKhoan user)
            throws IOException {
        String context = request.getContextPath();
        if ("ADMIN".equals(user.getVaiTro())) {
            response.sendRedirect(context + "/admin/dashboard");
        } else {
            response.sendRedirect(context + "/staff/quan-ly-don");
        }
    }
}
