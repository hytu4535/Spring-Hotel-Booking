package com.hotel.service;

import com.hotel.model.*;
import com.hotel.repository.*;
import com.hotel.util.MaTraCuuGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DatPhongService {

    private final DatPhongRepository datPhongRepository;
    private final KhachHangRepository khachHangRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final PhongRepository phongRepository;

    public DatPhongService(DatPhongRepository datPhongRepository,
                           KhachHangRepository khachHangRepository,
                           ChiTietDatPhongRepository chiTietDatPhongRepository,
                           PhongRepository phongRepository) {
        this.datPhongRepository = datPhongRepository;
        this.khachHangRepository = khachHangRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
        this.phongRepository = phongRepository;
    }

    @Transactional
    public String taoDonDatPhong(KhachHang khRequest, DatPhong dpRequest, List<ChiTietDatPhong> danhSachChiTiet, TaiKhoan currentUser) {
        if (khRequest == null || dpRequest == null || danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
            return null;
        }

        // 1. Xử lý Khách hàng (Nhóm 1: Chưa đăng nhập hoặc Nhóm 2: Đã đăng nhập)
        KhachHang kh;
        if (currentUser != null) {
            // Khách hàng đã đăng nhập (Nhóm 2)
            Optional<KhachHang> khOpt = khachHangRepository.findByMaTk(currentUser.getMaTk());
            if (khOpt.isPresent()) {
                kh = khOpt.get();
                // Cập nhật thông tin mới nhất từ form nếu có
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
            // Khách chưa đăng nhập (Nhóm 1)
            kh = khachHangRepository.findByEmailIgnoreCase(khRequest.getEmail().trim())
                    .orElseGet(() -> {
                        khRequest.setEmail(khRequest.getEmail().trim());
                        khRequest.setNgayTao(new java.sql.Timestamp(System.currentTimeMillis()));
                        return khachHangRepository.save(khRequest);
                    });
        }

        // 2. Kiểm tra trùng lịch (Room Overlap Check) trước khi tạo đơn
        for (ChiTietDatPhong ct : danhSachChiTiet) {
            long overlapCount = chiTietDatPhongRepository.countOverlappingBookings(
                    ct.getPhong().getMaPhong(),
                    dpRequest.getNgayNhanPhong(),
                    dpRequest.getNgayTraPhong()
            );
            if (overlapCount > 0) {
                // Trùng lịch -> rollback transaction tự động khi throw exception hoặc trả về null
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
        if (dpRequest.getTienDichVu() == null) {
            dpRequest.setTienDichVu(java.math.BigDecimal.ZERO);
        }
        if (dpRequest.getTongTien() == null || dpRequest.getTongTien().compareTo(java.math.BigDecimal.ZERO) == 0) {
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
}
