package com.hotel.repository;

import com.hotel.model.DatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DatPhongRepository extends JpaRepository<DatPhong, Integer> {
    Optional<DatPhong> findByMaTraCuu(String maTraCuu);
    List<DatPhong> findByKhachHang_MaKhOrderByNgayDatDesc(Integer maKh);
}
