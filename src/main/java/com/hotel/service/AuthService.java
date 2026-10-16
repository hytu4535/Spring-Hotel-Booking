package com.hotel.service;

import com.hotel.dao.TaiKhoanDAO;
import com.hotel.model.TaiKhoan;
import com.hotel.util.PasswordUtil;

public class AuthService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public TaiKhoan login(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || matKhau == null ||
                tenDangNhap.isBlank() || matKhau.isBlank()) {
            return null;
        }

        TaiKhoan tk = taiKhoanDAO.findByTenDangNhap(tenDangNhap.trim());
        if (tk == null) {
            return null;
        }

        // Kiểm tra trạng thái tài khoản
        if (tk.getTrangThai() != 1) {
            return null; // tài khoản bị khóa
        }

        // So sánh mật khẩu (BCrypt)
        if (!PasswordUtil.check(matKhau, tk.getMatKhau())) {
            return null;
        }

        return tk;
    }
}
