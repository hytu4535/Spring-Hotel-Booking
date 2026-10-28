package com.hotel.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "loai_phong")
@Getter
@Setter
public class LoaiPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_loai")
    private Integer maLoai;

    @Column(name = "ten_loai", nullable = false, length = 100)
    private String tenLoai;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "gia_mac_dinh", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaMacDinh;

    @Column(name = "suc_chua", nullable = false)
    private Integer sucChua;

    @Column(name = "dien_tich", precision = 6, scale = 2)
    private BigDecimal dienTich;

    @Column(name = "hinh_anh", length = 255)
    private String hinhAnh;

    @Column(name = "da_xoa", nullable = false)
    private Boolean daXoa = false;

    @OneToMany(mappedBy = "maLoaiPhong", fetch = FetchType.LAZY)
    private List<Phong> dsPhong;
}