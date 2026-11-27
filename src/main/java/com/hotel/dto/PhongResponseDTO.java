package com.hotel.dto;


public record PhongResponseDTO (
    Integer maPhong,
    String soPhong,
    Integer maLoaiPhong,
    String tenLoaiPhong,
    Integer tang,
    String ghiChu
){}
