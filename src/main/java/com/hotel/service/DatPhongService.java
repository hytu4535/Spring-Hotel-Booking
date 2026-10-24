package com.hotel.service;

import com.hotel.model.*;
import com.hotel.repository.*;
import com.hotel.util.MaTraCuuGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DatPhongService {

    private final DatPhongRepository datPhongRepository;
    private final KhachHangRepository khachHangRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final PhongRepository phongRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final EntityManager entityManager;

    public DatPhongService(DatPhongRepository datPhongRepository,
                           KhachHangRepository khachHangRepository,
                           ChiTietDatPhongRepository chiTietDatPhongRepository,
                           PhongRepository phongRepository,
                           ThanhToanRepository thanhToanRepository,
                           EntityManager entityManager) {
        this.datPhongRepository = datPhongRepository;
        this.khachHangRepository = khachHangRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
        this.phongRepository = phongRepository;
        this.thanhToanRepository = thanhToanRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public String taoDonDatPhong(KhachHang khRequest, DatPhong dpRequest, List<ChiTietDatPhong> danhSachChiTiet, TaiKhoan currentUser) {
        return taoDonDatPhong(khRequest, dpRequest, danhSachChiTiet, null, currentUser);
    }

    @Transactional
    public String taoDonDatPhong(KhachHang khRequest, DatPhong dpRequest, List<ChiTietDatPhong> danhSachChiTiet, List<CartServiceItem> danhSachDichVu, TaiKhoan currentUser) {
        if (khRequest == null || dpRequest == null || danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
            return null;
        }

        // 1. Xử lý Khách hàng
        KhachHang kh;
        if (currentUser != null) {
            Optional<KhachHang> khOpt = khachHangRepository.findByMaTk(currentUser.getMaTk());
            if (khOpt.isPresent()) {
                kh = khOpt.get();
                kh.setHoTen(khRequest.getHoTen());
                kh.setSoDienThoai(khRequest.getSoDienThoai());
                if (khRequest.getCccd() != null) kh.setCccd(khRequest.getCccd());
                if (khRequest.getDiaChi() != null) kh.setDiaChi(khRequest.getDiaChi());
                khachHangRepository.save(kh);
            } else {
                kh = khachHangRepository.findByEmailIgnoreCase(khRequest.getEmail().trim()).orElseGet(() -> {
                    khRequest.setEmail(khRequest.getEmail().trim());
                    khRequest.setNgayTao(new java.sql.Timestamp(System.currentTimeMillis()));
                    return khachHangRepository.save(khRequest);
                });
                kh.setMaTk(currentUser.getMaTk());
                khachHangRepository.save(kh);
            }
        } else {
            kh = khachHangRepository.findByEmailIgnoreCase(khRequest.getEmail().trim())
                    .orElseGet(() -> {
                        khRequest.setEmail(khRequest.getEmail().trim());
                        khRequest.setNgayTao(new java.sql.Timestamp(System.currentTimeMillis()));
                        return khachHangRepository.save(khRequest);
                    });
        }

        // 2. Kiểm tra trùng lịch
        for (ChiTietDatPhong ct : danhSachChiTiet) {
            long overlapCount = chiTietDatPhongRepository.countOverlappingBookings(
                    ct.getPhong().getMaPhong(),
                    dpRequest.getNgayNhanPhong(),
                    dpRequest.getNgayTraPhong()
            );
            if (overlapCount > 0) {
                throw new IllegalStateException("Phòng " + ct.getPhong().getMaPhong() + " đã có người đặt trong khoảng thời gian này.");
            }
        }

        // 3. Tạo mã tra cứu duy nhất
        String maTraCuu = MaTraCuuGenerator.generate(8);
        while (datPhongRepository.findByMaTraCuu(maTraCuu).isPresent()) {
            maTraCuu = MaTraCuuGenerator.generate(8);
        }

        dpRequest.setMaTraCuu(maTraCuu);
        dpRequest.setKhachHang(kh);
        dpRequest.setTrangThai("CHO_XAC_NHAN");
        dpRequest.setNgayDat(new java.sql.Timestamp(System.currentTimeMillis()));
        
        if (dpRequest.getTienPhong() == null) {
            dpRequest.setTienPhong(BigDecimal.ZERO);
        }
        if (dpRequest.getTienDichVu() == null) {
            dpRequest.setTienDichVu(BigDecimal.ZERO);
        }
        if (dpRequest.getTongTien() == null || dpRequest.getTongTien().compareTo(BigDecimal.ZERO) == 0) {
            dpRequest.setTongTien(dpRequest.getTienPhong().add(dpRequest.getTienDichVu()));
        }

        DatPhong savedDp = datPhongRepository.save(dpRequest);

        // 4. Lưu chi tiết đặt phòng
        for (ChiTietDatPhong ct : danhSachChiTiet) {
            Phong phong = phongRepository.findById(ct.getPhong().getMaPhong())
                    .orElseThrow(() -> new IllegalArgumentException("Phòng không tồn tại"));

            ct.setDatPhong(savedDp);
            ct.setPhong(phong);
            chiTietDatPhongRepository.save(ct);
        }

        // 5. Lưu dịch vụ phát sinh vào dat_dich_vu nếu có
        if (danhSachDichVu != null && !danhSachDichVu.isEmpty()) {
            BigDecimal tongTienDv = BigDecimal.ZERO;
            for (CartServiceItem s : danhSachDichVu) {
                entityManager.createNativeQuery(
                        "INSERT INTO dat_dich_vu (ma_dp, ma_dv, so_luong, don_gia, ngay_su_dung, thanh_tien) VALUES (?, ?, ?, ?, ?, ?)")
                        .setParameter(1, savedDp.getMaDp())
                        .setParameter(2, s.getMaDv())
                        .setParameter(3, s.getSoLuong())
                        .setParameter(4, s.getGia())
                        .setParameter(5, dpRequest.getNgayNhanPhong())
                        .setParameter(6, s.getThanhTien())
                        .executeUpdate();
                tongTienDv = tongTienDv.add(s.getThanhTien());
            }
            if (tongTienDv.compareTo(BigDecimal.ZERO) > 0) {
                savedDp.setTienDichVu(tongTienDv);
                savedDp.setTongTien(savedDp.getTienPhong().add(tongTienDv));
                datPhongRepository.save(savedDp);
            }
        }

        return maTraCuu;
    }

    @Transactional(readOnly = true)
    public DatPhong findByMaTraCuu(String maTraCuu) {
        return datPhongRepository.findByMaTraCuu(maTraCuu).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<DatPhong> getBookingsByCustomerMaKh(Integer maKh) {
        return datPhongRepository.findByKhachHang_MaKhOrderByNgayDatDesc(maKh);
    }

    @Transactional
    public Map<String, Object> themDichVuPhatSinh(Integer maDp, Integer maDv, Integer soLuong, java.sql.Date ngaySuDung, TaiKhoan currentUser) {
        if (maDp == null || maDv == null || soLuong == null || soLuong < 1) {
            throw new IllegalArgumentException("Dữ liệu đầu vào không hợp lệ.");
        }

        DatPhong dp = datPhongRepository.findById(maDp)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt phòng với ID: " + maDp));

        String trangThai = dp.getTrangThai();
        List<String> allowedStatuses = Arrays.asList("CHO_XAC_NHAN", "DA_XAC_NHAN", "DA_NHAN_PHONG", "DANG_O");
        if (trangThai == null || !allowedStatuses.contains(trangThai)) {
            throw new IllegalStateException("Không thể thêm dịch vụ cho đơn đặt phòng ở trạng thái: " + trangThai);
        }

        com.hotel.dao.DichVuDAO dichVuDAO = new com.hotel.dao.DichVuDAO();
        com.hotel.model.DichVu dv = dichVuDAO.findById(maDv);
        if (dv == null) {
            throw new IllegalArgumentException("Không tìm thấy dịch vụ với ID: " + maDv);
        }

        BigDecimal giaDv = dv.getGia() != null ? dv.getGia() : BigDecimal.ZERO;
        BigDecimal thanhTien = giaDv.multiply(BigDecimal.valueOf(soLuong));
        Integer maNvGhiNhan = (currentUser != null) ? currentUser.getMaTk() : null;

        String insertSql = "INSERT INTO dat_dich_vu (ma_dp, ma_dv, so_luong, don_gia, ngay_su_dung, thanh_tien, ma_nv_ghi_nhan) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (java.sql.Connection conn = com.hotel.util.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, maDp);
            ps.setInt(2, maDv);
            ps.setInt(3, soLuong);
            ps.setBigDecimal(4, giaDv);
            ps.setDate(5, ngaySuDung != null ? ngaySuDung : new java.sql.Date(System.currentTimeMillis()));
            ps.setBigDecimal(6, thanhTien);
            if (maNvGhiNhan != null && maNvGhiNhan > 0) {
                ps.setInt(7, maNvGhiNhan);
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu dịch vụ phát sinh vào cơ sở dữ liệu: " + e.getMessage(), e);
        }

        // Cập nhật lại tien_dich_vu và tong_tien trong dat_phong
        BigDecimal tienDichVuMoi = BigDecimal.ZERO;
        String sumSql = "SELECT COALESCE(SUM(thanh_tien), 0) FROM dat_dich_vu WHERE ma_dp = ?";
        try (java.sql.Connection conn = com.hotel.util.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sumSql)) {
            ps.setInt(1, maDp);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tienDichVuMoi = rs.getBigDecimal(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dp.setTienDichVu(tienDichVuMoi);
        BigDecimal tienPhong = dp.getTienPhong() != null ? dp.getTienPhong() : BigDecimal.ZERO;
        dp.setTongTien(tienPhong.add(tienDichVuMoi));
        datPhongRepository.save(dp);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Thêm dịch vụ thành công!");
        result.put("tongTien", dp.getTongTien());
        result.put("tienDichVu", dp.getTienDichVu());
        return result;
    }

    @Transactional
    public Map<String, Object> hoanTienVaHuyDon(Integer maDp, BigDecimal soTienHoan, String phuongThuc, String ghiChu, TaiKhoan currentUser) {
        if (maDp == null) {
            throw new IllegalArgumentException("Mã đơn đặt phòng không được để trống.");
        }
        DatPhong dp = datPhongRepository.findById(maDp)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt phòng với ID: " + maDp));

        if ("DA_HUY".equals(dp.getTrangThai())) {
            throw new IllegalStateException("Đơn đặt phòng này đã bị hủy trước đó.");
        }

        // 1. Thêm mới bản ghi hoàn tiền vào bảng thanh_toan (Không sửa bản ghi cũ)
        if (soTienHoan != null && soTienHoan.compareTo(BigDecimal.ZERO) > 0) {
            ThanhToan thanhToan = new ThanhToan();
            thanhToan.setDatPhong(dp);
            thanhToan.setSoTien(soTienHoan);
            thanhToan.setLoaiThanhToan("HOAN_TIEN");
            thanhToan.setPhuongThuc(phuongThuc != null && !phuongThuc.isEmpty() ? phuongThuc : "TIEN_MAT");
            thanhToan.setMaNvThu(currentUser != null ? currentUser.getMaTk() : null);
            thanhToan.setGhiChu(ghiChu != null && !ghiChu.isEmpty() ? ghiChu : "Hoàn tiền hủy đơn đặt phòng");
            thanhToan.setNgayThanhToan(new Timestamp(System.currentTimeMillis()));
            thanhToanRepository.save(thanhToan);
        }

        // 2. Cập nhật trạng thái đơn thành DA_HUY
        dp.setTrangThai("DA_HUY");
        datPhongRepository.save(dp);

        // 3. Trả lại phòng về trạng thái TRONG
        List<ChiTietDatPhong> chiTiets = chiTietDatPhongRepository.findByDatPhong_MaDp(maDp);
        for (ChiTietDatPhong ct : chiTiets) {
            if (ct.getPhong() != null) {
                Phong phong = ct.getPhong();
                phong.setTrangThai("TRONG");
                phongRepository.save(phong);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Đã hoàn tiền và hủy đơn thành công!");
        return result;
    }
}
