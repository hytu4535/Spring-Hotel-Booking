package com.hotel.dto;

import com.hotel.enums.TrangThaiPhong;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PhongRequestDTO (
  @NotBlank(message = "Số phòng không được để trống")
  @Size(max = 10, message = "Tên phòng không quá 10 kí tự")
  String soPhong,

  @NotNull(message = "Vui lòng chọn loại phòng")
  Integer maLoaiPhong,

  @NotNull(message = "Tầng không được để trống")
  Integer tang,

  TrangThaiPhong trangThaiPhong,

  @Size(max = 255, message = "Ghi chú không vượt qua 255 kí tự")
  String ghiChu

){}
