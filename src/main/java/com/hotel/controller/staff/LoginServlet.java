package com.hotel.controller.staff;

import com.hotel.model.TaiKhoan;
import com.hotel.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

public class LoginServlet extends HttpServlet {

    @Autowired
    private AuthService authService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;
        if (user != null) {
            redirectByRole(request, response, user);
            return;
        }

        request.setAttribute("adminMode", false);
        request.getRequestDispatcher("/WEB-INF/jsp/staff/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tenDangNhap = request.getParameter("tenDangNhap");
        String matKhau = request.getParameter("matKhau");

        TaiKhoan tk = authService.login(tenDangNhap, matKhau);
        if (tk == null || !"CUSTOMER".equals(tk.getVaiTro())) {
            request.setAttribute("error", "Tên đăng nhập không đúng hoặc tài khoản không có quyền khách hàng.");
            request.setAttribute("tenDangNhap", tenDangNhap);
            request.getRequestDispatcher("/WEB-INF/jsp/staff/login.jsp").forward(request, response);
            return;
        }

        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
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
        } else if ("CUSTOMER".equals(user.getVaiTro())) {
            response.sendRedirect(context + "/customer/profile");
        } else {
            response.sendRedirect(context + "/staff/quan-ly-don");
        }
    }
}
