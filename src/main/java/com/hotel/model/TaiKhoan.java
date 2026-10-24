package com.hotel.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tai_khoan")
public class TaiKhoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tk")
    private int maTk;
    @Column(name = "ten_dang_nhap", length = 100)
    private String tenDangNhap;
    @Column(name = "mat_khau", length = 255)
    private String matKhau;
    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;
    @Column(name = "email", nullable = false, length = 100)
    private String email;
    @Column(name = "so_dien_thoai", length = 15)
    private String soDienThoai;
    @Column(name = "vai_tro", nullable = false, length = 20)
    private String vaiTro;
    @Column(name = "ngay_tao")
    private Timestamp ngayTao;
    @Column(name = "trang_thai", nullable = false)
    private int trangThai;
    @Column(name = "provider", length = 20)
    private String provider;
    @Column(name = "provider_id", length = 100)
    private String providerId;
    @Column(name = "anh_dai_dien", length = 255)
    private String anhDaiDien;

    public TaiKhoan() {
    }

    public int getMaTk() {
        return maTk;
    }

    public void setMaTk(int maTk) {
        this.maTk = maTk;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getAnhDaiDien() { return anhDaiDien; }
    public void setAnhDaiDien(String anhDaiDien) { this.anhDaiDien = anhDaiDien; }
}
