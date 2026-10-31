package com.hotel.mapper;

import com.hotel.dto.PhongRequestDTO;
import com.hotel.dto.PhongResponseDTO;
import com.hotel.model.Phong;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PhongMapper {
    @Mapping(target = "maLoaiPhong", ignore = true)
    Phong toModel(PhongRequestDTO requestDTO);

    @Mapping(target = "maLoaiPhong", source = "maLoaiPhong.maLoai")
    @Mapping(target = "tenLoaiPhong", source = "maLoaiPhong.tenLoai")
    PhongResponseDTO toResponse(Phong model);
}
