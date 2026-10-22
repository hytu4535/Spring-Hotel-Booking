package com.hotel.repository;

import com.hotel.model.ChiTietDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.sql.Date;
import java.util.List;

public interface ChiTietDatPhongRepository extends JpaRepository<ChiTietDatPhong, Integer> {
    List<ChiTietDatPhong> findByDatPhong_MaDp(Integer maDp);

    @Query("SELECT COUNT(ct) FROM ChiTietDatPhong ct JOIN ct.datPhong dp " +
           "WHERE dp.trangThai <> 'DA_HUY' AND ct.phong.maPhong = :maPhong " +
           "AND dp.ngayNhanPhong < :ngayTra AND dp.ngayTraPhong > :ngayNhan")
    long countOverlappingBookings(@Param("maPhong") Integer maPhong,
                                  @Param("ngayNhan") Date ngayNhan,
                                  @Param("ngayTra") Date ngayTra);
}
