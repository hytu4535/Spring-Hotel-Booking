package com.hotel.repository;

import com.hotel.model.DatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DatPhongRepository extends JpaRepository<DatPhong, Integer> {
    List<DatPhong> findByMaKhOrderByNgayDatDesc(int maKh);

    Optional<DatPhong> findByMaTraCuu(String maTraCuu);

    List<DatPhong> findAllByOrderByNgayDatDesc();

    @Modifying(clearAutomatically = true)
    @Query("update DatPhong d set d.maKh = :primaryId where d.maKh = :duplicateId")
    int moveBookings(@Param("duplicateId") int duplicateId, @Param("primaryId") int primaryId);

    @Query("SELECT d FROM DatPhong d WHERE " +
            "(:trangThai IS NULL OR :trangThai = '' OR d.trangThai = :trangThai) AND " +
            "(:tuNgay IS NULL OR d.ngayDat >= :tuNgay) AND " +
            "(:denNgay IS NULL OR d.ngayDat <= :denNgay) AND " +
            "(:keyword IS NULL OR :keyword = '' OR d.maTraCuu LIKE %:keyword% OR d.khachHang.hoTen LIKE %:keyword% OR d.khachHang.soDienThoai LIKE %:keyword%) "
            +
            "ORDER BY d.ngayDat DESC")
    List<DatPhong> searchAdvanced(
            @Param("keyword") String keyword,
            @Param("trangThai") String trangThai,
            @Param("tuNgay") java.sql.Timestamp tuNgay,
            @Param("denNgay") java.sql.Timestamp denNgay);
}
