package com.hotel.controller;

import com.hotel.model.TaiKhoan;
import com.hotel.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Servlet chung để hiển thị và cập nhật hồ sơ cá nhân
 * Truy cập được cho cả ADMIN và NHANVIEN
 */
public class ProfileCommonServlet extends HttpServlet {

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        TaiKhoan currentUser = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra đăng nhập và quyền (ADMIN hoặc NHANVIEN)
        if (currentUser == null ||
                (!"ADMIN".equals(currentUser.getVaiTro()) && !"NHANVIEN".equals(currentUser.getVaiTro()))) {
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
        request.getRequestDispatcher("/WEB-INF/jsp/staff/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        TaiKhoan currentUser = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra đăng nhập và quyền (ADMIN hoặc NHANVIEN)
        if (currentUser == null ||
                (!"ADMIN".equals(currentUser.getVaiTro()) && !"NHANVIEN".equals(currentUser.getVaiTro()))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy dữ liệu từ form
        String hoTen = request.getParameter("hoTen");
        String email = request.getParameter("email");
        String soDienThoai = request.getParameter("soDienThoai");

        // Validate input
        boolean hasError = false;
        Map<String, String> errorMessages = new HashMap<>();

        if (hoTen == null || hoTen.trim().isEmpty()) {
            hasError = true;
            errorMessages.put("hoTen", "Họ tên không được để trống");
            request.setAttribute("errorHoTen", "Họ tên không được để trống");
        }
        if (email == null || email.trim().isEmpty()) {
            hasError = true;
            errorMessages.put("email", "Email không được để trống");
            request.setAttribute("errorEmail", "Email không được để trống");
        } else if (!email.matches("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
            hasError = true;
            errorMessages.put("email", "Email không đúng định dạng");
            request.setAttribute("errorEmail", "Email không đúng định dạng");
        }
        // Validate phone number: 10 digits, starting with 0
        if (soDienThoai != null && !soDienThoai.isEmpty()) {
            if (!soDienThoai.matches("^0[0-9]{9}$")) {
                hasError = true;
                errorMessages.put("soDienThoai", "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0");
                request.setAttribute("errorSoDienThoai", "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0");
            }
        }

        if (hasError) {
            request.setAttribute("errorMessages", errorMessages);
            // Also set general error for backward compatibility
            request.setAttribute("error", "Vui lòng sửa các lỗi được đánh dấu bên dưới.");
            // Keep the submitted values in request attributes as requested
            request.setAttribute("hoTen", hoTen);
            request.setAttribute("email", email);
            request.setAttribute("soDienThoai", soDienThoai);
            // Lấy lại thông tin người dùng để hiển thị form
            TaiKhoan user = taiKhoanService.findById(currentUser.getMaTk());
            request.setAttribute("user", user);
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
        request.getRequestDispatcher("/WEB-INF/jsp/staff/profile.jsp").forward(request, response);
    }
}