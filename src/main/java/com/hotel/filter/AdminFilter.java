package com.hotel.filter;

import com.hotel.model.TaiKhoan;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getServletPath();
        if ("/admin".equals(path) || "/admin/login".equals(path)) {
            chain.doFilter(request, response);
            return;
        }
        HttpSession session = req.getSession(false);

        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        if (user == null || !"ADMIN".equals(user.getVaiTro())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }
}
