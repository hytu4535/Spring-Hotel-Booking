package com.hotel.dao;

import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.DatPhong;
import com.hotel.model.KhachHang;
import com.hotel.util.MaTraCuuGenerator;

import java.sql.*;
import java.util.List;

public class DatPhongDAO extends BaseDAO {

    // Trả về mã tra cứu nếu thành công, trả về null nếu thất bại hoặc trùng lịch
    public String taoDonDatPhongTransaction(KhachHang khachHang, DatPhong datPhong, List<ChiTietDatPhong> danhSachChiTiet) {
        Connection conn = null;
        PreparedStatement psKhach = null;
        PreparedStatement psDatPhong = null;
        PreparedStatement psChiTiet = null;
        ResultSet rs = null;

        String maTraCuu = MaTraCuuGenerator.generate(8); // VD: HTL2610A7X2

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION

            // 1. XỬ LÝ KHÁCH HÀNG
            int maKh = -1;
            // Kiểm tra email đã tồn tại chưa
            String checkEmailSql = "SELECT ma_kh FROM khach_hang WHERE email = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(checkEmailSql)) {
                psCheck.setString(1, khachHang.getEmail());
                rs = psCheck.executeQuery();
                if (rs.next()) {
                    maKh = rs.getInt("ma_kh");
                }
            }

            // Nếu chưa tồn tại, insert khách hàng mới
            if (maKh == -1) {
                String insertKhach = "INSERT INTO khach_hang (ho_ten, email, so_dien_thoai, cccd, dia_chi) VALUES (?, ?, ?, ?, ?)";
                psKhach = conn.prepareStatement(insertKhach, Statement.RETURN_GENERATED_KEYS);
                psKhach.setString(1, khachHang.getHoTen());
                psKhach.setString(2, khachHang.getEmail());
                psKhach.setString(3, khachHang.getSoDienThoai());
                psKhach.setString(4, khachHang.getCccd());
                psKhach.setString(5, khachHang.getDiaChi());
                psKhach.executeUpdate();
                
                rs = psKhach.getGeneratedKeys();
                if (rs.next()) {
                    maKh = rs.getInt(1);
                }
            }

            // 2. INSERT ĐẶT PHÒNG
            String insertDatPhong = "INSERT INTO dat_phong (ma_tra_cuu, ma_kh, ngay_nhan_phong, ngay_tra_phong, trang_thai, tien_phong) VALUES (?, ?, ?, ?, 'CHO_XAC_NHAN', ?)";
            psDatPhong = conn.prepareStatement(insertDatPhong, Statement.RETURN_GENERATED_KEYS);
            psDatPhong.setString(1, maTraCuu);
            psDatPhong.setInt(2, maKh);
            psDatPhong.setDate(3, datPhong.getNgayNhanPhong());
            psDatPhong.setDate(4, datPhong.getNgayTraPhong());
            psDatPhong.setBigDecimal(5, datPhong.getTienPhong());
            psDatPhong.executeUpdate();

            int maDp = -1;
            rs = psDatPhong.getGeneratedKeys();
            if (rs.next()) {
                maDp = rs.getInt(1);
            }

            // 3. INSERT CHI TIẾT ĐẶT PHÒNG (Nhiều phòng)
            String insertChiTiet = "INSERT INTO chi_tiet_dat_phong (ma_dp, ma_phong, gia_phong, so_dem, thanh_tien) VALUES (?, ?, ?, ?, ?)";
            psChiTiet = conn.prepareStatement(insertChiTiet);

            for (ChiTietDatPhong ct : danhSachChiTiet) {
                // Check an toàn: phòng này có bị ai khác đặt trùng trong khoảng ngày này không? (Tránh race condition)
                String checkPhongSql = "SELECT 1 FROM chi_tiet_dat_phong ct JOIN dat_phong dp ON ct.ma_dp = dp.ma_dp " +
                                       "WHERE dp.trang_thai <> 'DA_HUY' AND ct.ma_phong = ? " +
                                       "AND dp.ngay_nhan_phong < ? AND dp.ngay_tra_phong > ?";
                try (PreparedStatement psCheckPhong = conn.prepareStatement(checkPhongSql)) {
                    psCheckPhong.setInt(1, ct.getMaPhong());
                    psCheckPhong.setDate(2, datPhong.getNgayTraPhong());
                    psCheckPhong.setDate(3, datPhong.getNgayNhanPhong());
                    ResultSet rsCheck = psCheckPhong.executeQuery();
                    if (rsCheck.next()) {
                        // Đã có người nhanh tay đặt phòng này rồi
                        conn.rollback(); // Hủy transaction
                        return null; // Trả về null để báo lỗi trùng lịch
                    }
                }

                // Nếu an toàn, add batch
                psChiTiet.setInt(1, maDp);
                psChiTiet.setInt(2, ct.getMaPhong());
                psChiTiet.setBigDecimal(3, ct.getGiaPhong());
                psChiTiet.setInt(4, ct.getSoDem());
                psChiTiet.setBigDecimal(5, ct.getThanhTien());
                psChiTiet.addBatch();
            }
            
            // Thực thi batch insert chi tiết
            psChiTiet.executeBatch();

