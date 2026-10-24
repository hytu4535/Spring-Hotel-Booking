package com.hotel.controller;

import com.hotel.model.TaiKhoan;
import com.hotel.service.DatPhongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.util.Map;

@RestController
@RequestMapping({"/staff", "/admin"})
public class DatDichVuRestController {

    private final DatPhongService datPhongService;

    public DatDichVuRestController(DatPhongService datPhongService) {
        this.datPhongService = datPhongService;
    }

    @PostMapping("/api/them-dich-vu")
    public ResponseEntity<?> themDichVu(
            @RequestParam Integer maDp,
            @RequestParam Integer maDv,
            @RequestParam Integer soLuong,
            @RequestParam(required = false) String ngaySuDung,
            HttpSession session) {
        try {
            TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
            Date sqlDate = (ngaySuDung != null && !ngaySuDung.isEmpty()) ? Date.valueOf(ngaySuDung) : new Date(System.currentTimeMillis());
            Map<String, Object> result = datPhongService.themDichVuPhatSinh(maDp, maDv, soLuong, sqlDate, currentUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/api/hoan-tien")
    public ResponseEntity<?> hoanTien(
            @RequestParam Integer maDp,
            @RequestParam java.math.BigDecimal soTienHoan,
            @RequestParam(required = false) String phuongThuc,
            @RequestParam(required = false) String ghiChu,
            HttpSession session) {
        try {
            TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
            Map<String, Object> result = datPhongService.hoanTienVaHuyDon(maDp, soTienHoan, phuongThuc, ghiChu, currentUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
