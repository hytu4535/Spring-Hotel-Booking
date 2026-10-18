package com.hotel.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class LoaiPhongRequestDTO {

    @NotBlank(message = "Tên loại phòng là bắt buộc.")
    @Size(max = 100, message = "Tên loại phòng tối đa 100 ký tự.")
    private String tenLoai;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự.")
    private String moTa;

    @NotNull(message = "Giá mặc định là bắt buộc.")
    @DecimalMin(value = "1.00", message = "Giá mặc định phải lớn hơn 0.")
    @Digits(integer = 10, fraction = 2, message = "Giá mặc định không hợp lệ.")
    private BigDecimal giaMacDinh;

    @NotNull(message = "Sức chứa là bắt buộc.")
    @Positive(message = "Sức chứa phải lớn hơn 0.")
    private Integer sucChua;

    @DecimalMin(value = "0.01", message = "Diện tích phải lớn hơn 0.")
    @Digits(integer = 4, fraction = 2, message = "Diện tích không hợp lệ.")
    private BigDecimal dienTich;

    @Size(max = 255, message = "Tên hình ảnh tối đa 255 ký tự.")
    private String hinhAnh;

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public BigDecimal getGiaMacDinh() {
        return giaMacDinh;
    }

    public void setGiaMacDinh(BigDecimal giaMacDinh) {
        this.giaMacDinh = giaMacDinh;
    }

    public Integer getSucChua() {
        return sucChua;
    }

    public void setSucChua(Integer sucChua) {
        this.sucChua = sucChua;
    }

    public BigDecimal getDienTich() {
        return dienTich;
    }

    public void setDienTich(BigDecimal dienTich) {
        this.dienTich = dienTich;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
}
