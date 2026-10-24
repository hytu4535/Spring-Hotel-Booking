package com.hotel.repository;

import com.hotel.model.LoaiPhong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoaiPhongRepository extends JpaRepository<LoaiPhong, Integer> {

    boolean existsByTenLoaiIgnoreCase(String tenLoai);

    boolean existsByTenLoaiIgnoreCaseAndMaLoaiNot(String tenLoai, Integer maLoai);

    Optional<LoaiPhong> findByTenLoai(String tenLoai);

    List<LoaiPhong> findAllByDaXoaFalse();

    Optional<LoaiPhong> findByMaLoaiAndDaXoaFalse(Integer id);
}
