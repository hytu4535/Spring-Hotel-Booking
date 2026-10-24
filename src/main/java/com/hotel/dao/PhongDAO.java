package com.hotel.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hotel.model.PhongSoDoDTO;
import com.hotel.model.PhongTrongDTO;

public class PhongDAO extends BaseDAO {

    public List<PhongTrongDTO> timPhongTrong(Date ngayNhanPhong, Date ngayTraPhong) {
        List<PhongTrongDTO> list = new ArrayList<>();
        String sql = "SELECT p.ma_phong, p.so_phong, p.tang, lp.ma_loai, lp.ten_loai, lp.gia_mac_dinh, lp.suc_chua, lp.dien_tich, lp.mo_ta, lp.hinh_anh " +
                "FROM phong p JOIN loai_phong lp ON p.ma_loai = lp.ma_loai " +
                "WHERE p.trang_thai <> 'BAO_TRI' " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM chi_tiet_dat_phong ct " +
                "    JOIN dat_phong dp ON ct.ma_dp = dp.ma_dp " +
                "    WHERE ct.ma_phong = p.ma_phong " +
                "      AND dp.trang_thai <> 'DA_HUY' " +
                "      AND dp.ngay_nhan_phong < ? " +
                "      AND dp.ngay_tra_phong > ? " +
                ") " +
                "ORDER BY lp.gia_mac_dinh ASC, p.so_phong ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, ngayTraPhong);
            ps.setDate(2, ngayNhanPhong);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhongTrongDTO dto = new PhongTrongDTO();
                    dto.setMaPhong(rs.getInt("ma_phong"));
                    dto.setMaLoai(rs.getInt("ma_loai"));
                    dto.setSoPhong(rs.getString("so_phong"));
                    dto.setTang(rs.getInt("tang"));
                    dto.setTenLoai(rs.getString("ten_loai"));
                    dto.setGiaPhong(rs.getBigDecimal("gia_mac_dinh"));
                    dto.setSucChua(rs.getInt("suc_chua"));
                    dto.setDienTich(rs.getBigDecimal("dien_tich"));
                    dto.setMoTa(rs.getString("mo_ta"));
                    dto.setHinhAnh(rs.getString("hinh_anh"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public PhongTrongDTO findPhongChiTiet(int maPhong) {
        String sql = "SELECT p.ma_phong, p.so_phong, p.tang, p.trang_thai, lp.ma_loai, lp.ten_loai, lp.gia_mac_dinh, lp.suc_chua, lp.dien_tich, lp.mo_ta, lp.hinh_anh " +
                "FROM phong p JOIN loai_phong lp ON p.ma_loai = lp.ma_loai WHERE p.ma_phong = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPhong);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PhongTrongDTO dto = new PhongTrongDTO();
                    dto.setMaPhong(rs.getInt("ma_phong"));
                    dto.setMaLoai(rs.getInt("ma_loai"));
                    dto.setSoPhong(rs.getString("so_phong"));
                    dto.setTang(rs.getInt("tang"));
                    dto.setTenLoai(rs.getString("ten_loai"));
                    dto.setGiaPhong(rs.getBigDecimal("gia_mac_dinh"));
                    dto.setSucChua(rs.getInt("suc_chua"));
                    dto.setDienTich(rs.getBigDecimal("dien_tich"));
                    dto.setMoTa(rs.getString("mo_ta"));
                    dto.setHinhAnh(rs.getString("hinh_anh"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<PhongSoDoDTO> getSoDoPhong() {
        List<PhongSoDoDTO> list = new ArrayList<>();
        // Truy vấn lấy danh sách phòng và thông tin Đơn active hôm nay (nếu có)
        String sql = "SELECT p.ma_phong, p.so_phong, p.tang, p.trang_thai, lp.ten_loai, " +
                     "(SELECT dp.ma_dp FROM chi_tiet_dat_phong ct JOIN dat_phong dp ON ct.ma_dp = dp.ma_dp " +
                     " WHERE ct.ma_phong = p.ma_phong AND dp.trang_thai IN ('DA_XAC_NHAN', 'DA_NHAN_PHONG') " +
                     " AND dp.ngay_nhan_phong <= CURDATE() AND dp.ngay_tra_phong >= CURDATE() LIMIT 1) as ma_dp, " +
                     "(SELECT kh.ho_ten FROM chi_tiet_dat_phong ct JOIN dat_phong dp ON ct.ma_dp = dp.ma_dp " +
                     " JOIN khach_hang kh ON dp.ma_kh = kh.ma_kh " +
                     " WHERE ct.ma_phong = p.ma_phong AND dp.trang_thai IN ('DA_XAC_NHAN', 'DA_NHAN_PHONG') " +
                     " AND dp.ngay_nhan_phong <= CURDATE() AND dp.ngay_tra_phong >= CURDATE() LIMIT 1) as ten_khach " +
                     "FROM phong p JOIN loai_phong lp ON p.ma_loai = lp.ma_loai " +
                     "ORDER BY p.tang, p.so_phong";
                     
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
             while (rs.next()) {
                 PhongSoDoDTO dto = new PhongSoDoDTO();
                 dto.setMaPhong(rs.getInt("ma_phong"));
                 dto.setSoPhong(rs.getString("so_phong"));
                 dto.setTang(rs.getInt("tang"));
                 dto.setTrangThai(rs.getString("trang_thai"));
                 dto.setTenLoaiPhong(rs.getString("ten_loai"));
                 
                 int maDp = rs.getInt("ma_dp");
                 if (!rs.wasNull()) {
                     dto.setMaDpHienTai(maDp);
                     dto.setTenKhachHang(rs.getString("ten_khach"));
                 }
                 list.add(dto);
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<PhongTrongDTO> findAllPhong() {
        List<PhongTrongDTO> list = new ArrayList<>();
        String sql = "SELECT p.ma_phong, p.so_phong, p.tang, p.trang_thai, lp.ma_loai, lp.ten_loai, lp.gia_mac_dinh " +
                     "FROM phong p JOIN loai_phong lp ON p.ma_loai = lp.ma_loai ORDER BY p.tang, p.so_phong";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhongTrongDTO dto = new PhongTrongDTO();
                dto.setMaPhong(rs.getInt("ma_phong"));
                dto.setSoPhong(rs.getString("so_phong"));
                dto.setTang(rs.getInt("tang"));
                dto.setTenLoai(rs.getString("ten_loai"));
                dto.setGiaPhong(rs.getBigDecimal("gia_mac_dinh"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}