package com.hotel.dto;

import java.math.BigDecimal;

public record LoaiPhongResponseDTO (
    Integer maLoai,
    String tenLoai,
    String moTa,
    BigDecimal giaMacDinh,
    Integer sucChua,
    BigDecimal dienTich,
    String hinhAnh,
    Long soLuongPhong
){}
