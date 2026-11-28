package com.hotel.service;

import com.hotel.model.TaiKhoan;
import com.hotel.repository.TaiKhoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

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

    public Optional<TaiKhoan> findByProviderAndProviderId(String provider, String providerId) {
        return taiKhoanRepository.findByProviderAndProviderId(provider, providerId);
    }

    public Optional<TaiKhoan> findByEmail(String email) {
        return taiKhoanRepository.findByEmailIgnoreCase(email);
    }

    public Optional<TaiKhoan> findActiveByUsername(String username) {
        return taiKhoanRepository.findByTenDangNhapAndTrangThaiAndDaXoaFalse(username, 1);
    }

    public TaiKhoan save(TaiKhoan account) {
        return taiKhoanRepository.save(account);
    }

    public List<TaiKhoan> findAll() {
        return taiKhoanRepository.findAllByOrderByNgayTaoDesc();
    }

    public List<TaiKhoan> search(String tenDangNhap, String hoTen,
                                 String vaiTro, Integer trangThai, String sort,
                                 String xoaFilter) {
        String username = normalize(tenDangNhap);
        String name = normalize(hoTen);
        List<TaiKhoan> source = "deleted".equalsIgnoreCase(xoaFilter)
                ? taiKhoanRepository.findAllByOrderByNgayTaoDesc().stream()
                    .filter(TaiKhoan::isDaXoa).collect(Collectors.toList())
                : "all".equalsIgnoreCase(xoaFilter)
                    ? taiKhoanRepository.findAllByOrderByNgayTaoDesc()
                    : taiKhoanRepository.findAllByDaXoaFalseOrderByNgayTaoDesc();
        List<TaiKhoan> result = source.stream()
                .filter(item -> username.isEmpty() || contains(item.getTenDangNhap(), username))
                .filter(item -> name.isEmpty() || contains(item.getHoTen(), name))
                .filter(item -> vaiTro == null || vaiTro.isBlank() || vaiTro.equalsIgnoreCase(item.getVaiTro()))
                .filter(item -> trangThai == null || item.getTrangThai() == trangThai)
                .collect(Collectors.toList());
        Comparator<TaiKhoan> comparator = Comparator.comparing(
                TaiKhoan::getNgayTao,
                Comparator.nullsLast(Comparator.naturalOrder()));
        if ("asc".equalsIgnoreCase(sort)) {
            result.sort(comparator);
        } else if ("name".equalsIgnoreCase(sort)) {
            result.sort(Comparator.comparing(
                    TaiKhoan::getHoTen,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        } else {
            result.sort(comparator.reversed());
        }
        return result;
    }

    @Transactional
    public boolean update(TaiKhoan tk) {
        taiKhoanRepository.saveAndFlush(tk);
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

    @Transactional
    public boolean softDelete(int maTk) {
        Optional<TaiKhoan> account = taiKhoanRepository.findById(maTk);
        if (account.isEmpty() || account.get().isDaXoa()) {
            return false;
        }
        account.get().setDaXoa(true);
        taiKhoanRepository.saveAndFlush(account.get());
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

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }
}