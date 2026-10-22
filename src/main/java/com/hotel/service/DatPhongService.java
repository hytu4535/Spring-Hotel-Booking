package com.hotel.service;

import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.DatPhong;
import com.hotel.model.KhachHang;
import com.hotel.repository.ChiTietDatPhongRepository;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.KhachHangRepository;
import com.hotel.util.MaTraCuuGenerator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class DatPhongService {

    private final DatPhongRepository datPhongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final KhachHangRepository khachHangRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public DatPhongService(DatPhongRepository datPhongRepository,
            ChiTietDatPhongRepository chiTietDatPhongRepository,
            KhachHangRepository khachHangRepository,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.datPhongRepository = datPhongRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
        this.khachHangRepository = khachHangRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional // Đảm bảo All-or-Nothing
    public String taoDonDatPhong(KhachHang khachHang, DatPhong datPhong, List<ChiTietDatPhong> danhSachChiTiet) {
        // 1. Lưu khách hàng
        int maKh = -1;
        Optional<KhachHang> existingKh = khachHangRepository.findByEmailIgnoreCase(khachHang.getEmail());
        if (existingKh.isPresent()) {
            maKh = existingKh.get().getMaKh();
        } else {
            khachHang.setNgayTao(new Timestamp(System.currentTimeMillis()));
            KhachHang savedKh = khachHangRepository.save(khachHang);
            maKh = savedKh.getMaKh();
        }

        // 2. Lưu Đơn Đặt Phòng
        String maTraCuu = MaTraCuuGenerator.generate(8);
        datPhong.setMaTraCuu(maTraCuu);
        datPhong.setMaKh(maKh);
        datPhong.setTrangThai("CHO_XAC_NHAN");
        datPhong.setNgayDat(new Timestamp(System.currentTimeMillis()));
        datPhong.setTienDichVu(java.math.BigDecimal.ZERO);
        datPhong.setTongTien(datPhong.getTienPhong());
        DatPhong savedDp = datPhongRepository.save(datPhong);

        // 3. Lưu Chi Tiết & Check trùng lịch
        for (ChiTietDatPhong ct : danhSachChiTiet) {
            long overlapping = chiTietDatPhongRepository.countOverlappingReservations(
                    ct.getMaPhong(), datPhong.getNgayNhanPhong().toLocalDate(),
                    datPhong.getNgayTraPhong().toLocalDate());
            if (overlapping > 0) {
                throw new IllegalStateException("Phòng bạn chọn vừa bị người khác đặt mất. Vui lòng chọn phòng khác.");
            }
            ct.setMaDp(savedDp.getMaDp());
            chiTietDatPhongRepository.save(ct);
        }

        // 4. KAFKA INTEGRATION: Bắn event thông báo tạo đơn thành công (Huyy sẽ dùng để
        // gửi mail)
        String message = String.format("{\"maTraCuu\": \"%s\", \"email\": \"%s\", \"action\": \"CREATED\"}", maTraCuu,
                khachHang.getEmail());
        kafkaTemplate.send("booking_events", message);

        return maTraCuu;
    }

    @Transactional(readOnly = true)
    public DatPhong traCuuDon(String maTraCuu, String email) {
        return datPhongRepository.findByMaTraCuu(maTraCuu)
                .filter(dp -> dp.getKhachHang().getEmail().equalsIgnoreCase(email))
                .orElse(null);
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
                kafkaTemplate.send("booking_events", message);
            }
        });
    }
}