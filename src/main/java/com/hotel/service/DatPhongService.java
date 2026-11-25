package com.hotel.service;

import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.CartServiceItem;
import com.hotel.model.DatPhong;
import com.hotel.model.KhachHang;
import com.hotel.repository.ChiTietDatPhongRepository;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.KhachHangRepository;
import com.hotel.util.MaTraCuuGenerator;
import com.hotel.event.BookingConfirmationEmailEvent;
import com.hotel.event.BookingStatusEmailEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.time.format.DateTimeFormatter;

@Service
public class DatPhongService {
    private static final Logger log = LoggerFactory.getLogger(DatPhongService.class);

    private final DatPhongRepository datPhongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final KhachHangRepository khachHangRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DatDichVuService datDichVuService;
    private final ApplicationEventPublisher eventPublisher;

    public DatPhongService(DatPhongRepository datPhongRepository,
            ChiTietDatPhongRepository chiTietDatPhongRepository,
            KhachHangRepository khachHangRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            DatDichVuService datDichVuService) {
        this(datPhongRepository, chiTietDatPhongRepository, khachHangRepository,
                kafkaTemplate, datDichVuService, null);
    }

    @Autowired
    public DatPhongService(DatPhongRepository datPhongRepository,
            ChiTietDatPhongRepository chiTietDatPhongRepository,
            KhachHangRepository khachHangRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            DatDichVuService datDichVuService,
            ApplicationEventPublisher eventPublisher) {
        this.datPhongRepository = datPhongRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
        this.khachHangRepository = khachHangRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.datDichVuService = datDichVuService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public String taoDonDatPhong(KhachHang khachHang, DatPhong datPhong,
            List<ChiTietDatPhong> danhSachChiTiet) {
        return taoDonDatPhong(khachHang, datPhong, danhSachChiTiet, java.util.Collections.emptyList());
    }

    @Transactional // Đảm bảo All-or-Nothing
    public String taoDonDatPhong(KhachHang khachHang, DatPhong datPhong,
            List<ChiTietDatPhong> danhSachChiTiet,
            List<CartServiceItem> danhSachDichVu) {
        if (datPhong.getNgayNhanPhong() == null || datPhong.getNgayTraPhong() == null
                || !datPhong.getNgayNhanPhong().before(datPhong.getNgayTraPhong())) {
            throw new IllegalArgumentException("Ngày nhận phòng phải trước ngày trả phòng.");
        }
        if (khachHang != null) {
            khachHang.setHoTen(khachHang.getHoTen() == null ? null : khachHang.getHoTen().trim());
            khachHang.setEmail(khachHang.getEmail() == null ? null : khachHang.getEmail().trim());
            khachHang.setSoDienThoai(khachHang.getSoDienThoai() == null
                    ? null
                    : khachHang.getSoDienThoai().trim());
        }
        if (danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
            throw new IllegalArgumentException("Đơn đặt phòng phải có ít nhất một phòng.");
        }
        if (khachHang == null || khachHang.getHoTen() == null
                || khachHang.getEmail() == null || khachHang.getSoDienThoai() == null
                || khachHang.getHoTen().trim().isEmpty() || khachHang.getEmail().trim().isEmpty()
                || khachHang.getSoDienThoai().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ họ tên, email và số điện thoại.");
        }
        for (ChiTietDatPhong ct : danhSachChiTiet) {
            long overlapping = chiTietDatPhongRepository.countOverlappingReservations(
                    ct.getMaPhong(), datPhong.getNgayNhanPhong(), datPhong.getNgayTraPhong());
            if (overlapping > 0) {
                throw new IllegalStateException("Phòng bạn chọn vừa bị người khác đặt mất. Vui lòng chọn phòng khác.");
            }
        }
        // 1. Lưu khách hàng
        int maKh = -1;
        Optional<KhachHang> existingKh = khachHang.getMaTk() == null
                ? Optional.empty()
                : khachHangRepository.findByMaTk(khachHang.getMaTk());
        if (existingKh.isEmpty()) {
            existingKh = khachHangRepository.findByEmailIgnoreCase(khachHang.getEmail());
        }
        if (existingKh.isPresent()) {
            KhachHang existing = existingKh.get();
            if (khachHang.getMaTk() != null && existing.getMaTk() != null
                    && !existing.getMaTk().equals(khachHang.getMaTk())) {
                throw new IllegalArgumentException("Email đã liên kết với một tài khoản khách hàng khác.");
            }
            if (khachHang.getMaTk() != null && existing.getMaTk() == null) {
                existing.setMaTk(khachHang.getMaTk());
                existing = khachHangRepository.save(existing);
            }
            maKh = existing.getMaKh();
        } else {
            khachHang.setNgayTao(new Timestamp(System.currentTimeMillis()));
            KhachHang savedKh = khachHangRepository.save(khachHang);
            maKh = savedKh.getMaKh();
        }

        // 2. Lưu Đơn Đặt Phòng
        String maTraCuu;
        do {
            maTraCuu = MaTraCuuGenerator.generate(8);
        } while (datPhongRepository.findByMaTraCuu(maTraCuu).isPresent());
        datPhong.setMaTraCuu(maTraCuu);
        datPhong.setMaKh(maKh);
        datPhong.setTrangThai("CHO_XAC_NHAN");
        datPhong.setNgayDat(new Timestamp(System.currentTimeMillis()));
        datPhong.setTienDichVu(java.math.BigDecimal.ZERO);
        datPhong.setTongTien(datPhong.getTienPhong());
        DatPhong savedDp = datPhongRepository.save(datPhong);

        // 3. Lưu Chi Tiết
        for (ChiTietDatPhong ct : danhSachChiTiet) {
            ct.setMaDp(savedDp.getMaDp());
            chiTietDatPhongRepository.save(ct);
        }

        datDichVuService.addCartServices(savedDp.getMaDp(), danhSachDichVu,
                savedDp.getNgayNhanPhong());

        // 4. KAFKA INTEGRATION: Bắn event thông báo tạo đơn thành công (Huyy sẽ dùng để
        // gửi mail)
        String message = String.format("{\"maTraCuu\": \"%s\", \"email\": \"%s\", \"action\": \"CREATED\"}", maTraCuu,
                khachHang.getEmail());
        publishEventAsync(message);
        publishBookingConfirmationEmail(datPhong, khachHang);

        return maTraCuu;
    }

    @Transactional(readOnly = true)
    public DatPhong traCuuDon(String maTraCuu, String email) {
        if (maTraCuu == null || email == null || maTraCuu.trim().isEmpty() || email.trim().isEmpty()) {
            return null;
        }
        return datPhongRepository.findByMaTraCuu(maTraCuu)
                .filter(dp -> dp.getKhachHang().getEmail().equalsIgnoreCase(email))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public DatPhong timTheoMaTraCuu(String maTraCuu) {
        return maTraCuu == null ? null : datPhongRepository.findByMaTraCuu(maTraCuu.trim()).orElse(null);
    }

    @Transactional
    public boolean huyDonPhong(int maDp, String maTraCuu, String email) {
        return datPhongRepository.findById(maDp)
                .filter(dp -> dp.getMaTraCuu().equals(maTraCuu)
                        && dp.getKhachHang() != null
                        && dp.getKhachHang().getEmail().equalsIgnoreCase(email)
                        && "CHO_XAC_NHAN".equals(dp.getTrangThai()))
                .map(dp -> {
                    dp.setTrangThai("DA_HUY");
                    datPhongRepository.save(dp);
                    String message = String.format(
                            "{\"maTraCuu\": \"%s\", \"email\": \"%s\", \"action\": \"CANCELED\"}",
                            dp.getMaTraCuu(), dp.getKhachHang().getEmail());
                    publishEventAsync(message);
                    publishBookingStatusEmail(dp, "DA_HUY");
                    return true;
                }).orElse(false);
    }

    @Transactional
    public boolean huyDonPhongCuaKhach(int maDp, int maTk) {
        Optional<KhachHang> customer = khachHangRepository.findByMaTk(maTk);
        if (customer.isEmpty()) {
            return false;
        }
        Optional<DatPhong> booking = datPhongRepository.findById(maDp);
        if (booking.isEmpty()
                || booking.get().getKhachHang() == null
                || !Objects.equals(booking.get().getKhachHang().getMaKh(), customer.get().getMaKh())
                || !"CHO_XAC_NHAN".equals(booking.get().getTrangThai())) {
            return false;
        }
        DatPhong dp = booking.get();
        dp.setTrangThai("DA_HUY");
        datPhongRepository.save(dp);
        String message = String.format("{\"maTraCuu\": \"%s\", \"email\": \"%s\", \"action\": \"CANCELED\"}",
                dp.getMaTraCuu(), dp.getKhachHang().getEmail());
        publishEventAsync(message);
        publishBookingStatusEmail(dp, "DA_HUY");
        return true;
    }

    @Transactional
    public void huyDonPhong(int maDp) {
        datPhongRepository.findById(maDp).ifPresent(dp -> {
            if ("CHO_XAC_NHAN".equals(dp.getTrangThai())) {
                dp.setTrangThai("DA_HUY");
                datPhongRepository.save(dp);

                // KAFKA INTEGRATION: Bắn event hủy đơn
                String message = String.format("{\"maTraCuu\": \"%s\", \"email\": \"%s\", \"action\": \"CANCELED\"}",
                        dp.getMaTraCuu(), dp.getKhachHang().getEmail());
                publishEventAsync(message);
                publishBookingStatusEmail(dp, "DA_HUY");
            }
        });
    }

    private void publishBookingConfirmationEmail(DatPhong booking, KhachHang customer) {
        if (eventPublisher == null || customer == null) {
            return;
        }
        eventPublisher.publishEvent(new BookingConfirmationEmailEvent(
                customer.getEmail(),
                customer.getHoTen(),
                booking.getMaTraCuu(),
                formatDate(booking.getNgayNhanPhong()),
                formatDate(booking.getNgayTraPhong()),
                formatAmount(booking.getTongTien())));
    }

    private void publishBookingStatusEmail(DatPhong booking, String status) {
        if (eventPublisher == null || booking == null || booking.getKhachHang() == null) {
            return;
        }
        KhachHang customer = booking.getKhachHang();
        eventPublisher.publishEvent(new BookingStatusEmailEvent(
                customer.getEmail(),
                customer.getHoTen(),
                booking.getMaTraCuu(),
                status,
                statusLabel(status)));
    }

    private String formatDate(java.sql.Date date) {
        return date == null ? "" : date.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String formatAmount(java.math.BigDecimal amount) {
        return amount == null ? "0 VNĐ" : amount.toPlainString() + " VNĐ";
    }

    private String statusLabel(String status) {
        if ("DA_HUY".equals(status)) {
            return "Đã hủy";
        }
        if ("DA_XAC_NHAN".equals(status)) {
            return "Đã xác nhận";
        }
        return status;
    }

    private void publishEventAsync(String message) {
        CompletableFuture.runAsync(() -> {
            try {
                kafkaTemplate.send("booking_events", message);
            } catch (RuntimeException ex) {
                log.warn("Không thể gửi sự kiện booking_events; không làm ảnh hưởng nghiệp vụ đặt phòng.", ex);
            }
        });
    }
}