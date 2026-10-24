package com.hotel.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.hotel.dao.TaiKhoanDAO;
import com.hotel.model.TaiKhoan;
import com.hotel.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Servlet để quản lý tài khoản (chỉ ADMIN)
 * Cho phép xem danh sách tài khoản, khóa/mở khóa, đổi vai trò, tạo tài khoản mới
 */
@Controller
public class AccountManagementServlet {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    @GetMapping("/admin/tai-khoan")
    public String doGet(@RequestParam Map<String, String> params, HttpSession session, Model model) {
        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra quyền_ADMIN
        if (user == null || !"ADMIN".equals(user.getVaiTro())) {
            return "redirect:/login";

        }

        // Lấy danh sách tất cả tài khoản
        List<TaiKhoan> taiKhoanList = loadFilteredAccounts(params);
        model.addAttribute("taiKhoanList", taiKhoanList);
        model.addAttribute("currentUser", user);
        setFilterAttributes(params, model);

        return "admin/taiKhoanManage";
    }

    @PostMapping("/admin/tai-khoan")
    public String doPost(@RequestParam Map<String, String> params, HttpSession session, Model model) {
        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra quyền_ADMIN
        if (user == null || !"ADMIN".equals(user.getVaiTro())) {
            return "redirect:/login";

        }

        String action = params.get("action");

        // Xử lý tạo tài khoản mới
        if ("create".equals(action)) {
            String tenDangNhap = params.get("tenDangNhap");
            String matKhau = params.get("matKhau");
            String hoTen = params.get("hoTen");
            String email = params.get("email");
            String soDienThoai = params.get("soDienThoai");
            String vaiTro = params.get("vaiTro");

            boolean hasError = false;
            if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
                model.addAttribute("errorTenDangNhap", "Tên đăng nhập không được để trống");
                hasError = true;
            } else if (taiKhoanDAO.existsTenDangNhap(tenDangNhap.trim(), null)) {
                model.addAttribute("errorTenDangNhap", "Tên đăng nhập đã tồn tại");
                hasError = true;
            }
            if (matKhau == null || matKhau.isBlank()) {
                model.addAttribute("errorMatKhau", "Mật khẩu không được để trống");
                hasError = true;
            } else if (matKhau.length() < 6) {
                model.addAttribute("errorMatKhau", "Mật khẩu phải có ít nhất 6 ký tự");
                hasError = true;
            }
            if (hoTen == null || hoTen.trim().isEmpty()) {
                model.addAttribute("errorHoTen", "Họ tên không được để trống");
                hasError = true;
            }
            if (email == null || email.trim().isEmpty()) {
                model.addAttribute("errorEmail", "Email không được để trống");
                hasError = true;
            } else if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
                model.addAttribute("errorEmail", "Email không đúng định dạng");
                hasError = true;
            } else if (taiKhoanDAO.existsEmail(email.trim(), null)) {
                model.addAttribute("errorEmail", "Email đã tồn tại");
                hasError = true;
            }
            // Per requirements: Admin chỉ được tạo tài khoản nhân viên
            if (vaiTro == null || (!"NHANVIEN".equals(vaiTro) && !"ADMIN".equals(vaiTro))) {
                model.addAttribute("errorVaiTro", "Vai trò không hợp lệ");
                hasError = true;
            } else if ("ADMIN".equals(vaiTro)) {
                // Admin không được tạo tài khoản admin mới (per requirements: chỉ được tạo nhân viên)
                model.addAttribute("errorVaiTro", "Chỉ được tạo tài khoản nhân viên");
                hasError = true;
            }

            if (hasError) {
                // Giữ lại các giá trị đã nhập
                model.addAttribute("tenDangNhap", tenDangNhap);
                model.addAttribute("hoTen", hoTen);
                model.addAttribute("email", email);
                model.addAttribute("soDienThoai", soDienThoai);
                model.addAttribute("vaiTro", vaiTro);

                List<TaiKhoan> taiKhoanList = loadFilteredAccounts(params);
                model.addAttribute("taiKhoanList", taiKhoanList);
                model.addAttribute("currentUser", user);
                return "admin/taiKhoanManage";

            }

            // Tài khoản mới luôn được kích hoạt (trang_thai = 1)
            TaiKhoan newTk = new TaiKhoan();
            newTk.setTenDangNhap(tenDangNhap.trim());
            newTk.setMatKhau(PasswordUtil.hash(matKhau)); // Hash mật khẩu
            newTk.setHoTen(hoTen.trim());
            newTk.setEmail(email.trim());
            newTk.setSoDienThoai(soDienThoai == null ? null : soDienThoai.trim());
            newTk.setVaiTro(vaiTro);
            newTk.setTrangThai(1); // Hoạt động

            boolean success = taiKhoanDAO.insert(newTk);
            if (success) {
                model.addAttribute("success", "Tạo tài khoản thành công!");
            } else {
                model.addAttribute("error", "Có lỗi xảy ra khi tạo tài khoản. Vui lòng thử lại.");
            }

