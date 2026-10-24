package com.hotel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "khach_hang")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_kh")
    private int maKh;
    @Column(name = "ma_tk")
    private Integer maTk;
    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;
    @Column(name = "email", nullable = false, length = 100)
    private String email;
    @Column(name = "so_dien_thoai", length = 15)
    private String soDienThoai;
    @Column(name = "cccd", length = 20)
    private String cccd;
    @Column(name = "dia_chi", length = 255)
    private String diaChi;
    @Column(name = "ngay_tao")
    private Timestamp ngayTao;

    // Getters and Setters
    public int getMaKh() { return maKh; }
    public void setMaKh(int maKh) { this.maKh = maKh; }
    public Integer getMaTk() { return maTk; }
    public void setMaTk(Integer maTk) { this.maTk = maTk; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }
}