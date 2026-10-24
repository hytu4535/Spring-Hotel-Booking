package com.hotel.service;

import com.hotel.dto.PhongRequestDTO;
import com.hotel.dto.PhongResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PhongService{
    List<PhongResponseDTO> getAllPhong();
    PhongResponseDTO getPhongById(Integer id);
    PhongResponseDTO createPhong(PhongRequestDTO requestDTO);
    PhongResponseDTO updatePhong(Integer id, PhongRequestDTO requestDTO);
    void deletePhong(Integer id);
}
