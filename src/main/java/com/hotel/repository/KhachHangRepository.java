package com.hotel.repository;

import com.hotel.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    Optional<KhachHang> findByMaTk(Integer maTk);
    Optional<KhachHang> findByEmailIgnoreCase(String email);

    @Query("select k from KhachHang k where lower(k.hoTen) like lower(concat('%', :keyword, '%')) " +
            "or lower(k.email) like lower(concat('%', :keyword, '%')) " +
            "or k.soDienThoai like concat('%', :keyword, '%') or k.cccd like concat('%', :keyword, '%') " +
            "order by k.ngayTao desc")
    List<KhachHang> search(@Param("keyword") String keyword);

    List<KhachHang> findAllByOrderByNgayTaoDesc();
}
