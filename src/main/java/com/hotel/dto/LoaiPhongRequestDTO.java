package com.hotel.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record LoaiPhongRequestDTO (
    @NotBlank(message = "Tên loại phòng không được để trống.")
    @Size(max = 100, message = "Tên loại phòng tối đa 100 ký tự.")
    String tenLoai,

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự.")
    String moTa,

    @NotNull(message = "Giá mặc định không được để trống.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá mặc định phải lớn hơn 0.")
    @Digits(integer = 10, fraction = 2, message = "Giá mặc định không hợp lệ.")
    BigDecimal giaMacDinh,

    @NotNull(message = "Sức chứa không được để trống.")
    @Min(value = 1, message = "Sức chứa phải lớn hơn 1")
    Integer sucChua,

    @DecimalMin(value = "0.0", inclusive = false,  message = "Diện tích phải lớn hơn 0.")
    @Digits(integer = 4, fraction = 2, message = "Diện tích không hợp lệ.")
    BigDecimal dienTich,

    @Size(max = 255, message = "Đường dẫn hình ảnh tối đa 255 kí tự")
    String hinhAnh

){}
