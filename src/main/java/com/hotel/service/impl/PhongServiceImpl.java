package com.hotel.service.impl;


import com.hotel.dto.PhongRequestDTO;
import com.hotel.dto.PhongResponseDTO;
import com.hotel.mapper.PhongMapper;
import com.hotel.model.LoaiPhong;
import com.hotel.model.Phong;
import com.hotel.repository.LoaiPhongRepository;
import com.hotel.repository.PhongRepository;
import com.hotel.service.PhongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhongServiceImpl implements PhongService {

    private final PhongRepository phongRepository;
    private final PhongMapper phongMapper;
    private final LoaiPhongRepository loaiPhongRepository;

    @Override
    @Transactional(readOnly = true)
    public PhongResponseDTO getSoDoPhong() {
        return new PhongResponseDTO(null, null, null, null, null, null, null);
    }


    @Override
    @Transactional(readOnly = true)
    public List<PhongResponseDTO> getAllPhong() {
        List<Phong> dsPhong = phongRepository.findAllByDaXoaFalse();
        return dsPhong.stream()
                .map(phongMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PhongResponseDTO getPhongById(Integer id) {
        Phong phong = phongRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng có ID: " +  id));
        return phongMapper.toResponse(phong);
    }

    @Override
    @Transactional
    public PhongResponseDTO createPhong(PhongRequestDTO requestDTO) {
        if (phongRepository.existsBySoPhongIgnoreCaseAndDaXoaFalse(requestDTO.soPhong())){
            throw new RuntimeException("Tên/Số phòng đã tồn tại!");
        }
        LoaiPhong loaiPhong = loaiPhongRepository.findByMaLoaiAndDaXoaFalse(requestDTO.maLoaiPhong())
                .orElseThrow(() -> new RuntimeException("Loại phòng không tồn tại!"));

        Phong model = phongMapper.toModel(requestDTO);
        model.setMaLoaiPhong(loaiPhong);
        model.setDaXoa(false);
        Phong savedModel = phongRepository.save(model);
        return phongMapper.toResponse(savedModel);
    }

    @Override
    @Transactional
    public PhongResponseDTO updatePhong(Integer id, PhongRequestDTO requestDTO){
        Phong exist = phongRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Phòng có ID: " + id));

        if (phongRepository.existsBySoPhongIgnoreCaseAndMaPhongNot(requestDTO.soPhong(), id)) {
            throw new RuntimeException("Tên phòng đã được sử dụng bởi mã phòng khác!");
        }

        LoaiPhong loaiPhong = loaiPhongRepository.findByMaLoaiAndDaXoaFalse(requestDTO.maLoaiPhong())
                .orElseThrow(() -> new RuntimeException("Loại phòng không tồn tại!"));


        exist.setSoPhong(requestDTO.soPhong());
        exist.setMaLoaiPhong(loaiPhong);
        exist.setTang(requestDTO.tang());
        exist.setTrangThaiPhong(requestDTO.trangThaiPhong());
        exist.setGhiChu(requestDTO.ghiChu());

        Phong updatedModel = phongRepository.save(exist);
        return phongMapper.toResponse(updatedModel);
    }

    @Override
    @Transactional
    public void deletePhong(Integer id) {
        Phong phong = phongRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng có ID: " + id));
        phong.setDaXoa(true);
        phongRepository.save(phong);
    }


    @Override
    @Transactional
    public List<PhongResponseDTO> searchPhong(String soPhong) {
        List<Phong> dsPhong;
        if (soPhong != null && !soPhong.trim().isEmpty()) {
            dsPhong = phongRepository.findBySoPhongContainingIgnoreCase(soPhong.trim());
        } else {
            dsPhong = phongRepository.findAll();
        }
        return dsPhong.stream()
                .map(phongMapper::toResponse)
                .collect(Collectors.toList());
    }

}
