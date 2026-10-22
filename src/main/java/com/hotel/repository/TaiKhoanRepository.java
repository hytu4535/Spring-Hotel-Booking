package com.hotel.repository;

import com.hotel.model.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    Optional<TaiKhoan> findByTenDangNhapAndTrangThai(String tenDangNhap, int trangThai);
    Optional<TaiKhoan> findByProviderAndProviderId(String provider, String providerId);
    Optional<TaiKhoan> findByEmailIgnoreCase(String email);
}
