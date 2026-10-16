package com.hotel.model;

public class DatPhongDTO extends DatPhong {
    private String tenKhachHang;
    private String emailKhachHang;
    private String sdtKhachHang;
    private String tenNhanVienXuLy;

    // Getters and Setters
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public String getEmailKhachHang() { return emailKhachHang; }
    public void setEmailKhachHang(String emailKhachHang) { this.emailKhachHang = emailKhachHang; }

    public String getSdtKhachHang() { return sdtKhachHang; }
    public void setSdtKhachHang(String sdtKhachHang) { this.sdtKhachHang = sdtKhachHang; }

    public String getTenNhanVienXuLy() { return tenNhanVienXuLy; }
    public void setTenNhanVienXuLy(String tenNhanVienXuLy) { this.tenNhanVienXuLy = tenNhanVienXuLy; }
}