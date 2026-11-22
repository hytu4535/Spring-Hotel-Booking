package com.hotel.repository;

import com.hotel.model.DatDichVuRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatDichVuRepository extends JpaRepository<DatDichVuRecord, Integer> {
    List<DatDichVuRecord> findByDatPhong_MaDpOrderByNgaySuDungAscMaDatDvAsc(Integer maDp);
}
