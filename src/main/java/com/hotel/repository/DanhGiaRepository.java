package com.hotel.repository;

import com.hotel.model.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DanhGiaRepository extends JpaRepository<DanhGia, Integer> {
    Optional<DanhGia> findByDatPhong_MaDp(Integer maDp);

    List<DanhGia> findByKhachHang_MaKh(Integer maKh);

    @Query("SELECT DISTINCT dg FROM DanhGia dg JOIN dg.datPhong dp JOIN dp.chiTietDatPhongs ctdp JOIN ctdp.phong p JOIN p.maLoaiPhong lp WHERE lp.maLoai = :maLoai")
    List<DanhGia> findByLoaiPhong(@Param("maLoai") Integer maLoai);
}
