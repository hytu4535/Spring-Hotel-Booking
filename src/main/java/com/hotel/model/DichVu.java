package com.hotel.model;

import java.math.BigDecimal;

public class DichVu {
    private int maDv;
    private String tenDv;
    private BigDecimal gia;
    private String donViTinh;
    private String moTa;
    private int trangThai; // 1: Hoạt động, 0: Ngừng hoạt động

    public int getMaDv() { return maDv; }
    public void setMaDv(int maDv) { this.maDv = maDv; }
    public String getTenDv() { return tenDv; }
    public void setTenDv(String tenDv) { this.tenDv = tenDv; }
    public BigDecimal getGia() { return gia; }
    public void setGia(BigDecimal gia) { this.gia = gia; }
    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }
}
