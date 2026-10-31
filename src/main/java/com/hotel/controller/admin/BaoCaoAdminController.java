package com.hotel.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/admin/bao-cao")
public class BaoCaoAdminController {

    @GetMapping
    public String baoCao(Model model) {
        // Mock data for statistical reports
        model.addAttribute("tongDoanhThu", new BigDecimal("185000000"));
        model.addAttribute("tongDonDat", 142);
        model.addAttribute("tyLeLapDay", 78.5);

        // Top services used
        List<Map<String, Object>> topServices = new ArrayList<>();
        topServices.add(Map.of("tenDv", "Ăn sáng buffet", "soLuong", 320, "doanhThu", new BigDecimal("48000000")));
        topServices.add(Map.of("tenDv", "Dưa đón sân bay", "soLuong", 85, "doanhThu", new BigDecimal("25500000")));
        topServices.add(Map.of("tenDv", "Massage - Spa", "soLuong", 64, "doanhThu", new BigDecimal("25600000")));
        topServices.add(Map.of("tenDv", "Giặt ủi", "soLuong", 110, "doanhThu", new BigDecimal("5500000")));
        model.addAttribute("topServices", topServices);

        // Top rooms booked
        List<Map<String, Object>> topRooms = new ArrayList<>();
        topRooms.add(Map.of("soPhong", "Phòng 305", "tenLoai", "Deluxe Double", "soLuotDat", 28, "doanhThu", new BigDecimal("42000000")));
        topRooms.add(Map.of("soPhong", "Phòng 201", "tenLoai", "Superior Twin", "soLuotDat", 25, "doanhThu", new BigDecimal("31250000")));
        topRooms.add(Map.of("soPhong", "Phòng 402", "tenLoai", "Suite Luxury", "soLuotDat", 19, "doanhThu", new BigDecimal("57000000")));
        topRooms.add(Map.of("soPhong", "Phòng 102", "tenLoai", "Standard Single", "soLuotDat", 15, "doanhThu", new BigDecimal("12000000")));
        model.addAttribute("topRooms", topRooms);

        // Monthly revenue mock
        List<Map<String, Object>> monthlyRevenue = new ArrayList<>();
        monthlyRevenue.add(Map.of("thang", "Tháng 7/2026", "doanhThu", new BigDecimal("45000000"), "soDon", 35));
        monthlyRevenue.add(Map.of("thang", "Tháng 8/2026", "doanhThu", new BigDecimal("58000000"), "soDon", 44));
        monthlyRevenue.add(Map.of("thang", "Tháng 9/2026", "doanhThu", new BigDecimal("39000000"), "soDon", 29));
        monthlyRevenue.add(Map.of("thang", "Tháng 10/2026", "doanhThu", new BigDecimal("43000000"), "soDon", 34));
        model.addAttribute("monthlyRevenue", monthlyRevenue);

        return "admin/bao-cao-thong-ke";
    }
}
