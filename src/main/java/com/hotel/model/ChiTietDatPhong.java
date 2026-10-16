package com.hotel.model;

import java.math.BigDecimal;

public class ChiTietDatPhong {
    private int maCtdp;
    private int maDp;
    private int maPhong;
    private BigDecimal giaPhong;
    private int soDem;
    private BigDecimal thanhTien;

    // Getters and Setters
    public int getMaCtdp() { return maCtdp; }
    public void setMaCtdp(int maCtdp) { this.maCtdp = maCtdp; }
    public int getMaDp() { return maDp; }
    public void setMaDp(int maDp) { this.maDp = maDp; }
    public int getMaPhong() { return maPhong; }
    public void setMaPhong(int maPhong) { this.maPhong = maPhong; }
    public BigDecimal getGiaPhong() { return giaPhong; }
    public void setGiaPhong(BigDecimal giaPhong) { this.giaPhong = giaPhong; }
    public int getSoDem() { return soDem; }
    public void setSoDem(int soDem) { this.soDem = soDem; }
    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
}