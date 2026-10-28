package com.hotel.service;

import com.hotel.model.TaiKhoan;
import com.hotel.repository.TaiKhoanRepository;
import com.hotel.util.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final TaiKhoanRepository taiKhoanRepository;

    public AuthService(TaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    public TaiKhoan login(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || matKhau == null ||
                tenDangNhap.isBlank() || matKhau.isBlank()) {
            return null;
        }

        TaiKhoan tk = taiKhoanRepository
                .findByTenDangNhapAndTrangThai(tenDangNhap.trim(), 1)
                .orElse(null);
        if (tk == null) {
            return null;
        }

        // So sánh mật khẩu (BCrypt)
        if (!PasswordUtil.check(matKhau, tk.getMatKhau())) {
            return null;
        }

        return tk;
    }
}
