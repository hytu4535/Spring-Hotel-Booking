package com.hotel.repository;

import com.hotel.model.LoaiPhong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoaiPhongRepository extends JpaRepository<LoaiPhong, Integer> {

    boolean existsByTenLoaiIgnoreCase(String tenLoai);

    boolean existsByTenLoaiIgnoreCaseAndMaLoaiNot(String tenLoai, Integer maLoai);
}
