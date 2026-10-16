package com.hotel.dao;

import com.hotel.model.TaiKhoan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO extends BaseDAO {

    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        String sql = "SELECT * FROM tai_khoan WHERE ten_dang_nhap = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TaiKhoan findById(int maTk) {
        String sql = "SELECT * FROM tai_khoan WHERE ma_tk = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maTk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TaiKhoan> findAll() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM tai_khoan ORDER BY ngay_tao DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(TaiKhoan tk) {
        String sql = "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, so_dien_thoai, vai_tro, trang_thai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getHoTen());
            ps.setString(4, tk.getEmail());
            ps.setString(5, tk.getSoDienThoai());
            ps.setString(6, tk.getVaiTro());
            ps.setInt(7, tk.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(TaiKhoan tk) {
        String sql = "UPDATE tai_khoan SET ho_ten = ?, email = ?, so_dien_thoai = ?, mat_khau = ?, vai_tro = ?, trang_thai = ? "
                + "WHERE ma_tk = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tk.getHoTen());
            ps.setString(2, tk.getEmail());
            ps.setString(3, tk.getSoDienThoai());
            ps.setString(4, tk.getMatKhau());
            ps.setString(5, tk.getVaiTro());
            ps.setInt(6, tk.getTrangThai());
            ps.setInt(7, tk.getMaTk());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMatKhau(int maTk, String matKhauHash) {
        String sql = "UPDATE tai_khoan SET mat_khau = ? WHERE ma_tk = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matKhauHash);
            ps.setInt(2, maTk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTrangThai(int maTk, int trangThai) {
        String sql = "UPDATE tai_khoan SET trang_thai = ? WHERE ma_tk = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, trangThai);
            ps.setInt(2, maTk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsTenDangNhap(String tenDangNhap, Integer excludeMaTk) {
        String sql = excludeMaTk == null
                ? "SELECT 1 FROM tai_khoan WHERE ten_dang_nhap = ?"
                : "SELECT 1 FROM tai_khoan WHERE ten_dang_nhap = ? AND ma_tk <> ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            if (excludeMaTk != null) {
                ps.setInt(2, excludeMaTk);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsEmail(String email, Integer excludeMaTk) {
        String sql = excludeMaTk == null
                ? "SELECT 1 FROM tai_khoan WHERE email = ?"
                : "SELECT 1 FROM tai_khoan WHERE email = ? AND ma_tk <> ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            if (excludeMaTk != null) {
                ps.setInt(2, excludeMaTk);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private TaiKhoan mapResultSet(ResultSet rs) throws SQLException {
        TaiKhoan tk = new TaiKhoan();
        tk.setMaTk(rs.getInt("ma_tk"));
        tk.setTenDangNhap(rs.getString("ten_dang_nhap"));
        tk.setMatKhau(rs.getString("mat_khau"));
        tk.setHoTen(rs.getString("ho_ten"));
        tk.setEmail(rs.getString("email"));
        tk.setSoDienThoai(rs.getString("so_dien_thoai"));
        tk.setVaiTro(rs.getString("vai_tro"));
        tk.setNgayTao(rs.getTimestamp("ngay_tao"));
        tk.setTrangThai(rs.getInt("trang_thai"));
        return tk;
    }
}

