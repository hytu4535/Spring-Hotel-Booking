package com.hotel.mapper;

import com.hotel.dto.LoaiPhongRequestDTO;
import com.hotel.dto.LoaiPhongResponseDTO;
import com.hotel.model.LoaiPhong;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoaiPhongMapper {
    LoaiPhong toModel(LoaiPhongRequestDTO requestDTO);
    LoaiPhongResponseDTO toResponse(LoaiPhong model);
}
