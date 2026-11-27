package com.hotel.controller.staff;

import com.hotel.model.DatPhong;
import com.hotel.model.Phong;
import com.hotel.repository.ChiTietDatPhongRepository;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.PhongRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class StaffDashboardController {

    private final PhongRepository phongRepository;
    private final DatPhongRepository datPhongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;

    public StaffDashboardController(
            PhongRepository phongRepository,
            DatPhongRepository datPhongRepository,
            ChiTietDatPhongRepository chiTietDatPhongRepository) {

        this.phongRepository = phongRepository;
        this.datPhongRepository = datPhongRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
    }

    @GetMapping("/staff/dashboard")
    public String dashboard(Model model) {

        // Ngày hiện tại
        LocalDate today = LocalDate.now();

        // Lấy danh sách phòng chưa bị xóa
        List<Phong> rooms = phongRepository.findAllByDaXoaFalse();

        // Lấy mã các phòng đang có khách đặt trong hôm nay
        List<Integer> bookedRoomIds =
                chiTietDatPhongRepository.findMaPhongDangDuocDatTrongNgay(
                        Date.valueOf(today)
                );

        // Chuyển sang Set để kiểm tra nhanh và tránh đếm trùng
        Set<Integer> bookedRoomSet = new HashSet<>(bookedRoomIds);

        // Tổng số phòng
        long totalRooms = rooms.size();

        // Số phòng đã đặt hoặc đang có khách lưu trú hôm nay
        long occupiedRooms = rooms.stream()
                .filter(room -> bookedRoomSet.contains(room.getMaPhong()))
                .count();

        // Số phòng còn trống hôm nay
        long availableRooms = totalRooms - occupiedRooms;

        // Lấy danh sách đơn đặt phòng
        List<DatPhong> bookings =
                datPhongRepository.findAllByOrderByNgayDatDesc();

        // Tính tổng giá trị đơn được tạo trong hôm nay
        BigDecimal todayRevenue = bookings.stream()
                .filter(booking -> booking.getNgayDat() != null
                        && booking.getNgayDat()
                        .toLocalDateTime()
                        .toLocalDate()
                        .equals(today))
                .map(DatPhong::getTongTien)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Đưa dữ liệu lên giao diện
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("occupiedRooms", occupiedRooms);
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("todayRevenue", todayRevenue);

        return "staff/dashboard";
    }
}