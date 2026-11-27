package com.hotel.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hotel.model.DatPhong;
import com.hotel.repository.DatPhongRepository;

import java.util.Optional;

@Component
public class BookingEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(BookingEventConsumer.class);
    private final DatPhongRepository datPhongRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BookingEventConsumer(DatPhongRepository datPhongRepository,
            ApplicationEventPublisher eventPublisher) {
        this.datPhongRepository = datPhongRepository;
        this.eventPublisher = eventPublisher;
    }

    @KafkaListener(topics = "booking_events", groupId = "hotel-booking-events")
    public void consume(String message) {
        try {
            JsonObject event = JsonParser.parseString(message).getAsJsonObject();
            String maTraCuu = event.has("maTraCuu") ? event.get("maTraCuu").getAsString() : null;
            String action = event.has("action") ? event.get("action").getAsString() : null;
            if (maTraCuu == null || action == null) {
                log.warn("Bo qua su kien booking khong hop le: {}", message);
                return;
            }
            Optional<DatPhong> booking = datPhongRepository.findByMaTraCuu(maTraCuu);
            if (booking.isEmpty() || booking.get().getKhachHang() == null) {
                log.warn("Bo qua su kien booking khong tim thay khach hang: {}", maTraCuu);
                return;
            }
            DatPhong dp = booking.get();
            if ("CANCELED".equals(action)) {
                eventPublisher.publishEvent(new BookingStatusEmailEvent(
                        dp.getKhachHang().getEmail(),
                        dp.getKhachHang().getHoTen(),
                        maTraCuu,
                        "DA_HUY",
                        "Đã hủy"));
            } else if ("CREATED".equals(action)) {
                eventPublisher.publishEvent(new BookingConfirmationEmailEvent(
                        dp.getKhachHang().getEmail(),
                        dp.getKhachHang().getHoTen(),
                        maTraCuu,
                        dp.getNgayNhanPhong() == null ? "" : dp.getNgayNhanPhong().toString(),
                        dp.getNgayTraPhong() == null ? "" : dp.getNgayTraPhong().toString(),
                        dp.getTongTien() == null ? "0 VNĐ" : dp.getTongTien().toPlainString() + " VNĐ"));
            }
            log.info("DA XU LY SU KIEN {} CHO DON {}", action, maTraCuu);
        } catch (RuntimeException ex) {
            log.error("Khong the xu ly su kien booking: {}", message, ex);
        }
    }
}