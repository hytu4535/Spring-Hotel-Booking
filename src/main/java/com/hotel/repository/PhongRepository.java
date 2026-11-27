package com.hotel.repository;

import com.hotel.model.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface PhongRepository extends JpaRepository<Phong, Integer> {

    List<Phong> findAllByDaXoaFalse();

    List<Phong> findBySoPhongContainingIgnoreCase(String soPhong);

    boolean existsBySoPhongIgnoreCaseAndDaXoaFalse(String soPhong);

    boolean existsBySoPhongIgnoreCaseAndMaPhongNot(
            String soPhong, Integer maPhong
    );

    @Query("SELECT p FROM Phong p " +
            "WHERE p.daXoa = false " +
            "AND NOT EXISTS (" +
            "    SELECT c FROM ChiTietDatPhong c JOIN c.datPhong d " +
            "    WHERE c.phong = p " +
            "    AND d.trangThai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN', 'DA_NHAN_PHONG') " +
            "    AND d.ngayNhanPhong < :checkOut " +
            "    AND d.ngayTraPhong > :checkIn" +
            ")")
    List<Phong> findAvailable(
            @Param("checkIn") Date checkIn,
            @Param("checkOut") Date checkOut
    );
}