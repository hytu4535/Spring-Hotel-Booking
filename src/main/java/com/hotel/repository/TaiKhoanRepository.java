package com.hotel.repository;

import com.hotel.model.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    Optional<TaiKhoan> findByTenDangNhapAndTrangThai(String tenDangNhap, int trangThai);
    Optional<TaiKhoan> findByTenDangNhapIgnoreCase(String tenDangNhap);
    Optional<TaiKhoan> findByProviderAndProviderId(String provider, String providerId);
    Optional<TaiKhoan> findByEmailIgnoreCase(String email);
    List<TaiKhoan> findAllByOrderByNgayTaoDesc();
    boolean existsByTenDangNhapIgnoreCase(String tenDangNhap);
    boolean existsByTenDangNhapIgnoreCaseAndMaTkNot(String tenDangNhap, int maTk);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndMaTkNot(String email, int maTk);
}
