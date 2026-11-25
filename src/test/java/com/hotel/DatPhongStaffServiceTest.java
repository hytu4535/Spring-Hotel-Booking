
package com.hotel;

import com.hotel.enums.TrangThaiPhong;
import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.DatPhong;
import com.hotel.model.Phong;
import com.hotel.repository.ChiTietDatPhongRepository;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.PhongRepository;
import com.hotel.service.DatPhongStaffService;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DatPhongStaffServiceTest {

        @Test
        void khongChoChuyenTrangThaiNguoc() {
                DatPhongRepository bookingRepository = mock(DatPhongRepository.class);
                PhongRepository phongRepository = mock(PhongRepository.class);
                ChiTietDatPhongRepository detailRepository = mock(ChiTietDatPhongRepository.class);

                DatPhong booking = new DatPhong();
                booking.setMaDp(1);
                booking.setTrangThai("DA_TRA_PHONG");

                when(bookingRepository.findById(1))
                                .thenReturn(Optional.of(booking));

                when(detailRepository.findByMaDp(1))
                                .thenReturn(List.of());

                DatPhongStaffService service = new DatPhongStaffService(
                                bookingRepository, phongRepository, detailRepository);

                // Không cho phép chuyển từ DA_TRA_PHONG về DA_XAC_NHAN
                assertFalse(service.updateTrangThaiTransaction(
                                1, "DA_XAC_NHAN", 5));

                verify(bookingRepository, never()).save(any());
        }

        @Test
        void choCheckOutKhiDonDangO() {
                DatPhongRepository bookingRepository = mock(DatPhongRepository.class);
                PhongRepository phongRepository = mock(PhongRepository.class);
                ChiTietDatPhongRepository detailRepository = mock(ChiTietDatPhongRepository.class);

                DatPhong booking = new DatPhong();
                booking.setMaDp(1);
                booking.setTrangThai("DA_NHAN_PHONG");

                when(bookingRepository.findById(1))
                                .thenReturn(Optional.of(booking));

                Phong room = new Phong();
                room.setMaPhong(10);
                room.setTrangThaiPhong(TrangThaiPhong.DANG_O);

                when(phongRepository.findAllById(List.of(10)))
                                .thenReturn(List.of(room));

                ChiTietDatPhong detail = new ChiTietDatPhong();
                detail.setMaPhong(10);

                when(detailRepository.findByMaDp(1))
                                .thenReturn(List.of(detail));

                DatPhongStaffService service = new DatPhongStaffService(
                                bookingRepository, phongRepository, detailRepository);

                // Cho phép checkout khi đơn đang ở
                assertTrue(service.updateTrangThaiTransaction(
                                1, "DA_TRA_PHONG", 5));

                assertEquals("DA_TRA_PHONG", booking.getTrangThai());
                assertEquals(TrangThaiPhong.TRONG, room.getTrangThaiPhong());

                verify(bookingRepository).save(booking);
        }

        @Test
        void giuNguyenPhongBaoTriKhiHuyDon() {
                DatPhongRepository bookingRepository = mock(DatPhongRepository.class);
                PhongRepository phongRepository = mock(PhongRepository.class);
                ChiTietDatPhongRepository detailRepository = mock(ChiTietDatPhongRepository.class);

                DatPhong booking = new DatPhong();
                booking.setMaDp(1);
                booking.setTrangThai("DA_XAC_NHAN");

                when(bookingRepository.findById(1))
                                .thenReturn(Optional.of(booking));

                Phong bookedRoom = new Phong();
                bookedRoom.setMaPhong(10);
                bookedRoom.setTrangThaiPhong(TrangThaiPhong.DA_DAT);

                Phong maintenanceRoom = new Phong();
                maintenanceRoom.setMaPhong(11);
                maintenanceRoom.setTrangThaiPhong(TrangThaiPhong.BAO_TRI);

                List<Phong> rooms = List.of(bookedRoom, maintenanceRoom);

                when(phongRepository.findAllById(List.of(10, 11)))
                                .thenReturn(rooms);

                ChiTietDatPhong firstDetail = new ChiTietDatPhong();
                firstDetail.setMaPhong(10);

                ChiTietDatPhong secondDetail = new ChiTietDatPhong();
                secondDetail.setMaPhong(11);

                when(detailRepository.findByMaDp(1))
                                .thenReturn(List.of(firstDetail, secondDetail));

                DatPhongStaffService service = new DatPhongStaffService(
                                bookingRepository, phongRepository, detailRepository);

                assertTrue(service.updateTrangThaiTransaction(
                                1, "DA_HUY", 5));

                // Phòng đã đặt được giải phóng
                assertEquals(TrangThaiPhong.TRONG,
                                bookedRoom.getTrangThaiPhong());

                // Phòng bảo trì vẫn giữ nguyên trạng thái
                assertEquals(TrangThaiPhong.BAO_TRI,
                                maintenanceRoom.getTrangThaiPhong());

                verify(bookingRepository).save(booking);
        }
}
