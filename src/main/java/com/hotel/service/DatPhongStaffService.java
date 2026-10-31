package com.hotel.service;

import com.hotel.enums.TrangThaiPhong;
import com.hotel.model.DatPhong;
import com.hotel.model.DatPhongDTO;
import com.hotel.model.Phong;
import com.hotel.repository.ChiTietDatPhongRepository;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.PhongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatPhongStaffService {

    private final DatPhongRepository datPhongRepository;
    private final PhongRepository phongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;

    public DatPhongStaffService(DatPhongRepository datPhongRepository, PhongRepository phongRepository, ChiTietDatPhongRepository chiTietDatPhongRepository) {
        this.datPhongRepository = datPhongRepository;
        this.phongRepository = phongRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
    }

    @Transactional(readOnly = true)
    public List<DatPhongDTO> findAllWithKhachHang() {
        return datPhongRepository.findAllByOrderByNgayDatDesc().stream().map(dp -> {
            DatPhongDTO dto = new DatPhongDTO();
            dto.setMaDp(dp.getMaDp());
            dto.setMaTraCuu(dp.getMaTraCuu());
            dto.setMaKh(dp.getMaKh());
            dto.setNgayDat(dp.getNgayDat());
            dto.setNgayNhanPhong(dp.getNgayNhanPhong());
            dto.setNgayTraPhong(dp.getNgayTraPhong());
            dto.setTrangThai(dp.getTrangThai());
            dto.setTongTien(dp.getTongTien());
            dto.setTienPhong(dp.getTienPhong());
            dto.setTienDichVu(dp.getTienDichVu());

            if (dp.getKhachHang() != null) {
                dto.setTenKhachHang(dp.getKhachHang().getHoTen());
                dto.setEmailKhachHang(dp.getKhachHang().getEmail());
                dto.setSdtKhachHang(dp.getKhachHang().getSoDienThoai());
            }

            if (dp.getNhanVienXuLy() != null) {
                dto.setTenNhanVienXuLy(dp.getNhanVienXuLy().getHoTen());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public boolean updateTrangThaiTransaction(int maDp, String newTrangThai, Integer maNvXuLy) {
        DatPhong dp = datPhongRepository.findById(maDp).orElse(null);
        if (dp == null) {
            return false;
        }

        dp.setTrangThai(newTrangThai);
        dp.setMaNvXuLy(maNvXuLy);

        TrangThaiPhong phongTrangThai = null;
        if (newTrangThai.equals("DA_XAC_NHAN")) {
            phongTrangThai = TrangThaiPhong.DA_DAT;
        } else if (newTrangThai.equals("DA_NHAN_PHONG")) {
            phongTrangThai = TrangThaiPhong.DANG_O;
        } else if (newTrangThai.equals("DA_TRA_PHONG") || newTrangThai.equals("DA_HUY")) {
            phongTrangThai = TrangThaiPhong.TRONG;
        }

        if (phongTrangThai != null) {
            List<Integer> maPhongList = chiTietDatPhongRepository.findByMaDp(maDp)
                    .stream()
                    .map(ct -> ct.getMaPhong())
                    .collect(Collectors.toList());

            if (!maPhongList.isEmpty()) {
                List<Phong> phongs = phongRepository.findAllById(maPhongList);
                for (Phong p : phongs) {
                    p.setTrangThaiPhong(phongTrangThai);
                }
                phongRepository.saveAll(phongs);
            }
        }

        datPhongRepository.save(dp);
        return true;
    }
}