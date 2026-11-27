package com.hotel.repository;

import com.hotel.model.ChiTietDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface ChiTietDatPhongRepository extends JpaRepository<ChiTietDatPhong, Integer> {
    
    @Query("SELECT c FROM ChiTietDatPhong c WHERE c.datPhong.maDp = :maDp")
    List<ChiTietDatPhong> findByMaDp(@Param("maDp") Integer maDp);

    @Query("SELECT c FROM ChiTietDatPhong c WHERE c.datPhong.maDp = :maDp")
    List<ChiTietDatPhong> findByDatPhong_MaDp(@Param("maDp") Integer maDp);

    @Query("SELECT COUNT(c) FROM ChiTietDatPhong c JOIN c.datPhong d " +
           "WHERE d.trangThai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN', 'DA_NHAN_PHONG') " +
           "AND c.phong.maPhong = :maPhong " +
           "AND d.ngayNhanPhong < :ngayTra " +
           "AND d.ngayTraPhong > :ngayNhan")
    long countOverlappingReservations(
            @Param("maPhong") Integer maPhong, 
            @Param("ngayNhan") Date ngayNhan,
            @Param("ngayTra") Date ngayTra
    );

    @Query("SELECT COUNT(c) FROM ChiTietDatPhong c " +
            "JOIN c.datPhong d " +
            "WHERE c.phong.maPhong IN :maPhongList " +
            "AND d.maDp <> :maDp " +
            "AND d.trangThai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN', 'DA_NHAN_PHONG') " +
            "AND d.ngayNhanPhong < :ngayTra " +
            "AND d.ngayTraPhong > :ngayNhan")
    long countRoomConflicts(
            @Param("maPhongList") List<Integer> maPhongList,
            @Param("maDp") Integer maDp,
            @Param("ngayNhan") java.sql.Date ngayNhan,
            @Param("ngayTra") java.sql.Date ngayTra
    );

    @Query("SELECT DISTINCT c.phong.maPhong " +
            "FROM ChiTietDatPhong c " +
            "JOIN c.datPhong d " +
            "WHERE d.trangThai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN', 'DA_NHAN_PHONG') " +
            "AND d.ngayNhanPhong <= :today " +
            "AND d.ngayTraPhong > :today")
    List<Integer> findMaPhongDangDuocDatTrongNgay(
            @Param("today") java.sql.Date today
    );
}
