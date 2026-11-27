
package com.hotel.service;

import com.hotel.model.CartServiceItem;
import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.DatPhong;
import com.hotel.model.KhachHang;
import com.hotel.model.ThanhToan;

import com.hotel.repository.ChiTietDatPhongRepository;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.KhachHangRepository;
import com.hotel.repository.ThanhToanRepository;

import com.hotel.util.MaTraCuuGenerator;
import com.hotel.event.BookingConfirmationEmailEvent;
import com.hotel.event.BookingStatusEmailEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class DatPhongService {

    private static final Logger log =
            LoggerFactory.getLogger(DatPhongService.class);

    private final DatPhongRepository datPhongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final KhachHangRepository khachHangRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DatDichVuService datDichVuService;
    private final ApplicationEventPublisher eventPublisher;
    private final ThanhToanRepository thanhToanRepository;

    // Constructor injection
    public DatPhongService(
            DatPhongRepository datPhongRepository,
            ChiTietDatPhongRepository chiTietDatPhongRepository,
            KhachHangRepository khachHangRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            DatDichVuService datDichVuService,
            ApplicationEventPublisher eventPublisher,
            ThanhToanRepository thanhToanRepository) {

        this.datPhongRepository = datPhongRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
        this.khachHangRepository = khachHangRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.datDichVuService = datDichVuService;
        this.eventPublisher = eventPublisher;
        this.thanhToanRepository = thanhToanRepository;
    }

    // =========================================================
    // 1. TẠO ĐƠN ĐẶT PHÒNG
    // =========================================================

    @Transactional
    public String taoDonDatPhong(
            KhachHang khachHang,
            DatPhong datPhong,
            List<ChiTietDatPhong> danhSachChiTiet) {

        return taoDonDatPhong(
                khachHang,
                datPhong,
                danhSachChiTiet,
                java.util.Collections.emptyList()
        );
    }

    @Transactional
    public String taoDonDatPhong(
            KhachHang khachHang,
            DatPhong datPhong,
            List<ChiTietDatPhong> danhSachChiTiet,
            List<CartServiceItem> danhSachDichVu) {

        // Kiểm tra ngày nhận và ngày trả
        if (datPhong.getNgayNhanPhong() == null
                || datPhong.getNgayTraPhong() == null
                || !datPhong.getNgayNhanPhong()
                .before(datPhong.getNgayTraPhong())) {

            throw new IllegalArgumentException(
                    "Ngày nhận phòng phải trước ngày trả phòng."
            );
        }

        // Chuẩn hóa thông tin khách hàng
        if (khachHang != null) {
            khachHang.setHoTen(
                    khachHang.getHoTen() == null
                            ? null
                            : khachHang.getHoTen().trim()
            );

            khachHang.setEmail(
                    khachHang.getEmail() == null
                            ? null
                            : khachHang.getEmail().trim()
            );

            khachHang.setSoDienThoai(
                    khachHang.getSoDienThoai() == null
                            ? null
                            : khachHang.getSoDienThoai().trim()
            );
        }

        // Kiểm tra danh sách phòng
        if (danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
            throw new IllegalArgumentException(
                    "Đơn đặt phòng phải có ít nhất một phòng."
            );
        }

        // Kiểm tra thông tin khách hàng
        if (khachHang == null
                || khachHang.getHoTen() == null
                || khachHang.getEmail() == null
                || khachHang.getSoDienThoai() == null
                || khachHang.getHoTen().isEmpty()
                || khachHang.getEmail().isEmpty()
                || khachHang.getSoDienThoai().isEmpty()) {

            throw new IllegalArgumentException(
                    "Vui lòng nhập đầy đủ họ tên, email và số điện thoại."
            );
        }

        // Kiểm tra mã phòng hợp lệ và không bị trùng trong cùng đơn
        List<Integer> maPhongList = danhSachChiTiet.stream()
                .map(ChiTietDatPhong::getMaPhong)
                .toList();

        if (maPhongList.stream().anyMatch(Objects::isNull)
                || maPhongList.stream().distinct().count() != maPhongList.size()) {

            throw new IllegalArgumentException(
                    "Danh sách phòng không hợp lệ hoặc có phòng bị chọn trùng."
            );
        }

        // Kiểm tra phòng có bị đặt trùng thời gian không
        for (ChiTietDatPhong ct : danhSachChiTiet) {

            long overlapping =
                    chiTietDatPhongRepository.countOverlappingReservations(
                            ct.getMaPhong(),
                            datPhong.getNgayNhanPhong(),
                            datPhong.getNgayTraPhong()
                    );

            if (overlapping > 0) {
                throw new IllegalStateException(
                        "Phòng bạn chọn vừa bị người khác đặt mất. Vui lòng chọn phòng khác."
                );
            }
        }

        // -----------------------------------------------------
        // Bước 1: Lưu khách hàng
        // -----------------------------------------------------

        int maKh;

        Optional<KhachHang> existingKh =
                khachHang.getMaTk() == null
                        ? Optional.empty()
                        : khachHangRepository.findByMaTk(khachHang.getMaTk());

        if (existingKh.isEmpty()) {
            existingKh = khachHangRepository.findByEmailIgnoreCase(
                    khachHang.getEmail()
            );
        }

        if (existingKh.isPresent()) {

            KhachHang existing = existingKh.get();

            if (khachHang.getMaTk() != null
                    && existing.getMaTk() != null
                    && !existing.getMaTk().equals(khachHang.getMaTk())) {

                throw new IllegalArgumentException(
                        "Email đã liên kết với một tài khoản khách hàng khác."
                );
            }

            if (khachHang.getMaTk() != null
                    && existing.getMaTk() == null) {

                existing.setMaTk(khachHang.getMaTk());
                existing = khachHangRepository.save(existing);
            }

            maKh = existing.getMaKh();

        } else {

            khachHang.setNgayTao(
                    new Timestamp(System.currentTimeMillis())
            );

            KhachHang savedKh = khachHangRepository.save(khachHang);
            maKh = savedKh.getMaKh();
        }

        // -----------------------------------------------------
        // Bước 2: Lưu đơn đặt phòng
        // -----------------------------------------------------

        String maTraCuu;

        do {
            maTraCuu = MaTraCuuGenerator.generate(8);
        } while (datPhongRepository.findByMaTraCuu(maTraCuu).isPresent());

        datPhong.setMaTraCuu(maTraCuu);
        datPhong.setMaKh(maKh);
        datPhong.setTrangThai("CHO_XAC_NHAN");
        datPhong.setNgayDat(
                new Timestamp(System.currentTimeMillis())
        );

        datPhong.setTienDichVu(BigDecimal.ZERO);

        BigDecimal tienPhong = datPhong.getTienPhong() == null
                ? BigDecimal.ZERO
                : datPhong.getTienPhong();

        datPhong.setTongTien(tienPhong);

        DatPhong savedDp = datPhongRepository.save(datPhong);

        // -----------------------------------------------------
        // Bước 3: Lưu chi tiết đặt phòng
        // -----------------------------------------------------

        for (ChiTietDatPhong ct : danhSachChiTiet) {

            ct.setMaDp(savedDp.getMaDp());

            chiTietDatPhongRepository.save(ct);
        }

        // -----------------------------------------------------
        // Bước 4: Lưu dịch vụ đi kèm
        // -----------------------------------------------------

        datDichVuService.addCartServices(
                savedDp.getMaDp(),
                danhSachDichVu,
                savedDp.getNgayNhanPhong()
        );

        // -----------------------------------------------------
        // Bước 5: Gửi sự kiện Kafka
        // -----------------------------------------------------

        String message = String.format(
                "{\"maTraCuu\": \"%s\", \"email\": \"%s\", \"action\": \"CREATED\"}",
                maTraCuu,
                khachHang.getEmail()
        );

        publishEventAsync(message);

        return maTraCuu;
    }

    // =========================================================
    // 2. TRA CỨU ĐƠN ĐẶT PHÒNG
    // =========================================================

    @Transactional(readOnly = true)
    public DatPhong traCuuDon(String maTraCuu, String email) {

        if (maTraCuu == null
                || email == null
                || maTraCuu.trim().isEmpty()
                || email.trim().isEmpty()) {

            return null;
        }

        return datPhongRepository.findByMaTraCuu(maTraCuu.trim())
                .filter(dp -> dp.getKhachHang() != null
                        && dp.getKhachHang().getEmail()
                        .equalsIgnoreCase(email.trim()))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public DatPhong timTheoMaTraCuu(String maTraCuu) {

        return maTraCuu == null
                ? null
                : datPhongRepository.findByMaTraCuu(
                maTraCuu.trim()
        ).orElse(null);
    }

    // =========================================================
    // 3. KHÁCH HỦY ĐƠN BẰNG MÃ TRA CỨU VÀ EMAIL
    // =========================================================

    @Transactional
    public boolean huyDonPhong(
            int maDp,
            String maTraCuu,
            String email) {

        Optional<DatPhong> booking =
                datPhongRepository.findById(maDp);

        if (booking.isEmpty()) {
            return false;
        }

        DatPhong dp = booking.get();

        if (maTraCuu == null
                || email == null
                || !Objects.equals(dp.getMaTraCuu(), maTraCuu)
                || dp.getKhachHang() == null
                || !dp.getKhachHang().getEmail()
                .equalsIgnoreCase(email.trim())
                || !"CHO_XAC_NHAN".equals(dp.getTrangThai())) {

            return false;
        }

        xuLyHuyDon(dp);

        return true;
    }

    // =========================================================
    // 4. KHÁCH HỦY ĐƠN KHI ĐÃ ĐĂNG NHẬP
    // =========================================================

    @Transactional
    public boolean huyDonPhongCuaKhach(int maDp, int maTk) {

        Optional<KhachHang> customer =
                khachHangRepository.findByMaTk(maTk);

        if (customer.isEmpty()) {
            return false;
        }

        Optional<DatPhong> booking =
                datPhongRepository.findById(maDp);

        if (booking.isEmpty()) {
            return false;
        }

        DatPhong dp = booking.get();

        if (dp.getKhachHang() == null
                || !Objects.equals(
                dp.getKhachHang().getMaKh(),
                customer.get().getMaKh()
        )
                || !"CHO_XAC_NHAN".equals(dp.getTrangThai())) {

            return false;
        }

        xuLyHuyDon(dp);

        return true;
    }

    // =========================================================
    // 5. HỦY ĐƠN THEO MÃ ĐẶT PHÒNG
    // =========================================================

    @Transactional
    public void huyDonPhong(int maDp) {

        datPhongRepository.findById(maDp).ifPresent(dp -> {

            if ("CHO_XAC_NHAN".equals(dp.getTrangThai())) {
                xuLyHuyDon(dp);
            }
        });
    }

    // =========================================================
    // 6. XỬ LÝ HỦY ĐƠN DÙNG CHUNG
    // =========================================================

    private void xuLyHuyDon(DatPhong dp) {

        // Chuyển trạng thái đơn thành đã hủy
        dp.setTrangThai("DA_HUY");

        datPhongRepository.save(dp);

        // Ghi nhận hoàn tiền nếu khách đã thanh toán
        recordRefund(dp);

        // Gửi sự kiện Kafka
        KhachHang customer = dp.getKhachHang();

        if (customer != null && customer.getEmail() != null) {

            String message = String.format(
                    "{\"maTraCuu\": \"%s\", \"email\": \"%s\", \"action\": \"CANCELED\"}",
                    dp.getMaTraCuu(),
                    customer.getEmail()
            );

            publishEventAsync(message);
        }

        // Gửi email thông báo hủy đơn
        publishBookingStatusEmail(dp, "DA_HUY");
    }

    // =========================================================
    // 7. GHI NHẬN HOÀN TIỀN
    // =========================================================

    private void recordRefund(DatPhong booking) {

        List<ThanhToan> payments =
                thanhToanRepository.findByDatPhong_MaDp(
                        booking.getMaDp()
                );

        // Kiểm tra đơn đã được hoàn tiền chưa
        boolean alreadyRefunded = payments.stream()
                .anyMatch(payment ->
                        "HOAN_TIEN".equalsIgnoreCase(
                                payment.getLoaiThanhToan()
                        )
                );

        if (alreadyRefunded) {
            return;
        }

        // Tính tổng số tiền khách đã thanh toán
        BigDecimal paidAmount = payments.stream()
                .filter(payment ->
                        !"HOAN_TIEN".equalsIgnoreCase(
                                payment.getLoaiThanhToan()
                        )
                )
                .map(ThanhToan::getSoTien)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        // Tạo giao dịch hoàn tiền
        ThanhToan refund = new ThanhToan();

        refund.setDatPhong(booking);

        refund.setSoTien(
                paidAmount.setScale(2, RoundingMode.HALF_UP)
        );

        refund.setNgayThanhToan(
                new Timestamp(System.currentTimeMillis())
        );

        refund.setPhuongThuc(
                payments.stream()
                        .filter(payment ->
                                payment.getPhuongThuc() != null
                        )
                        .map(ThanhToan::getPhuongThuc)
                        .findFirst()
                        .orElse("TIEN_MAT")
        );

        refund.setLoaiThanhToan("HOAN_TIEN");

        refund.setMaGiaoDich(
                "REFUND_" + booking.getMaTraCuu()
        );

        refund.setGhiChu(
                "Hoàn tiền khi khách hủy đơn đặt phòng."
        );

        thanhToanRepository.save(refund);
    }

    // =========================================================
    // 8. GỬI EMAIL XÁC NHẬN ĐẶT PHÒNG
    // =========================================================

    private void publishBookingConfirmationEmail(
            DatPhong booking,
            KhachHang customer) {

        if (eventPublisher == null || customer == null) {
            return;
        }

        eventPublisher.publishEvent(
                new BookingConfirmationEmailEvent(
                        customer.getEmail(),
                        customer.getHoTen(),
                        booking.getMaTraCuu(),
                        formatDate(booking.getNgayNhanPhong()),
                        formatDate(booking.getNgayTraPhong()),
                        formatAmount(booking.getTongTien())
                )
        );
    }

    // =========================================================
    // 9. GỬI EMAIL THÔNG BÁO TRẠNG THÁI ĐƠN
    // =========================================================

    private void publishBookingStatusEmail(
            DatPhong booking,
            String status) {

        if (eventPublisher == null
                || booking == null
                || booking.getKhachHang() == null) {

            return;
        }

        KhachHang customer = booking.getKhachHang();

        eventPublisher.publishEvent(
                new BookingStatusEmailEvent(
                        customer.getEmail(),
                        customer.getHoTen(),
                        booking.getMaTraCuu(),
                        status,
                        statusLabel(status)
                )
        );
    }

    // =========================================================
    // 10. ĐỊNH DẠNG NGÀY VÀ TIỀN
    // =========================================================

    private String formatDate(java.sql.Date date) {

        return date == null
                ? ""
                : date.toLocalDate().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
    }

    private String formatAmount(BigDecimal amount) {

        return amount == null
                ? "0 VNĐ"
                : amount.toPlainString() + " VNĐ";
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

    // =========================================================
    // 11. GỬI SỰ KIỆN KAFKA BẤT ĐỒNG BỘ
    // =========================================================

    private void publishEventAsync(String message) {

        CompletableFuture.runAsync(() -> {

            try {

                kafkaTemplate.send("booking_events", message);

            } catch (RuntimeException ex) {

                log.warn(
                        "Không thể gửi sự kiện booking_events; "
                                + "không làm ảnh hưởng nghiệp vụ đặt phòng.",
                        ex
                );
            }
        });
    }
}
