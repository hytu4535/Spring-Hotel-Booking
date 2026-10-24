package com.hotel.dao;

import com.hotel.model.DatDichVu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatDichVuDAO extends BaseDAO {
    public List<DatDichVu> findByMaDp(int maDp) {
        List<DatDichVu> list = new ArrayList<>();
        String sql = "SELECT ddv.*, dv.ten_dv, tk.ho_ten as nv_ten " +
                     "FROM dat_dich_vu ddv " +
                     "JOIN dich_vu dv ON ddv.ma_dv = dv.ma_dv " +
                     "LEFT JOIN tai_khoan tk ON ddv.ma_nv_ghi_nhan = tk.ma_tk " +
                     "WHERE ddv.ma_dp = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDp);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DatDichVu item = new DatDichVu();
                    item.setMaDatDv(rs.getInt("ma_ddv"));
                    item.setMaDp(rs.getInt("ma_dp"));
                    item.setMaDv(rs.getInt("ma_dv"));
                    item.setTenDv(rs.getString("ten_dv"));
                    item.setSoLuong(rs.getInt("so_luong"));
                    item.setGiaTaiThoiDiem(rs.getBigDecimal("don_gia"));
                    item.setNgaySuDung(rs.getDate("ngay_su_dung"));
                    item.setThanhTien(rs.getBigDecimal("thanh_tien"));
                    int maNv = rs.getInt("ma_nv_ghi_nhan");
                    if (!rs.wasNull()) {
                        item.setMaNvGhiNhan(maNv);
                    }
                    item.setTenNhanVien(rs.getString("nv_ten"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
