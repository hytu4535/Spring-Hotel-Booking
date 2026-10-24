package com.hotel.repository;

import com.hotel.model.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.EntityGraph;

public interface PhongRepository extends JpaRepository<Phong, Integer> {

    long countByLoaiPhong_MaLoai(Integer maLoai);

    @Override
    @EntityGraph(attributePaths = "loaiPhong")
    List<Phong> findAll();

    @Query("select p from Phong p join fetch p.loaiPhong lp " +
           "where p.trangThai <> 'BAO_TRI' and not exists (" +
           "select c.maCtdp from ChiTietDatPhong c, DatPhong d " +
           "where c.maDp = d.maDp and c.maPhong = p.maPhong " +
           "and d.trangThai <> 'DA_HUY' " +
           "and d.ngayNhanPhong < :checkOut and d.ngayTraPhong > :checkIn) " +
           "order by lp.giaMacDinh asc, p.soPhong asc")
    List<Phong> findAvailable(@Param("checkIn") Date checkIn, @Param("checkOut") Date checkOut);

    @Query(value = 
        "SELECT " +
        "    p.ma_phong as maPhong, " +
        "    p.so_phong as soPhong, " +
        "    p.tang as tang, " +
        "    p.trang_thai as trangThai, " +
        "    lp.ten_loai as tenLoaiPhong, " +
        "    (SELECT dp.ma_dp " +
        "     FROM chi_tiet_dat_phong ct " +
        "     JOIN dat_phong dp ON ct.ma_dp = dp.ma_dp " +
        "     WHERE ct.ma_phong = p.ma_phong " +
        "       AND dp.trang_thai IN ('DA_XAC_NHAN', 'DA_NHAN_PHONG') " +
        "       AND dp.ngay_nhan_phong <= CURRENT_DATE " +
        "       AND dp.ngay_tra_phong >= CURRENT_DATE " +
        "     LIMIT 1) as maDpHienTai, " +
        "    (SELECT kh.ho_ten " +
        "     FROM chi_tiet_dat_phong ct " +
        "     JOIN dat_phong dp ON ct.ma_dp = dp.ma_dp " +
        "     JOIN khach_hang kh ON dp.ma_kh = kh.ma_kh " +
        "     WHERE ct.ma_phong = p.ma_phong " +
        "       AND dp.trang_thai IN ('DA_XAC_NHAN', 'DA_NHAN_PHONG') " +
        "       AND dp.ngay_nhan_phong <= CURRENT_DATE " +
        "       AND dp.ngay_tra_phong >= CURRENT_DATE " +
        "     LIMIT 1) as tenKhachHang " +
        "FROM phong p " +
        "JOIN loai_phong lp ON p.ma_loai = lp.ma_loai " +
        "ORDER BY p.tang, p.so_phong", 
        nativeQuery = true)
    List<Map<String, Object>> getSoDoPhongNative();
}