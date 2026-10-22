package com.hotel.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "thanh_toan")
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tt")
    private Integer maTt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dp", nullable = false)
    private DatPhong datPhong;

    @Column(name = "so_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal soTien;

    @Column(name = "ngay_thanh_toan")
    private Timestamp ngayThanhToan;

    @Column(name = "phuong_thuc", nullable = false, length = 30)
    private String phuongThuc; // TIEN_MAT, CHUYEN_KHOAN, VNPAY, MOMO

    @Column(name = "ma_giao_dich", length = 100)
    private String maGiaoDich;

    @Column(name = "loai_thanh_toan", nullable = false, length = 30)
    private String loaiThanhToan; // DAT_COC, THANH_TOAN_DU, HOAN_TIEN

    @Column(name = "ma_nv_thu")
    private Integer maNvThu;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    // Getters and Setters
    public Integer getMaTt() { return maTt; }
    public void setMaTt(Integer maTt) { this.maTt = maTt; }

    public DatPhong getDatPhong() { return datPhong; }
    public void setDatPhong(DatPhong datPhong) { this.datPhong = datPhong; }

    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }

    public Timestamp getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(Timestamp ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }

    public String getPhuongThuc() { return phuongThuc; }
    public void setPhuongThuc(String phuongThuc) { this.phuongThuc = phuongThuc; }

    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }

    public String getLoaiThanhToan() { return loaiThanhToan; }
    public void setLoaiThanhToan(String loaiThanhToan) { this.loaiThanhToan = loaiThanhToan; }

    public Integer getMaNvThu() { return maNvThu; }
    public void setMaNvThu(Integer maNvThu) { this.maNvThu = maNvThu; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
