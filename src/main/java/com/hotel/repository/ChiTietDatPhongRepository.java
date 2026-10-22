package com.hotel.repository;

import com.hotel.model.ChiTietDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ChiTietDatPhongRepository extends JpaRepository<ChiTietDatPhong, Integer> {
    
    List<ChiTietDatPhong> findByMaDp(Integer maDp);

    @Query("SELECT COUNT(c) FROM ChiTietDatPhong c, DatPhong d " +
           "WHERE c.maDp = d.maDp " +
           "AND d.trangThai <> 'DA_HUY' " +
           "AND c.maPhong = :maPhong " +
           "AND d.ngayNhanPhong < :ngayTra " +
           "AND d.ngayTraPhong > :ngayNhan")
    long countOverlappingReservations(
            @Param("maPhong") Integer maPhong, 
            @Param("ngayNhan") LocalDate ngayNhan, 
            @Param("ngayTra") LocalDate ngayTra
    );
}