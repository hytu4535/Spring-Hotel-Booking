package com.hotel.model;
import javax.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="dich_vu")
public class DichVu {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="ma_dv") private Integer maDv;
 @Column(name="ten_dv",nullable=false) private String tenDv;
 @Column(name="don_gia",nullable=false) private BigDecimal donGia;
 @Column(name="don_vi_tinh") private String donViTinh;
 @Column(name="mo_ta") private String moTa;
 @Column(name="trang_thai",nullable=false) private int trangThai=1;
 public Integer getMaDv(){return maDv;} public void setMaDv(Integer v){maDv=v;}
 public String getTenDv(){return tenDv;} public void setTenDv(String v){tenDv=v;}
 public BigDecimal getDonGia(){return donGia;} public void setDonGia(BigDecimal v){donGia=v;}
 public String getDonViTinh(){return donViTinh;} public void setDonViTinh(String v){donViTinh=v;}
 public String getMoTa(){return moTa;} public void setMoTa(String v){moTa=v;}
 public int getTrangThai(){return trangThai;} public void setTrangThai(int v){trangThai=v;}
}