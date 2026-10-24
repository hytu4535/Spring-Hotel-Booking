package com.hotel.model;

import java.math.BigDecimal;
import java.sql.Date;

public class DatDichVu {
    private int maDatDv;
    private int maDp;
    private int maDv;
    private String tenDv;
    private int soLuong;
    private BigDecimal giaTaiThoiDiem;
    private Date ngaySuDung;
    private BigDecimal thanhTien;
    private Integer maNvGhiNhan;
    private String tenNhanVien;

    public int getMaDatDv() { return maDatDv; }
    public void setMaDatDv(int maDatDv) { this.maDatDv = maDatDv; }
    public int getMaDp() { return maDp; }
    public void setMaDp(int maDp) { this.maDp = maDp; }
    public int getMaDv() { return maDv; }
    public void setMaDv(int maDv) { this.maDv = maDv; }
    public String getTenDv() { return tenDv; }
    public void setTenDv(String tenDv) { this.tenDv = tenDv; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public BigDecimal getGiaTaiThoiDiem() { return giaTaiThoiDiem; }
    public void setGiaTaiThoiDiem(BigDecimal giaTaiThoiDiem) { this.giaTaiThoiDiem = giaTaiThoiDiem; }
    public Date getNgaySuDung() { return ngaySuDung; }
    public void setNgaySuDung(Date ngaySuDung) { this.ngaySuDung = ngaySuDung; }
    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
    public Integer getMaNvGhiNhan() { return maNvGhiNhan; }
    public void setMaNvGhiNhan(Integer maNvGhiNhan) { this.maNvGhiNhan = maNvGhiNhan; }
    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
}
