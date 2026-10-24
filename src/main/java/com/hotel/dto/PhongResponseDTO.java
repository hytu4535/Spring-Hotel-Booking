package com.hotel.dto;

import com.hotel.enums.TrangThaiPhong;

public record PhongResponseDTO (
    Integer maPhong,
    String soPhong,
    Integer maLoaiPhong,
    String tenLoaiPhong,
    Integer tang,
    TrangThaiPhong trangThaiPhong,
    String ghiChu
){}
