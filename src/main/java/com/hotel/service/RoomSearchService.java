package com.hotel.service;

import com.hotel.model.PhongTrongDTO;
import com.hotel.model.Phong;
import com.hotel.repository.PhongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomSearchService {
    private final PhongRepository phongRepository;

    public RoomSearchService(PhongRepository phongRepository) {
        this.phongRepository = phongRepository;
    }

    @Transactional(readOnly = true)
    public List<PhongTrongDTO> findAvailable(Date checkIn, Date checkOut) {
        return phongRepository.findAvailable(checkIn, checkOut).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PhongTrongDTO findDetails(int maPhong) {
        return phongRepository.findById(maPhong).map(this::toDto).orElse(null);
    }

    private PhongTrongDTO toDto(Phong room) {
        PhongTrongDTO dto = new PhongTrongDTO();
        dto.setMaPhong(room.getMaPhong());
        dto.setSoPhong(room.getSoPhong());
        dto.setTang(room.getTang());
        dto.setTenLoai(room.getLoaiPhong().getTenLoai());
        dto.setGiaPhong(room.getLoaiPhong().getGiaMacDinh());
        dto.setSucChua(room.getLoaiPhong().getSucChua());
        dto.setDienTich(room.getLoaiPhong().getDienTich());
        dto.setMoTa(room.getLoaiPhong().getMoTa());
        dto.setHinhAnh(room.getLoaiPhong().getHinhAnh());
        return dto;
    }
}
