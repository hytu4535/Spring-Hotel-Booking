package com.hotel.controller.staff;

import com.hotel.enums.TrangThaiPhong;
import com.hotel.model.DatPhong;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.PhongRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class StaffDashboardController {
    private final PhongRepository phongRepository;
    private final DatPhongRepository datPhongRepository;

    public StaffDashboardController(PhongRepository phongRepository, DatPhongRepository datPhongRepository) {
        this.phongRepository = phongRepository;
        this.datPhongRepository = datPhongRepository;
    }

    @GetMapping("/staff/dashboard")
    public String dashboard(Model model) {
        List<com.hotel.model.Phong> rooms = phongRepository.findAllByDaXoaFalse();
        List<DatPhong> bookings = datPhongRepository.findAllByOrderByNgayDatDesc();
        LocalDate today = LocalDate.now();

        model.addAttribute("totalRooms", rooms.size());
        model.addAttribute("occupiedRooms", rooms.stream()
                .filter(room -> room.getTrangThaiPhong() == TrangThaiPhong.DANG_O
                        || room.getTrangThaiPhong() == TrangThaiPhong.DA_DAT)
                .count());
        model.addAttribute("availableRooms", rooms.stream()
                .filter(room -> room.getTrangThaiPhong() == TrangThaiPhong.TRONG)
                .count());
        model.addAttribute("todayRevenue", bookings.stream()
                .filter(booking -> booking.getNgayDat() != null
                        && booking.getNgayDat().toLocalDateTime().toLocalDate().equals(today))
                .map(DatPhong::getTongTien)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return "staff/dashboard";
    }
}
