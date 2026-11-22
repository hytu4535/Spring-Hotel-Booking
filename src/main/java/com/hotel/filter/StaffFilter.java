package com.hotel.filter;

import com.hotel.model.TaiKhoan;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Locale;

@WebFilter("/staff/*")
public class StaffFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Cho phép cả ADMIN và NHANVIEN vào khu vực staff
        String role = user == null || user.getVaiTro() == null
                ? ""
                : user.getVaiTro().trim().toUpperCase(Locale.ROOT);
        boolean staffRole = "NHANVIEN".equals(role) || "STAFF".equals(role) || "EMPLOYEE".equals(role);
        if (user == null || (!"ADMIN".equals(role) && !staffRole)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }
}
