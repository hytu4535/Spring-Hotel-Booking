package com.hotel.repository;

import com.hotel.model.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ThanhToanRepository extends JpaRepository<ThanhToan, Integer> {
    List<ThanhToan> findByDatPhong_MaDp(Integer maDp);
}
