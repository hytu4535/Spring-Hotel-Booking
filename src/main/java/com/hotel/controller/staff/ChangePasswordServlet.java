package com.hotel.controller.staff;

import com.hotel.model.TaiKhoan;
import com.hotel.service.TaiKhoanService;
import com.hotel.util.PasswordUtil;
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
 * Servlet để đổi mật khẩu
 * Truy cập được cho cả ADMIN và NHANVIEN
 */
@WebServlet("/staff/doi-mat-khau")
public class ChangePasswordServlet extends HttpServlet {

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

        request.getRequestDispatcher("/WEB-INF/jsp/staff/changePassword.jsp").forward(request, response);
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
        String matKhauCu = request.getParameter("matKhauCu");
        String matKhauMoi = request.getParameter("matKhauMoi");
        String xacNhanMatKhauMoi = request.getParameter("xacNhanMatKhauMoi");

        // Validate input
        boolean hasError = false;
        Map<String, String> errorMessages = new HashMap<>();

        if (matKhauCu == null || matKhauCu.trim().isEmpty()) {
            hasError = true;
            errorMessages.put("matKhauCu", "Mật khẩu cũ không được để trống");
            request.setAttribute("errorMatKhauCu", "Mật khẩu cũ không được để trống");
        } else if (matKhauMoi == null || matKhauMoi.trim().isEmpty()) {
            hasError = true;
            errorMessages.put("matKhauMoi", "Mật khẩu mới không được để trống");
            request.setAttribute("errorMatKhauMoi", "Mật khẩu mới không được để trống");
        } else if (xacNhanMatKhauMoi == null || xacNhanMatKhauMoi.trim().isEmpty()) {
            hasError = true;
            errorMessages.put("xacNhanMatKhauMoi", "Vui lòng xác nhận mật khẩu mới");
            request.setAttribute("errorXacNhanMatKhauMoi", "Vui lòng xác nhận mật khẩu mới");
        } else if (!matKhauMoi.equals(xacNhanMatKhauMoi)) {
            hasError = true;
            errorMessages.put("matKhauMoi", "Mật khẩu mới và xác nhận mật khẩu mới không khớp");
            errorMessages.put("xacNhanMatKhauMoi", "Mật khẩu mới và xác nhận mật khẩu mới không khớp");
            request.setAttribute("errorMatKhauMoi", "Mật khẩu mới và xác nhận mật khẩu mới không khớp");
            request.setAttribute("errorXacNhanMatKhauMoi", "Mật khẩu mới và xác nhận mật khẩu mới không khớp");
        } else if (matKhauMoi.length() < 6) {
            hasError = true;
            errorMessages.put("matKhauMoi", "Mật khẩu mới phải có ít nhất 6 ký tự");
            request.setAttribute("errorMatKhauMoi", "Mật khẩu mới phải có ít nhất 6 ký tự");
        }

        // Kiểm tra mật khẩu cũ
        if (!hasError && !PasswordUtil.checkPassword(matKhauCu, currentUser.getMatKhau())) {
            hasError = true;
            errorMessages.put("matKhauCu", "Mật khẩu cũ không đúng");
            request.setAttribute("errorMatKhauCu", "Mật khẩu cũ không đúng");
        }

        if (hasError) {
            request.setAttribute("errorMessages", errorMessages);
            // Also set general error for backward compatibility
            request.setAttribute("error", "Vui lòng sửa các lỗi được đánh dấu bên dưới.");
            // Keep the submitted values in request attributes as requested
            request.setAttribute("matKhauCu", matKhauCu);
            request.setAttribute("matKhauMoi", matKhauMoi);
            request.setAttribute("xacNhanMatKhauMoi", xacNhanMatKhauMoi);
            request.getRequestDispatcher("/WEB-INF/jsp/staff/changePassword.jsp").forward(request, response);
            return;
        }

        // Cập nhật mật khẩu mới (đã được hash)
        String hashedPassword = PasswordUtil.hash(matKhauMoi.trim());
        currentUser.setMatKhau(hashedPassword);

        boolean success = taiKhoanService.update(currentUser);

        if (success) {
            // Cập nhật lại session với thông tin mới
            session.setAttribute("currentUser", currentUser);
            request.setAttribute("success", "Đổi mật khẩu thành công! Bạn có thể tiếp tục làm việc hoặc đăng nhập lại bằng mật khẩu mới để đảm bảo bảo mật.");
        } else {
            request.setAttribute("error", "Đổi mật khẩu thất bại. Vui lòng thử lại!");
        }

        // Hiển thị lại form đổi mật khẩu
        request.getRequestDispatcher("/WEB-INF/jsp/staff/changePassword.jsp").forward(request, response);
    }
}