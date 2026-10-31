package com.hotel.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "danh_gia")
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dg")
    private Integer maDg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dp", nullable = false)
    private DatPhong datPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_kh", nullable = false)
    private KhachHang khachHang;

    @Column(name = "so_sao", nullable = false)
    private Integer soSao;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "ngay_danh_gia")
    private Timestamp ngayDanhGia;

    // Getters and Setters
    public Integer getMaDg() { return maDg; }
    public void setMaDg(Integer maDg) { this.maDg = maDg; }

    public DatPhong getDatPhong() { return datPhong; }
    public void setDatPhong(DatPhong datPhong) { this.datPhong = datPhong; }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public Integer getSoSao() { return soSao; }
    public void setSoSao(Integer soSao) { this.soSao = soSao; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public Timestamp getNgayDanhGia() { return ngayDanhGia; }
    public void setNgayDanhGia(Timestamp ngayDanhGia) { this.ngayDanhGia = ngayDanhGia; }
}
