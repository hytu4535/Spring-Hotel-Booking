package com.hotel.mapper;

import com.hotel.dto.PhongRequestDTO;
import com.hotel.dto.PhongResponseDTO;
import com.hotel.model.Phong;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PhongMapper {
    Phong toModel(PhongRequestDTO requestDTO);
    PhongResponseDTO toResponse(Phong model);
}
