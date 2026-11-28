package com.hotel.service;

import com.hotel.dto.RegisterRequest;
import com.hotel.model.KhachHang;
import com.hotel.model.TaiKhoan;
import com.hotel.repository.KhachHangRepository;
import com.hotel.repository.TaiKhoanRepository;
import com.hotel.util.PasswordUtil;
import com.hotel.event.WelcomeEmailEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AuthService(TaiKhoanRepository taiKhoanRepository, KhachHangRepository khachHangRepository,
            ApplicationEventPublisher eventPublisher) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
        this.eventPublisher = eventPublisher;
    }

    public TaiKhoan login(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || matKhau == null ||
                tenDangNhap.isBlank() || matKhau.isBlank()) {
            return null;
        }

        TaiKhoan tk = taiKhoanRepository
                .findByTenDangNhapAndTrangThaiAndDaXoaFalse(tenDangNhap.trim(), 1)
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

    @Transactional
    public TaiKhoan register(RegisterRequest request) {
        String username = request.getTenDangNhap().trim();
        String email = request.getEmail().trim();
        if (taiKhoanRepository.existsByTenDangNhapIgnoreCase(username)) {
            throw new IllegalArgumentException("Tên đăng nhập đã được sử dụng.");
        }
        if (taiKhoanRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email đã được sử dụng.");
        }
        if (khachHangRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalArgumentException("Email đã được sử dụng bởi hồ sơ khách hàng.");
        }
        if (khachHangRepository.findBySoDienThoai(request.getSoDienThoai().trim()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng.");
        }
        if (!request.getMatKhau().equals(request.getXacNhanMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
        }

        TaiKhoan account = new TaiKhoan();
        account.setTenDangNhap(username);
        account.setMatKhau(PasswordUtil.hash(request.getMatKhau()));
        account.setHoTen(request.getHoTen().trim());
        account.setEmail(email);
        account.setSoDienThoai(request.getSoDienThoai().trim());
        account.setVaiTro("CUSTOMER");
        account.setTrangThai(1);
        account.setProvider("LOCAL");
        account.setNgayTao(new java.sql.Timestamp(System.currentTimeMillis()));
        TaiKhoan saved = taiKhoanRepository.save(account);

        KhachHang customer = new KhachHang();
        customer.setMaTk(saved.getMaTk());
        customer.setHoTen(saved.getHoTen());
        customer.setEmail(saved.getEmail());
        customer.setSoDienThoai(saved.getSoDienThoai());
        customer.setNgayTao(new java.sql.Timestamp(System.currentTimeMillis()));
        khachHangRepository.save(customer);
        eventPublisher.publishEvent(new WelcomeEmailEvent(
                saved.getEmail(), saved.getHoTen(), saved.getTenDangNhap()));
        return saved;
    }
}
