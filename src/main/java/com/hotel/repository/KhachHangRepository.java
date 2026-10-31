package com.hotel.repository;

import com.hotel.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    Optional<KhachHang> findByEmailIgnoreCase(String email);
    Optional<KhachHang> findByMaTk(Integer maTk);

    List<KhachHang> findAllByOrderByNgayTaoDesc();

    @Query("SELECT k FROM KhachHang k WHERE k.hoTen LIKE %:keyword% OR k.email LIKE %:keyword% OR k.soDienThoai LIKE %:keyword% OR k.cccd LIKE %:keyword%")
    List<KhachHang> search(@Param("keyword") String keyword);
}
