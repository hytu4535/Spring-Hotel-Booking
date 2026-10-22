package com.hotel.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_dat_phong")
public class ChiTietDatPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ctdp")
    private Integer maCtdp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dp", nullable = false)
    private DatPhong datPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phong", nullable = false)
    private Phong phong;

    @Column(name = "gia_phong", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaPhong;

    @Column(name = "so_dem", nullable = false)
    private Integer soDem;

    @Column(name = "thanh_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal thanhTien;

    // Backward compatibility helper for JDBC DAOs
    public int getMaPhong() {
        return phong != null ? phong.getMaPhong() : 0;
    }
    public void setMaPhong(int maPhong) {
        if (this.phong == null) {
            this.phong = new Phong();
        }
        this.phong.setMaPhong(maPhong);
    }

    // Getters and Setters
    public Integer getMaCtdp() { return maCtdp; }
    public void setMaCtdp(Integer maCtdp) { this.maCtdp = maCtdp; }

    public DatPhong getDatPhong() { return datPhong; }
    public void setDatPhong(DatPhong datPhong) { this.datPhong = datPhong; }

    public Phong getPhong() { return phong; }
    public void setPhong(Phong phong) { this.phong = phong; }

    public BigDecimal getGiaPhong() { return giaPhong; }
    public void setGiaPhong(BigDecimal giaPhong) { this.giaPhong = giaPhong; }

    public Integer getSoDem() { return soDem; }
    public void setSoDem(Integer soDem) { this.soDem = soDem; }

    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
}
