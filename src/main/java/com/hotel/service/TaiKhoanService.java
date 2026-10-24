package com.hotel.service;

import com.hotel.model.TaiKhoan;
import com.hotel.repository.TaiKhoanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaiKhoanService {
    private final TaiKhoanRepository taiKhoanRepository;

    public TaiKhoanService(TaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    public TaiKhoan findById(int maTk) {
        return taiKhoanRepository.findById(maTk).orElse(null);
    }

    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        return taiKhoanRepository.findByTenDangNhapIgnoreCase(tenDangNhap).orElse(null);
    }

    public List<TaiKhoan> findAll() {
        return taiKhoanRepository.findAllByOrderByNgayTaoDesc();
    }

    public boolean update(TaiKhoan tk) {
        taiKhoanRepository.save(tk);
        return true;
    }

    public boolean updateMatKhau(int maTk, String matKhauHash) {
        Optional<TaiKhoan> account = taiKhoanRepository.findById(maTk);
        if (account.isEmpty()) {
            return false;
        }
        account.get().setMatKhau(matKhauHash);
        taiKhoanRepository.save(account.get());
        return true;
    }

    public boolean updateTrangThai(int maTk, int trangThai) {
        Optional<TaiKhoan> account = taiKhoanRepository.findById(maTk);
        if (account.isEmpty()) {
            return false;
        }
        account.get().setTrangThai(trangThai);
        taiKhoanRepository.save(account.get());
        return true;
    }

    public boolean existsTenDangNhap(String tenDangNhap, Integer excludeMaTk) {
        return excludeMaTk == null
                ? taiKhoanRepository.existsByTenDangNhapIgnoreCase(tenDangNhap)
                : taiKhoanRepository.existsByTenDangNhapIgnoreCaseAndMaTkNot(tenDangNhap, excludeMaTk);
    }

    public boolean existsEmail(String email, Integer excludeMaTk) {
        return excludeMaTk == null
                ? taiKhoanRepository.existsByEmailIgnoreCase(email)
                : taiKhoanRepository.existsByEmailIgnoreCaseAndMaTkNot(email, excludeMaTk);
    }
}