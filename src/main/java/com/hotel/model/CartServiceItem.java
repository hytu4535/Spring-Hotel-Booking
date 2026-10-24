package com.hotel.model;

import java.math.BigDecimal;

public class CartServiceItem {
    private int maDv;
    private String tenDv;
    private BigDecimal gia;
    private int soLuong;
    private BigDecimal thanhTien;

    public CartServiceItem() {}

    public CartServiceItem(int maDv, String tenDv, BigDecimal gia, int soLuong) {
        this.maDv = maDv;
        this.tenDv = tenDv;
        this.gia = gia;
        this.soLuong = soLuong;
        this.thanhTien = gia.multiply(BigDecimal.valueOf(soLuong));
    }

    public int getMaDv() { return maDv; }
    public void setMaDv(int maDv) { this.maDv = maDv; }

    public String getTenDv() { return tenDv; }
    public void setTenDv(String tenDv) { this.tenDv = tenDv; }

    public BigDecimal getGia() { return gia; }
    public void setGia(BigDecimal gia) { this.gia = gia; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        if (this.gia != null) {
            this.thanhTien = this.gia.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
}
