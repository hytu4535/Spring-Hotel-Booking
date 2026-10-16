package com.hotel.model;

public class PhongSoDoDTO {
    private int maPhong;
    private String soPhong;
    private int tang;
    private String trangThai; // TRONG, DA_DAT, DANG_O, BAO_TRI
    private String tenLoaiPhong;
    
    // Dữ liệu nếu phòng đang có đơn
    private Integer maDpHienTai;
    private String tenKhachHang;

    // Getters and Setters
    public int getMaPhong() { return maPhong; }
    public void setMaPhong(int maPhong) { this.maPhong = maPhong; }

    public String getSoPhong() { return soPhong; }
    public void setSoPhong(String soPhong) { this.soPhong = soPhong; }

    public int getTang() { return tang; }
    public void setTang(int tang) { this.tang = tang; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getTenLoaiPhong() { return tenLoaiPhong; }
    public void setTenLoaiPhong(String tenLoaiPhong) { this.tenLoaiPhong = tenLoaiPhong; }

    public Integer getMaDpHienTai() { return maDpHienTai; }
    public void setMaDpHienTai(Integer maDpHienTai) { this.maDpHienTai = maDpHienTai; }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
}