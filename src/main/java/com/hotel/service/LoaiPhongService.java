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
public interface LoaiPhongService {
    List<LoaiPhongResponseDTO> getAllLoaiPhong();
    LoaiPhongResponseDTO getLoaiPhongById(Integer id);
    LoaiPhongResponseDTO createLoaiPhong(LoaiPhongRequestDTO requestDTO);
    LoaiPhongResponseDTO updateLoaiPhong(Integer id, LoaiPhongRequestDTO requestDTO);
    void deleteLoaiPhong(Integer id);
    List<LoaiPhongResponseDTO> searchLoaiPhong(String tenLoai);
}
