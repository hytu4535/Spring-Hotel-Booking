package com.hotel.repository;

import com.hotel.model.Phong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhongRepository extends JpaRepository<Phong, Integer> {

    long countByLoaiPhong_MaLoai(Integer maLoai);
}
