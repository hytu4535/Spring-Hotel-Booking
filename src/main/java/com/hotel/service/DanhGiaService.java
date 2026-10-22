package com.hotel.service;

import com.hotel.model.DatPhong;
import com.hotel.model.DanhGia;
import com.hotel.model.KhachHang;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.DanhGiaRepository;
import com.hotel.repository.KhachHangRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class DanhGiaService {

    private final DanhGiaRepository danhGiaRepository;
    private final DatPhongRepository datPhongRepository;
    private final KhachHangRepository khachHangRepository;

    public DanhGiaService(DanhGiaRepository danhGiaRepository,
                          DatPhongRepository datPhongRepository,
                          KhachHangRepository khachHangRepository) {
        this.danhGiaRepository = danhGiaRepository;
        this.datPhongRepository = datPhongRepository;
        this.khachHangRepository = khachHangRepository;
    }

    public List<DanhGia> getDanhGiaByLoaiPhong(Integer maLoai) {
        return danhGiaRepository.findByLoaiPhong(maLoai);
    }

    public Optional<DanhGia> getDanhGiaByMaDp(Integer maDp) {
        return danhGiaRepository.findByDatPhong_MaDp(maDp);
    }

    @Transactional
    public boolean submitReview(Integer maDp, Integer maKh, Integer soSao, String noiDung) {
        Optional<DatPhong> dpOpt = datPhongRepository.findById(maDp);
        if (!dpOpt.isPresent()) {
            return false;
        }
        DatPhong dp = dpOpt.get();

        // Rule: Only review if booking belongs to customer and status is DA_TRA_PHONG
        if (dp.getKhachHang() == null || !dp.getKhachHang().getMaKh().equals(maKh)) {
            return false;
        }

        if (!"DA_TRA_PHONG".equals(dp.getTrangThai())) {
            return false;
        }

        Optional<KhachHang> khOpt = khachHangRepository.findById(maKh);
        if (!khOpt.isPresent()) {
            return false;
        }

        // Check if already reviewed
        Optional<DanhGia> existingDg = danhGiaRepository.findByDatPhong_MaDp(maDp);
        DanhGia dg = existingDg.orElse(new DanhGia());
        dg.setDatPhong(dp);
        dg.setKhachHang(khOpt.get());
        dg.setSoSao(soSao != null ? soSao : 5);
        dg.setNoiDung(noiDung);
        dg.setNgayDanhGia(new Timestamp(System.currentTimeMillis()));

        danhGiaRepository.save(dg);
        return true;
    }
}
