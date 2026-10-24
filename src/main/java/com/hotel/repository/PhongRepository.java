package com.hotel.repository;

import com.hotel.model.Phong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhongRepository extends JpaRepository<Phong, Integer> {
    List<Phong> findAllByDaXoaFalse();

    Optional<Phong> findByMaPhongAndDaXoaFalse(Integer maPhong);

    boolean existsBySoPhongIgnoreCaseAndDaXoaFalse(String soPhong);

    boolean existsBySoPhongIgnoreCaseAndMaPhongNot(String soPhong, Integer maPhong);
}


