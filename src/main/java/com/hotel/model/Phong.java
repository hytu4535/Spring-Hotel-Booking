package com.hotel.model;


import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "phong")
@Getter
@Setter
public class Phong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_phong")
    private Integer maPhong;

    @Column(name = "so_phong", nullable = false, length = 10)
    private String soPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_loai", nullable = false)
    private LoaiPhong maLoaiPhong;

    @Column(name = "tang")
    private Integer tang;


    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "da_xoa", nullable = false)
    private Boolean daXoa = false;

    public LoaiPhong getLoaiPhong() {
        return this.maLoaiPhong;
    }
}