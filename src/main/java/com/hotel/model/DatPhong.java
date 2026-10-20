package com.hotel.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "dat_phong")
public class DatPhong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dp")
    private int maDp;
    @Column(name = "ma_tra_cuu", nullable = false, length = 20)
    private String maTraCuu;
    @Column(name = "ma_kh", nullable = false)
    private int maKh;
    @Column(name = "ma_nv_xu_ly")
    private Integer maNvXuLy;
    @Column(name = "ngay_dat")
    private Timestamp ngayDat;
    @Column(name = "ngay_nhan_phong", nullable = false)
    private Date ngayNhanPhong;
    @Column(name = "ngay_tra_phong", nullable = false)
    private Date ngayTraPhong;
    @Column(name = "trang_thai", nullable = false, length = 20)
    private String trangThai;
    @Column(name = "tien_phong", nullable = false)
    private BigDecimal tienPhong;
    @Column(name = "tien_dich_vu", nullable = false)
    private BigDecimal tienDichVu;
    @Column(name = "tong_tien", nullable = false)
    private BigDecimal tongTien;
    @Column(name = "ghi_chu", length = 255)
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