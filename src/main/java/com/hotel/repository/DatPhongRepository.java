package com.hotel.repository;

import com.hotel.model.DatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DatPhongRepository extends JpaRepository<DatPhong, Integer> {

    @Query("SELECT d FROM DatPhong d WHERE d.khachHang.maKh = :maKh ORDER BY d.ngayDat DESC")
    List<DatPhong> findByMaKhOrderByNgayDatDesc(@Param("maKh") int maKh);

    Optional<DatPhong> findByMaTraCuu(String maTraCuu);
    
    List<DatPhong> findAllByOrderByNgayDatDesc();

    @Modifying(clearAutomatically = true)
    @Query("update DatPhong d set d.khachHang.maKh = :primaryId where d.khachHang.maKh = :duplicateId")
    int moveBookings(@Param("duplicateId") int duplicateId, @Param("primaryId") int primaryId);
}
