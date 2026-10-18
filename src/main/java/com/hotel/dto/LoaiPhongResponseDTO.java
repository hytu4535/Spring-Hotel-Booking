package com.hotel.dto;

import java.math.BigDecimal;

public class LoaiPhongResponseDTO {

    private Integer maLoai;
    private String tenLoai;
    private String moTa;
    private BigDecimal giaMacDinh;
    private Integer sucChua;
    private BigDecimal dienTich;
    private String hinhAnh;
    private Long soLuongPhong;

    public Integer getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(Integer maLoai) {
        this.maLoai = maLoai;
    }

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

    public Long getSoLuongPhong() {
        return soLuongPhong;
    }

    public void setSoLuongPhong(Long soLuongPhong) {
        this.soLuongPhong = soLuongPhong;
    }
}
