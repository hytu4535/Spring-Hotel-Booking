package com.hotel.service;

import com.hotel.dto.LoaiPhongRequestDTO;
import com.hotel.dto.LoaiPhongResponseDTO;
import com.hotel.model.LoaiPhong;
import com.hotel.repository.LoaiPhongRepository;
import com.hotel.repository.PhongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Locale;

@Service
public class LoaiPhongService {

    private final LoaiPhongRepository loaiPhongRepository;
    private final PhongRepository phongRepository;

    public LoaiPhongService(LoaiPhongRepository loaiPhongRepository, PhongRepository phongRepository) {
        this.loaiPhongRepository = loaiPhongRepository;
        this.phongRepository = phongRepository;
    }

    @Transactional(readOnly = true)
    public List<LoaiPhongResponseDTO> getAll() {
        return loaiPhongRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoaiPhongResponseDTO> search(String tenLoai) {
        String filter = tenLoai == null ? "" : tenLoai.trim().toLowerCase(Locale.ROOT);
        return getAll().stream()
                .filter(item -> filter.isEmpty()
                        || (item.getTenLoai() != null
                        && item.getTenLoai().toLowerCase(Locale.ROOT).contains(filter)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LoaiPhongRequestDTO getForEdit(Integer maLoai) {
        LoaiPhong loaiPhong = loaiPhongRepository.findById(maLoai)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại phòng."));
        return toRequestDTO(loaiPhong);
    }

    @Transactional
    public void create(LoaiPhongRequestDTO request) {
        if (loaiPhongRepository.existsByTenLoaiIgnoreCase(request.getTenLoai().trim())) {
            throw new IllegalArgumentException("Tên loại phòng đã tồn tại.");
        }

        LoaiPhong loaiPhong = new LoaiPhong();
        applyRequestToEntity(request, loaiPhong);
        loaiPhongRepository.save(loaiPhong);
    }

    @Transactional
    public void update(Integer maLoai, LoaiPhongRequestDTO request) {
        LoaiPhong loaiPhong = loaiPhongRepository.findById(maLoai)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại phòng."));

        if (loaiPhongRepository.existsByTenLoaiIgnoreCaseAndMaLoaiNot(request.getTenLoai().trim(), maLoai)) {
            throw new IllegalArgumentException("Tên loại phòng đã tồn tại.");
        }

        applyRequestToEntity(request, loaiPhong);
        loaiPhongRepository.save(loaiPhong);
    }

    @Transactional
    public void delete(Integer maLoai) {
        if (!loaiPhongRepository.existsById(maLoai)) {
            throw new IllegalArgumentException("Không tìm thấy loại phòng.");
        }

        long soLuongPhong = phongRepository.countByLoaiPhong_MaLoai(maLoai);
        if (soLuongPhong > 0) {
            throw new IllegalStateException("Không thể xóa loại phòng vì đang có " + soLuongPhong + " phòng sử dụng.");
        }

        loaiPhongRepository.deleteById(maLoai);
    }

    private LoaiPhongResponseDTO toResponseDTO(LoaiPhong loaiPhong) {
        LoaiPhongResponseDTO dto = new LoaiPhongResponseDTO();
        dto.setMaLoai(loaiPhong.getMaLoai());
        dto.setTenLoai(loaiPhong.getTenLoai());
        dto.setMoTa(loaiPhong.getMoTa());
        dto.setGiaMacDinh(loaiPhong.getGiaMacDinh());
        dto.setSucChua(loaiPhong.getSucChua());
        dto.setDienTich(loaiPhong.getDienTich());
        dto.setHinhAnh(loaiPhong.getHinhAnh());
        dto.setSoLuongPhong(phongRepository.countByLoaiPhong_MaLoai(loaiPhong.getMaLoai()));
        return dto;
    }

    private LoaiPhongRequestDTO toRequestDTO(LoaiPhong loaiPhong) {
        LoaiPhongRequestDTO dto = new LoaiPhongRequestDTO();
        dto.setTenLoai(loaiPhong.getTenLoai());
        dto.setMoTa(loaiPhong.getMoTa());
        dto.setGiaMacDinh(loaiPhong.getGiaMacDinh());
        dto.setSucChua(loaiPhong.getSucChua());
        dto.setDienTich(loaiPhong.getDienTich());
        dto.setHinhAnh(loaiPhong.getHinhAnh());
        return dto;
    }

    private void applyRequestToEntity(LoaiPhongRequestDTO request, LoaiPhong loaiPhong) {
        loaiPhong.setTenLoai(request.getTenLoai().trim());
        loaiPhong.setMoTa(request.getMoTa() != null ? request.getMoTa().trim() : null);
        loaiPhong.setGiaMacDinh(request.getGiaMacDinh());
        loaiPhong.setSucChua(request.getSucChua());
        loaiPhong.setDienTich(request.getDienTich());
        loaiPhong.setHinhAnh(request.getHinhAnh() != null ? request.getHinhAnh().trim() : null);
    }
}
