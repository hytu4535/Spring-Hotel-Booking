package com.hotel.repository;

import com.hotel.model.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ThanhToanRepository extends JpaRepository<ThanhToan, Integer> {
    List<ThanhToan> findByDatPhong_MaDp(Integer maDp);

    @Query("SELECT t FROM ThanhToan t WHERE " +
           "(:loaiThanhToan IS NULL OR t.loaiThanhToan = :loaiThanhToan) AND " +
           "(:phuongThuc IS NULL OR t.phuongThuc = :phuongThuc) AND " +
           "(:keyword IS NULL OR :keyword = '' OR t.maGiaoDich LIKE %:keyword% OR t.datPhong.maTraCuu LIKE %:keyword%)")
    List<ThanhToan> searchTransactions(@Param("loaiThanhToan") String loaiThanhToan,
                                       @Param("phuongThuc") String phuongThuc,
                                       @Param("keyword") String keyword);
}
