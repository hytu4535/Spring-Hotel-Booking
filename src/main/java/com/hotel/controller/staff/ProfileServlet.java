package com.hotel.controller.staff;

import com.hotel.model.TaiKhoan;
import com.hotel.service.TaiKhoanService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet để hiển thị và cập nhật hồ sơ cá nhân
 * Truy cập được cho cả ADMIN và NHANVIEN
 */
@WebServlet({"/staff/profile", "/admin/profile"})
public class ProfileServlet extends HttpServlet {

    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        TaiKhoan currentUser = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra đăng nhập
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy thông tin tài khoản hiện tại từ database để có dữ liệu mới nhất
        TaiKhoan user = taiKhoanService.findById(currentUser.getMaTk());
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute("user", user);
        // Also set currentUser for JSP access
        request.setAttribute("currentUser", currentUser);
        setViewPaths(request);
        request.getRequestDispatcher("/WEB-INF/jsp/staff/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        TaiKhoan currentUser = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra đăng nhập
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy dữ liệu từ form
        String hoTen = request.getParameter("hoTen");
        String email = request.getParameter("email");
        String soDienThoai = request.getParameter("soDienThoai");

        // Validate input
        boolean hasError = false;

        if (hoTen == null || hoTen.trim().isEmpty()) {
            hasError = true;
            request.setAttribute("errorHoTen", "Họ tên không được để trống");
        }
        if (email == null || email.trim().isEmpty()) {
            hasError = true;
            request.setAttribute("errorEmail", "Email không được để trống");
        } else if (!email.matches("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
            hasError = true;
            request.setAttribute("errorEmail", "Email không đúng định dạng");
        }
        // Validate phone number: 10 digits, starting with 0
        if (soDienThoai != null && !soDienThoai.isEmpty()) {
            if (!soDienThoai.matches("^0[0-9]{9}$")) {
                hasError = true;
                request.setAttribute("errorSoDienThoai", "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0");
            }
        }

        if (hasError) {
            // Keep the submitted values in request attributes as requested
            request.setAttribute("hoTen", hoTen);
            request.setAttribute("email", email);
            request.setAttribute("soDienThoai", soDienThoai);
            // Set currentUser from session for JSP access
            request.setAttribute("currentUser", currentUser);
            // Lấy lại thông tin người dùng để hiển thị form
            TaiKhoan user = taiKhoanService.findById(currentUser.getMaTk());
            request.setAttribute("user", user);
            setViewPaths(request);
            request.getRequestDispatcher("/WEB-INF/jsp/staff/profile.jsp").forward(request, response);
            return;
        }

        // Cập nhật thông tin
        currentUser.setHoTen(hoTen.trim());
        currentUser.setEmail(email.trim());
        currentUser.setSoDienThoai(soDienThoai != null ? soDienThoai.trim() : null);

        boolean success = taiKhoanService.update(currentUser);

        if (success) {
            // Cập nhật lại session
            session.setAttribute("currentUser", currentUser);
            request.setAttribute("success", "Cập nhật hồ sơ thành công!");
        } else {
            request.setAttribute("error", "Cập nhật hồ sơ thất bại. Vui lòng thử lại!");
        }

        // Lấy lại thông tin để hiển thị
        TaiKhoan user = taiKhoanService.findById(currentUser.getMaTk());
        request.setAttribute("user", user);
        setViewPaths(request);
        request.getRequestDispatcher("/WEB-INF/jsp/staff/profile.jsp").forward(request, response);
    }

    private void setViewPaths(HttpServletRequest request) {
        boolean admin = request.getServletPath().startsWith("/admin/");
        request.setAttribute("isAdminView", admin);
        request.setAttribute("profileBasePath", admin ? "/admin/profile" : "/staff/profile");
    }
}