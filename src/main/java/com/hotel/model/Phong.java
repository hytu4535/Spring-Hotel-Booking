package com.hotel.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import com.hotel.enums.TrangThaiPhong;
import com.hotel.mapper.LoaiPhongMapper;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiPhong trangThaiPhong;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "da_xoa", nullable = false)
    private Boolean daXoa = false;
}