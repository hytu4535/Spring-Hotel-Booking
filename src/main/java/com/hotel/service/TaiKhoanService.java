package com.hotel.service;

import com.hotel.dao.TaiKhoanDAO;
import com.hotel.model.TaiKhoan;

public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public TaiKhoan findById(int maTk) {
        return taiKhoanDAO.findById(maTk);
    }

    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        return taiKhoanDAO.findByTenDangNhap(tenDangNhap);
    }

    public boolean update(TaiKhoan tk) {
        return taiKhoanDAO.update(tk);
    }

    public boolean updateMatKhau(int maTk, String matKhauHash) {
        return taiKhoanDAO.updateMatKhau(maTk, matKhauHash);
    }
}