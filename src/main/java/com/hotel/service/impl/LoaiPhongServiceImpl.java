package com.hotel.service.impl;

import com.hotel.dto.LoaiPhongRequestDTO;
import com.hotel.dto.LoaiPhongResponseDTO;
import com.hotel.dto.PhongResponseDTO;
import com.hotel.mapper.LoaiPhongMapper;
import com.hotel.model.LoaiPhong;
import com.hotel.model.Phong;
import com.hotel.repository.LoaiPhongRepository;
import com.hotel.service.LoaiPhongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoaiPhongServiceImpl implements LoaiPhongService {
    private final LoaiPhongRepository loaiPhongRepository;
    private final LoaiPhongMapper loaiPhongMapper;


    @Override
    @Transactional(readOnly = true)
    public List<LoaiPhongResponseDTO> getAllLoaiPhong() {
        List<LoaiPhong> dsLoaiPhong = loaiPhongRepository.findAllByDaXoaFalse();
        return dsLoaiPhong.stream()
                .map(loaiPhongMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LoaiPhongResponseDTO getLoaiPhongById(Integer id) {
        LoaiPhong loaiPhong = loaiPhongRepository.findByMaLoaiAndDaXoaFalse(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng có ID: " + id));
        return loaiPhongMapper.toResponse(loaiPhong);
    }

    @Override
    @Transactional
    public LoaiPhongResponseDTO createLoaiPhong(LoaiPhongRequestDTO requestDTO){
        if (loaiPhongRepository.existsByTenLoaiIgnoreCase(requestDTO.tenLoai())) {
            throw new RuntimeException("Tên loại phòng đã tồn tại!");
        }

        LoaiPhong model = loaiPhongMapper.toModel(requestDTO);
        LoaiPhong savedModel = loaiPhongRepository.save(model);
        return loaiPhongMapper.toResponse(savedModel);
    }

    @Override
    @Transactional
    public List<LoaiPhongResponseDTO> searchLoaiPhong(String tenLoai) {
        List<LoaiPhong> dsLoaiPhong;
        if (tenLoai != null && !tenLoai.trim().isEmpty()) {
            dsLoaiPhong = loaiPhongRepository.findByTenLoaiIgnoreCase(tenLoai.trim());
        } else {
            dsLoaiPhong = loaiPhongRepository.findAll();
        }
        return dsLoaiPhong.stream()
                .map(loaiPhongMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LoaiPhongResponseDTO updateLoaiPhong(Integer id, LoaiPhongRequestDTO requestDTO){
        LoaiPhong existLoaiPhong = loaiPhongRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng có ID: " + id));

        if (loaiPhongRepository.existsByTenLoaiIgnoreCaseAndMaLoaiNot(requestDTO.tenLoai(), id)) {
            throw new RuntimeException("Tên loại phòng đã được sử dụng bởi loại phòng khác!");
        }

        existLoaiPhong.setTenLoai(requestDTO.tenLoai());
        existLoaiPhong.setMoTa(requestDTO.moTa());
        existLoaiPhong.setGiaMacDinh(requestDTO.giaMacDinh());
        existLoaiPhong.setSucChua(requestDTO.sucChua());
        existLoaiPhong.setDienTich(requestDTO.dienTich());
        existLoaiPhong.setHinhAnh(requestDTO.hinhAnh());

        LoaiPhong updatedModel = loaiPhongRepository.save(existLoaiPhong);
        return loaiPhongMapper.toResponse(updatedModel);
    }

    @Override
    @Transactional
    public void deleteLoaiPhong(Integer id) {
        LoaiPhong loaiPhong = loaiPhongRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng có ID: " + id));

        loaiPhong.setDaXoa(true);
        loaiPhongRepository.save(loaiPhong);
    }


}