            // COMMIT TRANSACTION
            conn.commit();
            return maTraCuu; // Thành công trả về mã tra cứu cho khách xem

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // CÓ LỖI -> ROLLBACK QUAY LẠI TỪ ĐẦU
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // Trả lại autocommit mặc định
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(rs, psKhach, psDatPhong, psChiTiet, conn);
        }
    }

    // Lấy thông tin đơn bằng Mã tra cứu (Dành cho chức năng Tra Cứu)
    public DatPhong findByMaTraCuu(String maTraCuu) {
        String sql = "SELECT * FROM dat_phong WHERE ma_tra_cuu = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTraCuu);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DatPhong dp = new DatPhong();
                dp.setMaDp(rs.getInt("ma_dp"));
                dp.setMaTraCuu(rs.getString("ma_tra_cuu"));
                dp.setMaKh(rs.getInt("ma_kh"));
                dp.setNgayNhanPhong(rs.getDate("ngay_nhan_phong"));
                dp.setNgayTraPhong(rs.getDate("ngay_tra_phong"));
                dp.setTrangThai(rs.getString("trang_thai"));
                dp.setTienPhong(rs.getBigDecimal("tien_phong"));
                dp.setTongTien(rs.getBigDecimal("tong_tien"));
                return dp;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Đổi trạng thái đơn (Dùng cho Hủy đơn, Duyệt đơn)
    public boolean updateTrangThai(int maDp, String trangThaiMoi) {
        String sql = "UPDATE dat_phong SET trang_thai = ? WHERE ma_dp = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThaiMoi);
            ps.setInt(2, maDp);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // 1. Lấy danh sách toàn bộ đơn (kèm tên khách hàng)
    public List<com.hotel.model.DatPhongDTO> findAllWithKhachHang() {
        List<com.hotel.model.DatPhongDTO> list = new java.util.ArrayList<>();
        String sql = "SELECT dp.*, kh.ho_ten AS kh_ten, kh.email AS kh_email, kh.so_dien_thoai AS kh_sdt, tk.ho_ten AS nv_ten " +
                     "FROM dat_phong dp " +
                     "JOIN khach_hang kh ON dp.ma_kh = kh.ma_kh " +
                     "LEFT JOIN tai_khoan tk ON dp.ma_nv_xu_ly = tk.ma_tk " +
                     "ORDER BY dp.ngay_dat DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             while (rs.next()) {
                 com.hotel.model.DatPhongDTO dto = new com.hotel.model.DatPhongDTO();
                 dto.setMaDp(rs.getInt("ma_dp"));
                 dto.setMaTraCuu(rs.getString("ma_tra_cuu"));
                 dto.setMaKh(rs.getInt("ma_kh"));
                 dto.setNgayDat(rs.getTimestamp("ngay_dat"));
                 dto.setNgayNhanPhong(rs.getDate("ngay_nhan_phong"));
                 dto.setNgayTraPhong(rs.getDate("ngay_tra_phong"));
                 dto.setTrangThai(rs.getString("trang_thai"));
                 dto.setTongTien(rs.getBigDecimal("tong_tien"));
                 dto.setTienPhong(rs.getBigDecimal("tien_phong"));
                 dto.setTienDichVu(rs.getBigDecimal("tien_dich_vu"));
                 
                 // DTO fields
                 dto.setTenKhachHang(rs.getString("kh_ten"));
                 dto.setEmailKhachHang(rs.getString("kh_email"));
                 dto.setSdtKhachHang(rs.getString("kh_sdt"));
                 dto.setTenNhanVienXuLy(rs.getString("nv_ten"));
                 list.add(dto);
             }
        } catch(SQLException e){ e.printStackTrace();}
        return list;
    }

    // 2. Transaction Cập nhật Trạng thái Đơn hàng VÀ Trạng thái Phòng tương ứng
    public boolean updateTrangThaiTransaction(int maDp, String newTrangThai, Integer maNvXuLy) {
        Connection conn = null;
        PreparedStatement psDp = null;
        PreparedStatement psPhong = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION
            
            // A. Cập nhật Đặt Phòng
            String sqlDp = "UPDATE dat_phong SET trang_thai = ?, ma_nv_xu_ly = ? WHERE ma_dp = ?";
            psDp = conn.prepareStatement(sqlDp);
            psDp.setString(1, newTrangThai);
            if (maNvXuLy != null) psDp.setInt(2, maNvXuLy);
            else psDp.setNull(2, java.sql.Types.INTEGER);
            psDp.setInt(3, maDp);
            if (psDp.executeUpdate() == 0) {
                conn.rollback();
                return false;
            }
            
            // B. Quy đổi Trạng thái Phòng theo Trạng thái Đơn
            String phongTrangThai = null;
            if (newTrangThai.equals("DA_XAC_NHAN")) phongTrangThai = "DA_DAT";
            else if (newTrangThai.equals("DA_NHAN_PHONG")) phongTrangThai = "DANG_O";
            else if (newTrangThai.equals("DA_TRA_PHONG") || newTrangThai.equals("DA_HUY")) phongTrangThai = "TRONG";
            
            // C. Cập nhật trạng thái các Phòng nằm trong Đơn này
            if (phongTrangThai != null) {
                String sqlPhong = "UPDATE phong SET trang_thai = ? WHERE ma_phong IN " +
                                  "(SELECT ma_phong FROM chi_tiet_dat_phong WHERE ma_dp = ?)";
                psPhong = conn.prepareStatement(sqlPhong);
                psPhong.setString(1, phongTrangThai);
                psPhong.setInt(2, maDp);
                psPhong.executeUpdate();
            }
            
            conn.commit(); // THÀNH CÔNG
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) {}
            close(psPhong, psDp, conn);
        }
    }
}