package com.hotel.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "khach_hang")
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_kh")
    private Integer maKh;

    @Column(name = "ma_tk")
    private Integer maTk;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "so_dien_thoai", nullable = false, length = 15)
    private String soDienThoai;

    @Column(name = "cccd", length = 20)
    private String cccd;

    @Column(name = "dia_chi", length = 255)
    private String diaChi;

    @Column(name = "ngay_tao")
    private Timestamp ngayTao;

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL)
    private List<DatPhong> datPhongs;

    // Getters and Setters
    public Integer getMaKh() { return maKh; }
    public void setMaKh(Integer maKh) { this.maKh = maKh; }

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

    public List<DatPhong> getDatPhongs() { return datPhongs; }
    public void setDatPhongs(List<DatPhong> datPhongs) { this.datPhongs = datPhongs; }
}
