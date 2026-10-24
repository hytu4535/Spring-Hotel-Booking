package com.hotel.repository;

import com.hotel.model.DatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DatPhongRepository extends JpaRepository<DatPhong, Integer> {
    Optional<DatPhong> findByMaTraCuu(String maTraCuu);

    List<DatPhong> findByKhachHang_MaKhOrderByNgayDatDesc(Integer maKh);

    @Query("SELECT dp FROM DatPhong dp WHERE dp.khachHang.maKh = :maKh ORDER BY dp.ngayDat DESC")
    List<DatPhong> findByMaKhOrderByNgayDatDesc(@Param("maKh") Integer maKh);

    @Modifying
    @Query("UPDATE DatPhong dp SET dp.khachHang = (SELECT k FROM KhachHang k WHERE k.maKh = :primaryKh) WHERE dp.khachHang.maKh = :duplicateKh")
    void moveBookings(@Param("duplicateKh") Integer duplicateKh, @Param("primaryKh") Integer primaryKh);
}
