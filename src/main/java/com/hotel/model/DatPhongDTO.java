package com.hotel.model;

import java.util.List;

public class DatPhongDTO extends DatPhong {
    private String tenKhachHang;
    private String emailKhachHang;
    private String sdtKhachHang;
    private String tenNhanVienXuLy;
    private List<ChiTietDatPhong> danhSachChiTiet;
    private List<DatDichVu> danhSachDichVu;
    private List<ThanhToan> danhSachThanhToan;

    // Getters and Setters
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public String getEmailKhachHang() { return emailKhachHang; }
    public void setEmailKhachHang(String emailKhachHang) { this.emailKhachHang = emailKhachHang; }

    public String getSdtKhachHang() { return sdtKhachHang; }
    public void setSdtKhachHang(String sdtKhachHang) { this.sdtKhachHang = sdtKhachHang; }

    public String getTenNhanVienXuLy() { return tenNhanVienXuLy; }
    public void setTenNhanVienXuLy(String tenNhanVienXuLy) { this.tenNhanVienXuLy = tenNhanVienXuLy; }

    public List<ChiTietDatPhong> getDanhSachChiTiet() { return danhSachChiTiet; }
    public void setDanhSachChiTiet(List<ChiTietDatPhong> danhSachChiTiet) { this.danhSachChiTiet = danhSachChiTiet; }

    public List<DatDichVu> getDanhSachDichVu() { return danhSachDichVu; }
    public void setDanhSachDichVu(List<DatDichVu> danhSachDichVu) { this.danhSachDichVu = danhSachDichVu; }

    public List<ThanhToan> getDanhSachThanhToan() { return danhSachThanhToan; }
    public void setDanhSachThanhToan(List<ThanhToan> danhSachThanhToan) { this.danhSachThanhToan = danhSachThanhToan; }
}
