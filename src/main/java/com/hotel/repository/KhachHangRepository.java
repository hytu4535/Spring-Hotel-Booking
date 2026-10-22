package com.hotel.repository;

import com.hotel.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    Optional<KhachHang> findByEmailIgnoreCase(String email);
    Optional<KhachHang> findByMaTk(Integer maTk);
}
