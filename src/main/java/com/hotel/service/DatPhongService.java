package com.hotel.service;

import com.google.gson.JsonObject;
import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.CartServiceItem;
import com.hotel.model.DatPhong;
import com.hotel.model.KhachHang;
import com.hotel.repository.ChiTietDatPhongRepository;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.KhachHangRepository;
import com.hotel.repository.PhongRepository;
import com.hotel.repository.ThanhToanRepository;
import com.hotel.enums.TrangThaiPhong;
import com.hotel.model.Phong;
import com.hotel.model.ThanhToan;
import com.hotel.util.MaTraCuuGenerator;
import com.hotel.event.BookingConfirmationEmailEvent;
import com.hotel.event.BookingStatusEmailEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DatPhongService {
    private static final Logger log = LoggerFactory.getLogger(DatPhongService.class);

    private final DatPhongRepository datPhongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final KhachHangRepository khachHangRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DatDichVuService datDichVuService;
    private final ApplicationEventPublisher eventPublisher;
    private final PhongRepository phongRepository;
    private final ThanhToanRepository thanhToanRepository;

    public DatPhongService(DatPhongRepository datPhongRepository,
            ChiTietDatPhongRepository chiTietDatPhongRepository,
            KhachHangRepository khachHangRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            DatDichVuService datDichVuService) {
        this(datPhongRepository, chiTietDatPhongRepository, khachHangRepository,
                kafkaTemplate, datDichVuService, null);
    }

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
        this.phongRepository = null;
        this.thanhToanRepository = null;
    }

    @Autowired
    public DatPhongService(DatPhongRepository datPhongRepository,
            ChiTietDatPhongRepository chiTietDatPhongRepository,
            KhachHangRepository khachHangRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            DatDichVuService datDichVuService,
            ApplicationEventPublisher eventPublisher,
            PhongRepository phongRepository, ThanhToanRepository thanhToanRepository) {
        this.datPhongRepository = datPhongRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
        this.khachHangRepository = khachHangRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.datDichVuService = datDichVuService;
        this.eventPublisher = eventPublisher;
        this.phongRepository = phongRepository;
        this.thanhToanRepository = thanhToanRepository;
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

        publishBookingEvent(savedDp, "CREATED", khachHang.getEmail());

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
                    releaseBookedRooms(dp);
                    recordRefund(dp);
                    publishBookingEvent(dp, "CANCELED");
                    return true;
                }).orElse(false);
    }

    @Transactional
    public boolean huyDonPhongCuaKhach(int maDp, int maTk) {
        Optional<KhachHang> customer = khachHangRepository.findByMaTk(maTk);
        if (customer.isEmpty()) {
            return false;
        }
        Optional<DatPhong> booking = datPhongRepository.findByIdForUpdate(maDp);
        if (booking.isEmpty()
                || booking.get().getKhachHang() == null
                || !Objects.equals(booking.get().getKhachHang().getMaKh(), customer.get().getMaKh())
                || !"CHO_XAC_NHAN".equals(booking.get().getTrangThai())) {
            return false;
        }
        DatPhong dp = booking.get();
        dp.setTrangThai("DA_HUY");
        datPhongRepository.save(dp);
        releaseBookedRooms(dp);
        recordRefund(dp);
        publishBookingEvent(dp, "CANCELED");
        return true;
    }

    @Transactional
    public void huyDonPhong(int maDp) {
        datPhongRepository.findById(maDp).ifPresent(dp -> {
            if ("CHO_XAC_NHAN".equals(dp.getTrangThai())) {
                dp.setTrangThai("DA_HUY");
                datPhongRepository.save(dp);
                releaseBookedRooms(dp);
                recordRefund(dp);

                publishBookingEvent(dp, "CANCELED");
            }
        });
    }

    private void releaseBookedRooms(DatPhong booking) {
        if (phongRepository == null) {
            return;
        }
        List<Integer> roomIds = chiTietDatPhongRepository.findByMaDp(booking.getMaDp()).stream()
                .map(ChiTietDatPhong::getMaPhong)
                .toList();
        if (roomIds.isEmpty()) {
            return;
        }
        List<Phong> rooms = phongRepository.findAllById(roomIds);
        boolean changed = false;
        for (Phong room : rooms) {
            if (room.getTrangThaiPhong() == TrangThaiPhong.DA_DAT) {
                room.setTrangThaiPhong(TrangThaiPhong.TRONG);
                changed = true;
            }
        }
        if (changed) {
            phongRepository.saveAll(rooms);
        }
    }

    private void recordRefund(DatPhong booking) {
        if (thanhToanRepository == null) {
            return;
        }
        List<ThanhToan> payments = thanhToanRepository.findByDatPhong_MaDp(booking.getMaDp());
        boolean alreadyRefunded = payments.stream()
                .anyMatch(payment -> "HOAN_TIEN".equalsIgnoreCase(payment.getLoaiThanhToan()));
        if (alreadyRefunded) {
            return;
        }
        BigDecimal paidAmount = payments.stream()
                .filter(payment -> !"HOAN_TIEN".equalsIgnoreCase(payment.getLoaiThanhToan()))
                .map(ThanhToan::getSoTien)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        ThanhToan refund = new ThanhToan();
        refund.setDatPhong(booking);
        refund.setSoTien(paidAmount.setScale(2, RoundingMode.HALF_UP));
        refund.setNgayThanhToan(new Timestamp(System.currentTimeMillis()));
        refund.setPhuongThuc(payments.stream()
                .filter(payment -> payment.getPhuongThuc() != null)
                .map(ThanhToan::getPhuongThuc)
                .findFirst()
                .orElse("TIEN_MAT"));
        refund.setLoaiThanhToan("HOAN_TIEN");
        refund.setMaGiaoDich("REFUND_" + booking.getMaTraCuu());
        refund.setGhiChu("Hoàn tiền khi khách hủy đơn đặt phòng.");
        thanhToanRepository.save(refund);
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

    private String formatDate(java.sql.Date date) {
        return date == null ? "" : date.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String formatAmount(java.math.BigDecimal amount) {
        return amount == null ? "0 VNĐ" : amount.toPlainString() + " VNĐ";
    }

    private void publishBookingEvent(DatPhong booking, String action) {
        publishBookingEvent(booking, action,
                booking == null || booking.getKhachHang() == null ? null : booking.getKhachHang().getEmail());
    }

    private void publishBookingEvent(DatPhong booking, String action, String email) {
        if (booking == null || email == null) {
            return;
        }
        JsonObject event = new JsonObject();
        event.addProperty("maTraCuu", booking.getMaTraCuu());
        event.addProperty("email", email);
        event.addProperty("action", action);
        String message = event.toString();
        Runnable publish = () -> {
            try {
                kafkaTemplate.send("booking_events", message)
                        .addCallback(result -> {
                        }, ex -> log.error("Không thể gửi sự kiện booking_events.", ex));
            } catch (RuntimeException ex) {
                log.error("Không thể gửi sự kiện booking_events.", ex);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish.run();
                }
            });
        } else {
            publish.run();
        }
    }
}