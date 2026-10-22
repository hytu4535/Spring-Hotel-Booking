package com.hotel.repository;

import com.hotel.model.DatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DatPhongRepository extends JpaRepository<DatPhong, Integer> {
    List<DatPhong> findByMaKhOrderByNgayDatDesc(int maKh);

    Optional<DatPhong> findByMaTraCuu(String maTraCuu);
    
    List<DatPhong> findAllByOrderByNgayDatDesc();

    @Modifying(clearAutomatically = true)
    @Query("update DatPhong d set d.maKh = :primaryId where d.maKh = :duplicateId")
    int moveBookings(@Param("duplicateId") int duplicateId, @Param("primaryId") int primaryId);
}
