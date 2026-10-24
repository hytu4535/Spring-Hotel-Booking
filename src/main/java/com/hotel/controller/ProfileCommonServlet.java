package com.hotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.hotel.model.TaiKhoan;
import com.hotel.service.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet chung để hiển thị và cập nhật hồ sơ cá nhân
 * Truy cập được cho cả ADMIN và NHANVIEN
 */
@Controller
public class ProfileCommonServlet {

    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    @GetMapping("/profile")
    public String doGet(HttpSession session, Model model) {
        TaiKhoan currentUser = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra đăng nhập và quyền (ADMIN hoặc NHANVIEN)
        if (currentUser == null ||
                (!"ADMIN".equals(currentUser.getVaiTro()) && !"NHANVIEN".equals(currentUser.getVaiTro()))) {
            return "redirect:/login";

        }

        // Lấy thông tin tài khoản hiện tại từ database để có dữ liệu mới nhất
        TaiKhoan user = taiKhoanService.findById(currentUser.getMaTk());
        if (user == null) {
            return "redirect:/login";

        }

        model.addAttribute("user", user);
        setViewPaths(model);
        return "staff/profile";
    }

    @PostMapping("/profile")
    public String doPost(@RequestParam(required = false) String hoTen,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String soDienThoai,
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

        if (hoTen == null || hoTen.trim().isEmpty()) {
            hasError = true;
            errorMessages.put("hoTen", "Họ tên không được để trống");
            model.addAttribute("errorHoTen", "Họ tên không được để trống");
        }
        if (email == null || email.trim().isEmpty()) {
            hasError = true;
            errorMessages.put("email", "Email không được để trống");
            model.addAttribute("errorEmail", "Email không được để trống");
        } else if (!email.matches("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
            hasError = true;
            errorMessages.put("email", "Email không đúng định dạng");
            model.addAttribute("errorEmail", "Email không đúng định dạng");
        }
        // Validate phone number: 10 digits, starting with 0
        if (soDienThoai != null && !soDienThoai.isEmpty()) {
            if (!soDienThoai.matches("^0[0-9]{9}$")) {
                hasError = true;
                errorMessages.put("soDienThoai", "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0");
                model.addAttribute("errorSoDienThoai", "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0");
            }
        }

        if (hasError) {
            model.addAttribute("errorMessages", errorMessages);
            // Also set general error for backward compatibility
            model.addAttribute("error", "Vui lòng sửa các lỗi được đánh dấu bên dưới.");
            // Keep the submitted values when redisplaying the form
            model.addAttribute("hoTen", hoTen);
            model.addAttribute("email", email);
            model.addAttribute("soDienThoai", soDienThoai);
            // Lấy lại thông tin người dùng để hiển thị form
            TaiKhoan user = taiKhoanService.findById(currentUser.getMaTk());
            model.addAttribute("user", user);
            setViewPaths(model);
            return "staff/profile";
        }

        // Cập nhật thông tin
        currentUser.setHoTen(hoTen.trim());
        currentUser.setEmail(email.trim());
        currentUser.setSoDienThoai(soDienThoai != null ? soDienThoai.trim() : null);

        boolean success = taiKhoanService.update(currentUser);

        if (success) {
            // Cập nhật lại session
            session.setAttribute("currentUser", currentUser);
            model.addAttribute("success", "Cập nhật hồ sơ thành công!");
        } else {
            model.addAttribute("error", "Cập nhật hồ sơ thất bại. Vui lòng thử lại!");
        }

        // Lấy lại thông tin để hiển thị
        TaiKhoan user = taiKhoanService.findById(currentUser.getMaTk());
        model.addAttribute("user", user);
        setViewPaths(model);
        return "staff/profile";
    }

    private void setViewPaths(Model model) {
        model.addAttribute("isAdminView", false);
        model.addAttribute("profileBasePath", "/profile");
    }
}