package com.hotel.controller.admin;

import com.hotel.model.TaiKhoan;
import com.hotel.service.TaiKhoanService;
import com.hotel.util.PasswordUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({"/admin/tai-khoan", "/admin/nhan-vien"})
public class AdminAccountController {
    private final TaiKhoanService service;
    public AdminAccountController(TaiKhoanService service) { this.service = service; }

    @GetMapping
    public String list(HttpServletRequest request, Model model) {
        if (!isAdmin(request)) return "redirect:/login";
        model.addAttribute("taiKhoanList", service.findAll());
        model.addAttribute("filterTenDangNhap", request.getParameter("tenDangNhap"));
        model.addAttribute("filterHoTen", request.getParameter("hoTen"));
        return "admin/taiKhoanManage";
    }

    @GetMapping("/{maTk}/sua")
    public String editForm(@PathVariable int maTk, HttpServletRequest request, Model model) {
        if (!isAdmin(request)) return "redirect:/login";
        TaiKhoan account = service.findById(maTk);
        if (account == null) return "redirect:/admin/tai-khoan";
        model.addAttribute("account", account);
        return "admin/form-tai-khoan";
    }

    @PostMapping
    public String action(@RequestParam String action, @RequestParam(required = false) Integer maTk,
                         HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!isAdmin(request)) return "redirect:/login";
        if ("create".equals(action)) {
            String username = request.getParameter("tenDangNhap");
            String password = request.getParameter("matKhau");
            TaiKhoan created = new TaiKhoan();
            created.setTenDangNhap(username);
            created.setMatKhau(PasswordUtil.hash(password));
            created.setHoTen(request.getParameter("hoTen"));
            created.setEmail(request.getParameter("email"));
            created.setSoDienThoai(request.getParameter("soDienThoai"));
            created.setVaiTro("NHANVIEN");
            created.setTrangThai(1);
            if (username != null && password != null && !password.isEmpty()
                    && !service.existsTenDangNhap(username, null) && !service.existsEmail(created.getEmail(), null)) {
                service.update(created);
                redirectAttributes.addFlashAttribute("success", "Tạo tài khoản nhân viên thành công.");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "Không thể tạo tài khoản: tên đăng nhập hoặc email đã tồn tại, hoặc dữ liệu còn thiếu.");
            }
        }
        if (maTk != null && maTk == currentUser(request).getMaTk()
                && ("lock".equals(action) || "role".equals(action))) {
            redirectAttributes.addFlashAttribute("error", "Không thể khóa hoặc đổi quyền tài khoản đang đăng nhập.");
            return "redirect:" + request.getRequestURI();
        }

        if ("lock".equals(action) && maTk != null) {
            redirectAttributes.addFlashAttribute("success",
                    service.updateTrangThai(maTk, 0) ? "Đã khóa tài khoản." : "Không thể khóa tài khoản.");
        }
        if ("unlock".equals(action) && maTk != null) {
            redirectAttributes.addFlashAttribute("success",
                    service.updateTrangThai(maTk, 1) ? "Đã mở khóa tài khoản." : "Không thể mở khóa tài khoản.");
        }
        if ("role".equals(action) && maTk != null) {
            TaiKhoan user = service.findById(maTk);
            if (user != null) {
                user.setVaiTro("ADMIN".equals(user.getVaiTro()) ? "NHANVIEN" : "ADMIN");
                service.update(user);
                redirectAttributes.addFlashAttribute("success", "Đã cập nhật vai trò tài khoản.");
            }
        }
        return "redirect:" + request.getRequestURI();
    }

    @PostMapping("/{maTk}/sua")
    public String edit(@PathVariable int maTk,
                       @RequestParam String hoTen,
                       @RequestParam String email,
                       @RequestParam(required = false) String soDienThoai,
                       @RequestParam(required = false) String matKhau,
                       HttpServletRequest request,
                       RedirectAttributes redirectAttributes) {
        if (!isAdmin(request)) return "redirect:/login";
        TaiKhoan account = service.findById(maTk);
        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "Tài khoản không tồn tại.");
            return "redirect:/admin/tai-khoan";
        }
        if (hoTen == null || hoTen.isBlank() || email == null || email.isBlank()
                || service.existsEmail(email.trim(), maTk)) {
            redirectAttributes.addFlashAttribute("error", "Họ tên/email không hợp lệ hoặc email đã tồn tại.");
            return "redirect:/admin/tai-khoan/" + maTk + "/sua";
        }
        account.setHoTen(hoTen.trim());
        account.setEmail(email.trim());
        account.setSoDienThoai(soDienThoai == null ? null : soDienThoai.trim());
        if (matKhau != null && !matKhau.isBlank()) {
            if (matKhau.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
                return "redirect:/admin/tai-khoan/" + maTk + "/sua";
            }
            account.setMatKhau(PasswordUtil.hash(matKhau));
        }
        service.update(account);
        redirectAttributes.addFlashAttribute("success", "Cập nhật tài khoản thành công.");
        return "redirect:/admin/tai-khoan";
    }

    private TaiKhoan currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (TaiKhoan) session.getAttribute("currentUser");
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        TaiKhoan user = session == null ? null : (TaiKhoan) session.getAttribute("currentUser");
        return user != null && "ADMIN".equals(user.getVaiTro());
    }
}
