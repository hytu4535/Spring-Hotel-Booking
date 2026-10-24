package com.hotel.service;

import com.hotel.dao.ThongKeDAO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ThongKeService {

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    public BigDecimal getDoanhThuHomNay() {
        return thongKeDAO.getDoanhThuHomNay();
    }

    public BigDecimal getDoanhThuThangNay() {
        return thongKeDAO.getDoanhThuThangNay();
    }

    public int getTongSoDon() {
        return thongKeDAO.getTongSoDon();
    }

    public Map<String, Object> getThongKePhong() {
        return thongKeDAO.getThongKePhong();
    }

    public List<Map<String, Object>> getDoanhThuTheoNgay(int days) {
        return thongKeDAO.getDoanhThuTheoNgay(days);
    }

    public List<Map<String, Object>> getDoanhThuTheoThang(int year) {
        return thongKeDAO.getDoanhThuTheoThang(year);
    }

    public List<Map<String, Object>> getHoatDongGganDay(int limit) {
        return thongKeDAO.getHoatDongGganDay(limit);
    }
}
