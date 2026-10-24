package com.hotel.dao;

import com.hotel.model.DichVu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DichVuDAO extends BaseDAO {

    public List<DichVu> findAll() {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM dich_vu ORDER BY ma_dv DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DichVu dv = new DichVu();
                dv.setMaDv(rs.getInt("ma_dv"));
                dv.setTenDv(rs.getString("ten_dv"));
                dv.setGia(rs.getBigDecimal("don_gia"));
                dv.setDonViTinh(rs.getString("don_vi_tinh"));
                dv.setMoTa(rs.getString("mo_ta"));
                dv.setTrangThai(rs.getInt("trang_thai"));
                list.add(dv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public DichVu findById(int maDv) {
        String sql = "SELECT * FROM dich_vu WHERE ma_dv = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DichVu dv = new DichVu();
                    dv.setMaDv(rs.getInt("ma_dv"));
                    dv.setTenDv(rs.getString("ten_dv"));
                    dv.setGia(rs.getBigDecimal("don_gia"));
                    dv.setDonViTinh(rs.getString("don_vi_tinh"));
                    dv.setMoTa(rs.getString("mo_ta"));
                    dv.setTrangThai(rs.getInt("trang_thai"));
                    return dv;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(DichVu dv) {
        String sql = "INSERT INTO dich_vu (ten_dv, don_gia, don_vi_tinh, mo_ta, trang_thai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dv.getTenDv());
            ps.setBigDecimal(2, dv.getGia());
            ps.setString(3, dv.getDonViTinh());
            ps.setString(4, dv.getMoTa());
            ps.setInt(5, dv.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(DichVu dv) {
        String sql = "UPDATE dich_vu SET ten_dv = ?, don_gia = ?, don_vi_tinh = ?, mo_ta = ?, trang_thai = ? WHERE ma_dv = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dv.getTenDv());
            ps.setBigDecimal(2, dv.getGia());
            ps.setString(3, dv.getDonViTinh());
            ps.setString(4, dv.getMoTa());
            ps.setInt(5, dv.getTrangThai());
            ps.setInt(6, dv.getMaDv());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maDv) {
        String sql = "DELETE FROM dich_vu WHERE ma_dv = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDv);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
