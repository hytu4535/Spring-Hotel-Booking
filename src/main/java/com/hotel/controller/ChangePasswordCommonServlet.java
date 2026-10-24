package com.hotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.hotel.model.TaiKhoan;
import com.hotel.service.TaiKhoanService;
import com.hotel.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Servlet chung để đổi mật khẩu
 * Truy cập được cho cả ADMIN và NHANVIEN
 */
@Controller
public class ChangePasswordCommonServlet {

    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    @GetMapping("/doi-mat-khau")
    public String doGet(HttpSession session, Model model) {
        TaiKhoan currentUser = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra đăng nhập và quyền (ADMIN hoặc NHANVIEN)
        if (currentUser == null ||
                (!"ADMIN".equals(currentUser.getVaiTro()) && !"NHANVIEN".equals(currentUser.getVaiTro()))) {
            return "redirect:/login";

        }

        setViewPaths(model);
        return "staff/changePassword";
    }

    @PostMapping("/doi-mat-khau")
    public String doPost(@RequestParam(required = false) String matKhauCu,
                         @RequestParam(required = false) String matKhauMoi,
                         @RequestParam(required = false) String xacNhanMatKhauMoi,
                         HttpSession session, Model model) {
        TaiKhoan currentUser = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra đăng nhập và quyền (ADMIN hoặc NHANVIEN)
        if (currentUser == null ||
                (!"ADMIN".equals(currentUser.getVaiTro()) && !"NHANVIEN".equals(currentUser.getVaiTro()))) {
            return "redirect:/login";

        }

        // Lấy dữ liệu từ form

        // Validate input
        boolean hasError = false;
        Map<String, String> errorMessages = new HashMap<>();

        if (matKhauCu == null || matKhauCu.trim().isEmpty()) {
            hasError = true;
            errorMessages.put("matKhauCu", "Mật khẩu cũ không được để trống");
            model.addAttribute("errorMatKhauCu", "Mật khẩu cũ không được để trống");
        } else if (matKhauMoi == null || matKhauMoi.trim().isEmpty()) {
            hasError = true;
            errorMessages.put("matKhauMoi", "Mật khẩu mới không được để trống");
            model.addAttribute("errorMatKhauMoi", "Mật khẩu mới không được để trống");
        } else if (xacNhanMatKhauMoi == null || xacNhanMatKhauMoi.trim().isEmpty()) {
            hasError = true;
            errorMessages.put("xacNhanMatKhauMoi", "Vui lòng xác nhận mật khẩu mới");
            model.addAttribute("errorXacNhanMatKhauMoi", "Vui lòng xác nhận mật khẩu mới");
        } else if (!matKhauMoi.equals(xacNhanMatKhauMoi)) {
            hasError = true;
            errorMessages.put("matKhauMoi", "Mật khẩu mới và xác nhận mật khẩu mới không khớp");
            errorMessages.put("xacNhanMatKhauMoi", "Mật khẩu mới và xác nhận mật khẩu mới không khớp");
            model.addAttribute("errorMatKhauMoi", "Mật khẩu mới và xác nhận mật khẩu mới không khớp");
            model.addAttribute("errorXacNhanMatKhauMoi", "Mật khẩu mới và xác nhận mật khẩu mới không khớp");
        } else if (matKhauMoi.length() < 6) {
            hasError = true;
            errorMessages.put("matKhauMoi", "Mật khẩu mới phải có ít nhất 6 ký tự");
            model.addAttribute("errorMatKhauMoi", "Mật khẩu mới phải có ít nhất 6 ký tự");
        }

        // Kiểm tra mật khẩu cũ
        if (!hasError && !PasswordUtil.checkPassword(matKhauCu, currentUser.getMatKhau())) {
            hasError = true;
            errorMessages.put("matKhauCu", "Mật khẩu cũ không đúng");
            model.addAttribute("errorMatKhauCu", "Mật khẩu cũ không đúng");
        }

        if (hasError) {
            model.addAttribute("errorMessages", errorMessages);
            // Also set general error for backward compatibility
            model.addAttribute("error", "Vui lòng sửa các lỗi được đánh dấu bên dưới.");
            // Keep the submitted values when redisplaying the form
            model.addAttribute("matKhauCu", matKhauCu);
            model.addAttribute("matKhauMoi", matKhauMoi);
            model.addAttribute("xacNhanMatKhauMoi", xacNhanMatKhauMoi);
            setViewPaths(model);
            return "staff/changePassword";
        }

        // Cập nhật mật khẩu mới (đã được hash)
        String hashedPassword = PasswordUtil.hash(matKhauMoi.trim());
        currentUser.setMatKhau(hashedPassword);

        boolean success = taiKhoanService.update(currentUser);

        if (success) {
            // Cập nhật lại session với thông tin mới
            session.setAttribute("currentUser", currentUser);
            model.addAttribute("success", "Đổi mật khẩu thành công! Bạn có thể tiếp tục làm việc hoặc đăng nhập lại bằng mật khẩu mới để đảm bảo bảo mật.");
        } else {
            model.addAttribute("error", "Đổi mật khẩu thất bại. Vui lòng thử lại!");
        }

        // Hiển thị lại form đổi mật khẩu
        setViewPaths(model);
        return "staff/changePassword";
    }

    private void setViewPaths(Model model) {
        model.addAttribute("isAdminView", false);
        model.addAttribute("passwordBasePath", "/doi-mat-khau");
    }
}