            // Lấy lại danh sách sau khi tạo
            List<TaiKhoan> taiKhoanList = loadFilteredAccounts(params);
            model.addAttribute("taiKhoanList", taiKhoanList);
            model.addAttribute("currentUser", user);
            return "admin/taiKhoanManage";

        }

        // Xử lý các hành động khác (lock, unlock, role) trên tài khoản hiện có
        String maTkStr = params.get("maTk");
        if (maTkStr == null || maTkStr.isBlank()) {
            model.addAttribute("error", "Thông tin tài khoản không hợp lệ");
            List<TaiKhoan> taiKhoanList = loadFilteredAccounts(params);
            model.addAttribute("taiKhoanList", taiKhoanList);
            model.addAttribute("currentUser", user);
            return "admin/taiKhoanManage";

        }

        int maTk = Integer.parseInt(maTkStr);

        // Không cho phép tự thao tác trên chính mình
        if (maTk == user.getMaTk()) {
            model.addAttribute("error", "Không thể thực hiện hành động trên tài khoản của chính bạn");
            List<TaiKhoan> taiKhoanList = loadFilteredAccounts(params);
            model.addAttribute("taiKhoanList", taiKhoanList);
            model.addAttribute("currentUser", user);
            return "admin/taiKhoanManage";

        }

        boolean success = false;
        String message = "";

        switch (action) {
            case "lock":
                success = taiKhoanDAO.updateTrangThai(maTk, 0); // 0: khóa
                message = success ? "Khóa tài khoản thành công" : "Khóa tài khoản thất bại";
                break;
            case "unlock":
                success = taiKhoanDAO.updateTrangThai(maTk, 1); // 1: hoạt động
                message = success ? "Mở khóa tài khoản thành công" : "Mở khóa tài khoản thất bại";
                break;
            case "role":
                // Lấy tài khoản hiện tại để xác định vai trò mới
                TaiKhoan tk = taiKhoanDAO.findById(maTk);
                if (tk != null) {
                    String newRole = "ADMIN".equals(tk.getVaiTro()) ? "NHANVIEN" : "ADMIN";
                    tk.setVaiTro(newRole);
                    success = taiKhoanDAO.update(tk);
                    message = success ? "Đổi vai trò thành công" : "Đổi vai trò thất bại";
                }
                break;
            default:
                model.addAttribute("error", "Hành động không hợp lệ");
                List<TaiKhoan> taiKhoanList = loadFilteredAccounts(params);
                model.addAttribute("taiKhoanList", taiKhoanList);
                model.addAttribute("currentUser", user);
                return "admin/taiKhoanManage";

        }

        if (success) {
            model.addAttribute("success", message);
        } else {
            model.addAttribute("error", message);
        }

        // Lấy lại danh sách sau khi thực hiện hành động
        List<TaiKhoan> taiKhoanList = loadFilteredAccounts(params);
        model.addAttribute("taiKhoanList", taiKhoanList);
        model.addAttribute("currentUser", user);
        setFilterAttributes(params, model);
        return "admin/taiKhoanManage";
    }

    private List<TaiKhoan> loadFilteredAccounts(Map<String, String> params) {
        String username = normalize(params.get("tenDangNhap"));
        String name = normalize(params.get("hoTen"));
        String email = normalize(params.get("email"));
        String phone = normalize(params.get("soDienThoai"));
        String role = normalize(params.get("vaiTro"));
        String status = normalize(params.get("trangThai"));
        return taiKhoanDAO.findAll().stream()
                .filter(tk -> contains(tk.getTenDangNhap(), username))
                .filter(tk -> contains(tk.getHoTen(), name))
                .filter(tk -> contains(tk.getEmail(), email))
                .filter(tk -> contains(tk.getSoDienThoai(), phone))
                .filter(tk -> role.isEmpty() || role.equalsIgnoreCase(tk.getVaiTro()))
                .filter(tk -> status.isEmpty() || (("1".equals(status) || "0".equals(status))
                        && Integer.toString(tk.getTrangThai()).equals(status)))
                .collect(Collectors.toList());
    }

    private void setFilterAttributes(Map<String, String> params, Model model) {
        model.addAttribute("filterTenDangNhap", valueOrEmpty(params.get("tenDangNhap")));
        model.addAttribute("filterHoTen", valueOrEmpty(params.get("hoTen")));
        model.addAttribute("filterEmail", valueOrEmpty(params.get("email")));
        model.addAttribute("filterSoDienThoai", valueOrEmpty(params.get("soDienThoai")));
        model.addAttribute("filterVaiTro", valueOrEmpty(params.get("vaiTro")));
        model.addAttribute("filterTrangThai", valueOrEmpty(params.get("trangThai")));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private boolean contains(String value, String filter) {
        return filter.isEmpty() || (value != null && value.toLowerCase(Locale.ROOT).contains(filter));
    }
}