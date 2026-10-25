package com.hotel.service;

import com.hotel.model.PhongSoDoDTO;
import com.hotel.repository.PhongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PhongService {
    private final PhongRepository phongRepository;

    public PhongService(PhongRepository phongRepository) {
        this.phongRepository = phongRepository;
    }

    @Transactional(readOnly = true)
    public List<PhongSoDoDTO> getSoDoPhong() {
        List<PhongSoDoDTO> result = new ArrayList<>();
        for (Map<String, Object> row : phongRepository.getSoDoPhongNative()) {
            PhongSoDoDTO dto = new PhongSoDoDTO();
            dto.setMaPhong(number(row.get("maPhong")));
            dto.setSoPhong(string(row.get("soPhong")));
            dto.setTang(number(row.get("tang")));
            dto.setTrangThai(string(row.get("trangThai")));
            dto.setTenLoaiPhong(string(row.get("tenLoaiPhong")));
            Object maDp = row.get("maDpHienTai");
            if (maDp != null) {
                dto.setMaDpHienTai(number(maDp));
            }

            dto.setTenKhachHang(string(row.get("tenKhachHang")));
            result.add(dto);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<com.hotel.model.Phong> findAll() { return phongRepository.findAll(); }
    @Transactional
    public com.hotel.model.Phong save(com.hotel.model.Phong phong) { return phongRepository.save(phong); }
    @Transactional(readOnly = true)
    public com.hotel.model.Phong get(int id) { return phongRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng.")); }
    @Transactional
    public void delete(int id) { phongRepository.deleteById(id); }

    private int number(Object value) {
        return value == null ? 0 : ((Number) value).intValue();
    }

    private String string(Object value) {
        return value == null ? null : value.toString();
    }
}