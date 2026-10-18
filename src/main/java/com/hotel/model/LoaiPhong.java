package com.hotel.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "loai_phong")
public class LoaiPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_loai")
    private Integer maLoai;

    @Column(name = "ten_loai", nullable = false, length = 100)
    private String tenLoai;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "gia_mac_dinh", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaMacDinh;

    @Column(name = "suc_chua", nullable = false)
    private Integer sucChua;

    @Column(name = "dien_tich", precision = 6, scale = 2)
    private BigDecimal dienTich;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    public Integer getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(Integer maLoai) {
        this.maLoai = maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public BigDecimal getGiaMacDinh() {
        return giaMacDinh;
    }

    public void setGiaMacDinh(BigDecimal giaMacDinh) {
        this.giaMacDinh = giaMacDinh;
    }

    public Integer getSucChua() {
        return sucChua;
    }

    public void setSucChua(Integer sucChua) {
        this.sucChua = sucChua;
    }

    public BigDecimal getDienTich() {
        return dienTich;
    }

    public void setDienTich(BigDecimal dienTich) {
        this.dienTich = dienTich;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
}