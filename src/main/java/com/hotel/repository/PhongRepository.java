package com.hotel.repository;

import com.hotel.model.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface PhongRepository extends JpaRepository<Phong, Integer> {
    List<Phong> findAllByDaXoaFalse();

    List<Phong> findBySoPhongContainingIgnoreCase(String soPhong);

    Optional<Phong> findByMaPhongAndDaXoaFalse(Integer maPhong);

    boolean existsBySoPhongIgnoreCaseAndDaXoaFalse(String soPhong);

    boolean existsBySoPhongIgnoreCaseAndMaPhongNot(String soPhong, Integer maPhong);

    @Query("SELECT p FROM Phong p WHERE p.trangThaiPhong = 'TRONG' AND p.daXoa = false")
    List<Phong> findAvailable(@Param("checkIn") Date checkIn, @Param("checkOut") Date checkOut);
}


