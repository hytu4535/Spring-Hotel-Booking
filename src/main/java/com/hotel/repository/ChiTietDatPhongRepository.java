package com.hotel.repository;

import com.hotel.model.ChiTietDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.sql.Date;
import java.util.List;

public interface ChiTietDatPhongRepository extends JpaRepository<ChiTietDatPhong, Integer> {

                                  @Param("ngayNhan") Date ngayNhan,
}