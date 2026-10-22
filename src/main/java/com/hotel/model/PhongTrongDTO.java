package com.hotel.model;

import java.math.BigDecimal;

public class PhongTrongDTO {
    private int maPhong;
    private int maLoai;
    private String soPhong;
    private int tang;
    private String tenLoai;
    private BigDecimal giaPhong;
    private Integer sucChua;
    private BigDecimal dienTich;
    private String moTa;
    private String hinhAnh;

    public int getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(int maPhong) {
        this.maPhong = maPhong;
    }

    public int getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(int maLoai) {
        this.maLoai = maLoai;
    }

    public String getSoPhong() {
        return soPhong;
    }

    public void setSoPhong(String soPhong) {
        this.soPhong = soPhong;
    }

    public int getTang() {
        return tang;
    }

    public void setTang(int tang) {
        this.tang = tang;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public BigDecimal getGiaPhong() {
        return giaPhong;
    }

    public void setGiaPhong(BigDecimal giaPhong) {
        this.giaPhong = giaPhong;
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

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
}
