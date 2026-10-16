package com.hotel.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class DatPhong {
    private int maDp;
    private String maTraCuu;
    private int maKh;
    private Integer maNvXuLy;
    private Timestamp ngayDat;
    private Date ngayNhanPhong;
    private Date ngayTraPhong;
    private String trangThai;
    private BigDecimal tienPhong;
    private BigDecimal tienDichVu;
    private BigDecimal tongTien;
    private String ghiChu;

    // Getters and Setters
    public int getMaDp() { return maDp; }
    public void setMaDp(int maDp) { this.maDp = maDp; }
    public String getMaTraCuu() { return maTraCuu; }
    public void setMaTraCuu(String maTraCuu) { this.maTraCuu = maTraCuu; }
    public int getMaKh() { return maKh; }
    public void setMaKh(int maKh) { this.maKh = maKh; }
    public Integer getMaNvXuLy() { return maNvXuLy; }
    public void setMaNvXuLy(Integer maNvXuLy) { this.maNvXuLy = maNvXuLy; }
    public Timestamp getNgayDat() { return ngayDat; }
    public void setNgayDat(Timestamp ngayDat) { this.ngayDat = ngayDat; }
    public Date getNgayNhanPhong() { return ngayNhanPhong; }
    public void setNgayNhanPhong(Date ngayNhanPhong) { this.ngayNhanPhong = ngayNhanPhong; }
    public Date getNgayTraPhong() { return ngayTraPhong; }
    public void setNgayTraPhong(Date ngayTraPhong) { this.ngayTraPhong = ngayTraPhong; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public BigDecimal getTienPhong() { return tienPhong; }
    public void setTienPhong(BigDecimal tienPhong) { this.tienPhong = tienPhong; }
    public BigDecimal getTienDichVu() { return tienDichVu; }
    public void setTienDichVu(BigDecimal tienDichVu) { this.tienDichVu = tienDichVu; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}