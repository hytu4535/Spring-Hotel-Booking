package com.hotel.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKeDAO extends BaseDAO {

    public BigDecimal getDoanhThuHomNay() {
        String sql = "SELECT SUM(tong_tien) FROM dat_phong WHERE DATE(ngay_dat) = CURDATE() AND trang_thai <> 'DA_HUY'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal sum = rs.getBigDecimal(1);
                return sum != null ? sum : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getDoanhThuThangNay() {
        String sql = "SELECT SUM(tong_tien) FROM dat_phong WHERE MONTH(ngay_dat) = MONTH(CURDATE()) AND YEAR(ngay_dat) = YEAR(CURDATE()) AND trang_thai <> 'DA_HUY'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal sum = rs.getBigDecimal(1);
                return sum != null ? sum : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public int getTongSoDon() {
        String sql = "SELECT COUNT(*) FROM dat_phong WHERE trang_thai <> 'DA_HUY'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Object> getThongKePhong() {
        Map<String, Object> stats = new HashMap<>();
        int totalRooms = 0;
        int occupiedRooms = 0;
        int availableRooms = 0;
        int maintenanceRooms = 0;

        String sql = "SELECT p.ma_phong, p.trang_thai, " +
                     "(SELECT COUNT(*) FROM chi_tiet_dat_phong ct JOIN dat_phong dp ON ct.ma_dp = dp.ma_dp " +
                     " WHERE ct.ma_phong = p.ma_phong AND dp.trang_thai IN ('DA_XAC_NHAN', 'DA_NHAN_PHONG') " +
                     " AND dp.ngay_nhan_phong <= CURDATE() AND dp.ngay_tra_phong >= CURDATE()) as is_occupied " +
                     "FROM phong p";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                totalRooms++;
                String trangThai = rs.getString("trang_thai");
                int isOccupied = rs.getInt("is_occupied");

                if ("BAO_TRI".equals(trangThai)) {
                    maintenanceRooms++;
                } else if (isOccupied > 0 || "DANG_O".equals(trangThai)) {
                    occupiedRooms++;
                } else {
                    availableRooms++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        double occupancyRate = totalRooms > 0 ? (double) occupiedRooms / totalRooms * 100 : 0.0;

        stats.put("totalRooms", totalRooms);
        stats.put("occupiedRooms", occupiedRooms);
        stats.put("availableRooms", availableRooms);
        stats.put("maintenanceRooms", maintenanceRooms);
        stats.put("occupancyRate", Math.round(occupancyRate * 10.0) / 10.0);

        return stats;
    }

    public List<Map<String, Object>> getDoanhThuTheoNgay(int days) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT DATE(ngay_dat) as ngay, SUM(tong_tien) as doanh_thu, COUNT(*) as so_don " +
                     "FROM dat_phong " +
                     "WHERE ngay_dat >= DATE_SUB(CURDATE(), INTERVAL ? DAY) AND trang_thai <> 'DA_HUY' " +
                     "GROUP BY DATE(ngay_dat) " +
                     "ORDER BY ngay ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("ngay", rs.getDate("ngay"));
                    row.put("doanhThu", rs.getBigDecimal("doanh_thu"));
                    row.put("soDon", rs.getInt("so_don"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getDoanhThuTheoThang(int year) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT MONTH(ngay_dat) as thang, SUM(tong_tien) as doanh_thu, COUNT(*) as so_don " +
                     "FROM dat_phong " +
                     "WHERE YEAR(ngay_dat) = ? AND trang_thai <> 'DA_HUY' " +
                     "GROUP BY MONTH(ngay_dat) " +
                     "ORDER BY thang ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("thang", rs.getInt("thang"));
                    row.put("doanhThu", rs.getBigDecimal("doanh_thu"));
                    row.put("soDon", rs.getInt("so_don"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getHoatDongGganDay(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT dp.ngay_dat, dp.trang_thai, kh.ho_ten, " +
                     "(SELECT GROUP_CONCAT(p.so_phong SEPARATOR ', ') FROM chi_tiet_dat_phong ct JOIN phong p ON ct.ma_phong = p.ma_phong WHERE ct.ma_dp = dp.ma_dp) as so_phong " +
                     "FROM dat_phong dp " +
                     "JOIN khach_hang kh ON dp.ma_kh = kh.ma_kh " +
                     "ORDER BY dp.ngay_dat DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("ngayDat", rs.getTimestamp("ngay_dat"));
                    row.put("trangThai", rs.getString("trang_thai"));
                    row.put("hoTen", rs.getString("ho_ten"));
                    row.put("soPhong", rs.getString("so_phong"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
