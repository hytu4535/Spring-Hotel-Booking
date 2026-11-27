package com.hotel.service;

import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.DatPhong;
import com.hotel.model.DatPhongDTO;
import com.hotel.model.Phong;
import com.hotel.event.BookingStatusEmailEvent;
import com.hotel.repository.ChiTietDatPhongRepository;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.PhongRepository;
import com.hotel.repository.ThanhToanRepository;
import com.hotel.model.ThanhToan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatPhongStaffService {

    private final DatPhongRepository datPhongRepository;
    private final PhongRepository phongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ThanhToanRepository thanhToanRepository;

    public DatPhongStaffService(DatPhongRepository datPhongRepository, PhongRepository phongRepository,
            ChiTietDatPhongRepository chiTietDatPhongRepository) {
        this(datPhongRepository, phongRepository, chiTietDatPhongRepository, null);
    }

    public DatPhongStaffService(DatPhongRepository datPhongRepository, PhongRepository phongRepository,
            ChiTietDatPhongRepository chiTietDatPhongRepository,
            ApplicationEventPublisher eventPublisher) {
        this.datPhongRepository = datPhongRepository;
        this.phongRepository = phongRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
        this.eventPublisher = eventPublisher;
        this.thanhToanRepository = null;
    }

    @Autowired
    public DatPhongStaffService(DatPhongRepository datPhongRepository, PhongRepository phongRepository,
            ChiTietDatPhongRepository chiTietDatPhongRepository,
            ThanhToanRepository thanhToanRepository,
            ApplicationEventPublisher eventPublisher) {
        this.datPhongRepository = datPhongRepository;
        this.phongRepository = phongRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
        this.thanhToanRepository = thanhToanRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<DatPhongDTO> findAllWithKhachHang() {
        return datPhongRepository.findAllByOrderByNgayDatDesc().stream().map(dp -> {
            DatPhongDTO dto = new DatPhongDTO();
            dto.setMaDp(dp.getMaDp());
            dto.setMaTraCuu(dp.getMaTraCuu());
            dto.setMaKh(dp.getMaKh());
            dto.setNgayDat(dp.getNgayDat());
            dto.setNgayNhanPhong(dp.getNgayNhanPhong());
            dto.setNgayTraPhong(dp.getNgayTraPhong());
            dto.setTrangThai(dp.getTrangThai());
            dto.setTongTien(dp.getTongTien());
            dto.setTienPhong(dp.getTienPhong());
            dto.setTienDichVu(dp.getTienDichVu());

            if (dp.getKhachHang() != null) {
                dto.setTenKhachHang(dp.getKhachHang().getHoTen());
                dto.setEmailKhachHang(dp.getKhachHang().getEmail());
                dto.setSdtKhachHang(dp.getKhachHang().getSoDienThoai());
            }

            if (dp.getNhanVienXuLy() != null) {
                dto.setTenNhanVienXuLy(dp.getNhanVienXuLy().getHoTen());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DatPhong> searchAdvanced(String keyword, String trangThai, String tuNgay, String denNgay) {
        Timestamp tuNgayTimestamp = parseStartOfDay(tuNgay);
        Timestamp denNgayTimestamp = parseEndOfDay(denNgay);
        return datPhongRepository.searchAdvanced(keyword, trangThai, tuNgayTimestamp, denNgayTimestamp);
    }

    @Transactional(readOnly = true)
    public DatPhong getDatPhongDetail(int maDp) {
        return datPhongRepository.findById(maDp).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ChiTietDatPhong> getChiTiet(int maDp) {
        return chiTietDatPhongRepository.findByMaDp(maDp);
    }

    @Transactional
    public boolean updateTrangThaiTransaction(
            int maDp,
            String newTrangThai,
            Integer maNvXuLy) {

        DatPhong dp = datPhongRepository.findById(maDp).orElse(null);

        if (dp == null) {
            return false;
        }

        List<ChiTietDatPhong> chiTiet =
                chiTietDatPhongRepository.findByMaDp(maDp);

        if (!isValidStatusTransition(dp, newTrangThai, chiTiet)) {
            return false;
        }

        // Chỉ cập nhật trạng thái đơn đặt phòng
        dp.setTrangThai(newTrangThai);
        dp.setMaNvXuLy(maNvXuLy);

        datPhongRepository.save(dp);

        // Gửi email thông báo
        publishStatusEmail(dp, newTrangThai);

        // Hoàn tiền nếu đơn bị hủy
        if ("DA_HUY".equals(newTrangThai)) {
            recordRefund(dp);
        }

        return true;
    }

    private void publishStatusEmail(DatPhong booking, String status) {
        if (eventPublisher == null
                || booking.getKhachHang() == null
                || (!"DA_XAC_NHAN".equals(status) && !"DA_HUY".equals(status))) {
            return;
        }
        eventPublisher.publishEvent(new BookingStatusEmailEvent(
                booking.getKhachHang().getEmail(),
                booking.getKhachHang().getHoTen(),
                booking.getMaTraCuu(),
                status,
                "DA_XAC_NHAN".equals(status) ? "Đã xác nhận" : "Đã hủy"));
    }

    private boolean isValidStatusTransition(
            DatPhong dp,
            String newTrangThai,
            List<ChiTietDatPhong> chiTiet) {

        if (newTrangThai == null || chiTiet.isEmpty()) {
            return false;
        }

        // Lấy danh sách mã phòng trong đơn
        List<Integer> maPhongList = chiTiet.stream()
                .map(ChiTietDatPhong::getMaPhong)
                .distinct()
                .collect(Collectors.toList());

        // Kiểm tra các phòng có tồn tại không
        List<Phong> phongs = phongRepository.findAllById(maPhongList);

        if (phongs.size() != maPhongList.size()) {
            return false;
        }

        // 1. Xác nhận đặt phòng
        if ("DA_XAC_NHAN".equals(newTrangThai)) {

            return "CHO_XAC_NHAN".equals(dp.getTrangThai())
                    && !hasRoomConflict(dp, maPhongList);
        }

        // 2. Nhận phòng
        if ("DA_NHAN_PHONG".equals(newTrangThai)) {

            return "DA_XAC_NHAN".equals(dp.getTrangThai())
                    && isCheckInDateValid(dp);
        }

        // 3. Trả phòng
        if ("DA_TRA_PHONG".equals(newTrangThai)) {

            return "DA_NHAN_PHONG".equals(dp.getTrangThai())
                    && isCheckOutDateValid(dp);
        }

        // 4. Hủy đặt phòng
        if ("DA_HUY".equals(newTrangThai)) {

            return "CHO_XAC_NHAN".equals(dp.getTrangThai())
                    || "DA_XAC_NHAN".equals(dp.getTrangThai());
        }

        return false;
    }

    private Timestamp parseStartOfDay(String date) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        }
        return Timestamp.valueOf(date.trim() + " 00:00:00");
    }

    private Timestamp parseEndOfDay(String date) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        }
        return Timestamp.valueOf(date.trim() + " 23:59:59");
    }

    private boolean isCheckInDateValid(DatPhong booking) {
        if (booking.getNgayNhanPhong() == null || booking.getNgayTraPhong() == null) {
            return true;
        }
        LocalDate today = LocalDate.now();
        LocalDate checkIn = booking.getNgayNhanPhong().toLocalDate();
        LocalDate checkOut = booking.getNgayTraPhong().toLocalDate();
        return !today.isBefore(checkIn) && today.isBefore(checkOut);
    }

    private boolean isCheckOutDateValid(DatPhong booking) {
        if (booking.getNgayNhanPhong() == null) {
            return true;
        }
        return !LocalDate.now().isBefore(booking.getNgayNhanPhong().toLocalDate());
    }

    private void recordRefund(DatPhong booking) {
        if (thanhToanRepository == null) {
            return;
        }
        List<ThanhToan> payments = thanhToanRepository.findByDatPhong_MaDp(booking.getMaDp());
        if (payments.stream().anyMatch(payment -> "HOAN_TIEN".equalsIgnoreCase(payment.getLoaiThanhToan()))) {
            return;
        }
        BigDecimal paidAmount = payments.stream()
                .filter(payment -> !"HOAN_TIEN".equalsIgnoreCase(payment.getLoaiThanhToan()))
                .map(ThanhToan::getSoTien)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        ThanhToan refund = new ThanhToan();
        refund.setDatPhong(booking);
        refund.setSoTien(paidAmount.setScale(2, RoundingMode.HALF_UP));
        refund.setNgayThanhToan(new Timestamp(System.currentTimeMillis()));
        refund.setPhuongThuc(payments.stream()
                .map(ThanhToan::getPhuongThuc)
                .filter(method -> method != null)
                .findFirst()
                .orElse("TIEN_MAT"));
        refund.setLoaiThanhToan("HOAN_TIEN");
        refund.setMaGiaoDich("REFUND_" + booking.getMaTraCuu());
        refund.setGhiChu("Hoàn tiền khi nhân viên hủy đơn đặt phòng.");
        thanhToanRepository.save(refund);
    }

    private boolean hasRoomConflict(
            DatPhong booking,
            List<Integer> maPhongList) {

        if (booking.getNgayNhanPhong() == null
                || booking.getNgayTraPhong() == null) {
            return true;
        }

        long conflictCount = chiTietDatPhongRepository.countRoomConflicts(
                maPhongList,
                booking.getMaDp(),
                booking.getNgayNhanPhong(),
                booking.getNgayTraPhong()
        );

        return conflictCount > 0;
    }
}