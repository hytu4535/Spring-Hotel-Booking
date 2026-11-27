package com.hotel.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record PhongRequestDTO (
  @NotBlank(message = "Số phòng không được để trống")
  @Size(max = 10, message = "Tên phòng không quá 10 kí tự")
  String soPhong,

  @NotNull(message = "Vui lòng chọn loại phòng")
  Integer maLoaiPhong,

  @NotNull(message = "Tầng không được để trống")
  Integer tang,

  @Size(max = 255, message = "Ghi chú không vượt qua 255 kí tự")
  String ghiChu

){}
