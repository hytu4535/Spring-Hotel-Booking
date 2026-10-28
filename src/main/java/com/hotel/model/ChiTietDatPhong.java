package com.hotel.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_dat_phong")
public class ChiTietDatPhong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ctdp")
    private int maCtdp;

    @Column(name = "ma_dp", nullable = false)
    private int maDp;

    @Column(name = "ma_phong", nullable = false)
    private int maPhong;

    @Column(name = "gia_phong", nullable = false)
    private BigDecimal giaPhong;

    @Column(name = "so_dem", nullable = false)
    private int soDem;

    @Column(name = "thanh_tien", nullable = false)
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