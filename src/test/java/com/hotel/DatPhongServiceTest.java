package com.hotel;

import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.DatPhong;
import com.hotel.model.KhachHang;
import com.hotel.repository.ChiTietDatPhongRepository;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.KhachHangRepository;
import com.hotel.service.DatDichVuService;
import com.hotel.service.DatPhongService;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DatPhongServiceTest {
        @Test
        void tuChoiKhiNgayTraPhongKhongHopLe() {
                DatPhongService service = newService();
                DatPhong booking = new DatPhong();
                booking.setNgayNhanPhong(Date.valueOf("2026-12-10"));
                booking.setNgayTraPhong(Date.valueOf("2026-12-10"));
                assertThrows(IllegalArgumentException.class,
                                () -> service.taoDonDatPhong(new KhachHang(), booking,
                                                List.of(new ChiTietDatPhong()), List.of()));
        }

        @Test
        void tuChoiKhiPhongDaCoDonTrongKhoangNgay() {
                DatPhongRepository datPhongRepository = mock(DatPhongRepository.class);
                ChiTietDatPhongRepository chiTietRepository = mock(ChiTietDatPhongRepository.class);
                KhachHangRepository khachHangRepository = mock(KhachHangRepository.class);
                DatDichVuService datDichVuService = mock(DatDichVuService.class);
                KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
                DatPhongService service = new DatPhongService(datPhongRepository, chiTietRepository,
                                khachHangRepository, kafkaTemplate, datDichVuService);
                KhachHang customer = new KhachHang();
                customer.setMaKh(1);
                customer.setHoTen("Nguyen Van A");
                customer.setEmail("a@example.com");
                customer.setSoDienThoai("0900000000");
                when(khachHangRepository.findByEmailIgnoreCase(customer.getEmail())).thenReturn(Optional.of(customer));
                DatPhong booking = new DatPhong();
                booking.setNgayNhanPhong(Date.valueOf("2026-12-10"));
                booking.setNgayTraPhong(Date.valueOf("2026-12-12"));
                booking.setTienPhong(java.math.BigDecimal.valueOf(1000000));
                ChiTietDatPhong detail = new ChiTietDatPhong();
                detail.setMaPhong(1);
                when(chiTietRepository.countOverlappingReservations(1,
                                booking.getNgayNhanPhong(), booking.getNgayTraPhong())).thenReturn(1L);
                assertThrows(IllegalStateException.class,
                                () -> service.taoDonDatPhong(customer, booking, List.of(detail), List.of()));
                verify(datPhongRepository, never()).save(any(DatPhong.class));
                verify(chiTietRepository, never()).save(any(ChiTietDatPhong.class));
        }

        private DatPhongService newService() {
                return new DatPhongService(mock(DatPhongRepository.class),
                                mock(ChiTietDatPhongRepository.class),
                                mock(KhachHangRepository.class),
                                mock(KafkaTemplate.class),
                                mock(DatDichVuService.class));
        }
}