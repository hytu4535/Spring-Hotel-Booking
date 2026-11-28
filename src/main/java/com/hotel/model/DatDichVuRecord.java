package com.hotel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "dat_dich_vu")
public class DatDichVuRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ddv")
    private Integer maDatDv;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_dp", nullable = false)
    private DatPhong datPhong;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_dv", nullable = false)
    private DichVu dichVu;

    @Column(name = "so_luong", nullable = false)
    private int soLuong;

    @Column(name = "don_gia", nullable = false, precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "ngay_su_dung")
    private Date ngaySuDung;

    @Column(name = "thanh_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal thanhTien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nv_ghi_nhan")
    private TaiKhoan nhanVienGhiNhan;

    public Integer getMaDatDv() {
        return maDatDv;
    }

    public DatPhong getDatPhong() {
        return datPhong;
    }

    public void setDatPhong(DatPhong datPhong) {
        this.datPhong = datPhong;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public Date getNgaySuDung() {
        return ngaySuDung;
    }

    public void setNgaySuDung(Date ngaySuDung) {
        this.ngaySuDung = ngaySuDung;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    public TaiKhoan getNhanVienGhiNhan() {
        return nhanVienGhiNhan;
    }

    public void setNhanVienGhiNhan(TaiKhoan nhanVienGhiNhan) {
        this.nhanVienGhiNhan = nhanVienGhiNhan;
    }
}
