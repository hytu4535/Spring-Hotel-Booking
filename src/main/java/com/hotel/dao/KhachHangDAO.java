package com.hotel.dao;

import com.hotel.model.KhachHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KhachHangDAO extends BaseDAO {

    // Hàm lấy thông tin khách hàng bằng ID (Dùng cho chức năng Tra cứu đơn)
    public KhachHang findById(int maKh) {
        String sql = "SELECT * FROM khach_hang WHERE ma_kh = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maKh);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setMaKh(rs.getInt("ma_kh"));
                    kh.setHoTen(rs.getString("ho_ten"));
                    kh.setEmail(rs.getString("email"));
                    kh.setSoDienThoai(rs.getString("so_dien_thoai"));
                    kh.setCccd(rs.getString("cccd"));
                    kh.setDiaChi(rs.getString("dia_chi"));
                    kh.setNgayTao(rs.getTimestamp("ngay_tao"));
                    return kh;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Nếu cần các hàm khác như insert, update... thì cứ viết thêm xuống dưới đây
}