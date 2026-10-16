package com.hotel.service;

import com.hotel.dao.DatPhongDAO;
import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.DatPhong;
import com.hotel.model.KhachHang;

import java.util.List;

public class DatPhongService {

    private final DatPhongDAO datPhongDAO = new DatPhongDAO();

    // Trả về chuỗi Mã Tra Cứu nếu thành công. Null nếu xảy ra lỗi / trùng lịch.
    public String taoDonDatPhong(KhachHang khachHang, DatPhong datPhong, List<ChiTietDatPhong> danhSachChiTiet) {
        if (khachHang == null || datPhong == null || danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
            return null;
        }
        
        // Gọi DAO thực thi Transaction
        return datPhongDAO.taoDonDatPhongTransaction(khachHang, datPhong, danhSachChiTiet);
    }
}