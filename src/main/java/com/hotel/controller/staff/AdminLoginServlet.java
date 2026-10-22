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

@WebServlet({"/admin", "/admin/login"})
public class AdminLoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        TaiKhoan user = session == null ? null : (TaiKhoan) session.getAttribute("currentUser");
        if (user != null && !"CUSTOMER".equals(user.getVaiTro())) {
            redirectByRole(request, response, user);
            return;
        }
        request.setAttribute("adminMode", true);
        request.getRequestDispatcher("/WEB-INF/jsp/staff/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("tenDangNhap");
        TaiKhoan user = authService.login(username, request.getParameter("matKhau"));
        if (user == null || "CUSTOMER".equals(user.getVaiTro())) {
            request.setAttribute("adminMode", true);
            request.setAttribute("tenDangNhap", username);
            request.setAttribute("error", "Tên đăng nhập không đúng hoặc tài khoản không có quyền nhân viên/quản trị.");
            request.getRequestDispatcher("/WEB-INF/jsp/staff/login.jsp").forward(request, response);
            return;
        }

        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        request.getSession(true).setAttribute("currentUser", user);
        redirectByRole(request, response, user);
    }

    private void redirectByRole(HttpServletRequest request, HttpServletResponse response,
                                TaiKhoan user) throws IOException {
        String context = request.getContextPath();
        if ("ADMIN".equals(user.getVaiTro())) {
            response.sendRedirect(context + "/admin/dashboard");
        } else {
            response.sendRedirect(context + "/staff/quan-ly-don");
        }
    }
}
