package com.hotel.filter;

import com.hotel.model.TaiKhoan;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

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
        if (user == null ||
                (!"ADMIN".equals(user.getVaiTro()) && !"NHANVIEN".equals(user.getVaiTro()))) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }
}
