package com.hotel.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "dat_phong")
public class DatPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dp")
    private Integer maDp;

    @Column(name = "ma_tra_cuu", nullable = false, unique = true, length = 20)
    private String maTraCuu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_kh", nullable = false)
    private KhachHang khachHang;

    @Column(name = "ma_nv_xu_ly")
    private Integer maNvXuLy;

    @Column(name = "ngay_dat")
    private Timestamp ngayDat;

    @Column(name = "ngay_nhan_phong", nullable = false)
    private Date ngayNhanPhong;

    @Column(name = "ngay_tra_phong", nullable = false)
    private Date ngayTraPhong;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai;

    @Column(name = "tien_phong", nullable = false, precision = 12, scale = 2)
    private BigDecimal tienPhong;

    @Column(name = "tien_dich_vu", nullable = false, precision = 12, scale = 2)
    private BigDecimal tienDichVu;

    @Column(name = "tong_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nv_xu_ly", insertable = false, updatable = false)
    private TaiKhoan nhanVienXuLy;

    @OneToMany(mappedBy = "datPhong", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDatPhong> chiTietDatPhongs;

    @OneToMany(mappedBy = "datPhong", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThanhToan> thanhToans;

    // Backward compatibility helper for JDBC DAOs
    public int getMaKh() {
        return khachHang != null ? khachHang.getMaKh() : 0;
    }
    public void setMaKh(int maKh) {
        if (this.khachHang == null) {
            this.khachHang = new KhachHang();
        }
        this.khachHang.setMaKh(maKh);
    }

    // Getters and Setters
    public Integer getMaDp() { return maDp; }
    public void setMaDp(Integer maDp) { this.maDp = maDp; }

    public String getMaTraCuu() { return maTraCuu; }
    public void setMaTraCuu(String maTraCuu) { this.maTraCuu = maTraCuu; }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public Integer getMaNvXuLy() { return maNvXuLy; }
    public void setMaNvXuLy(Integer maNvXuLy) { this.maNvXuLy = maNvXuLy; }

    public Timestamp getNgayDat() { return ngayDat; }
    public void setNgayDat(Timestamp ngayDat) { this.ngayDat = ngayDat; }

    public Date getNgayNhanPhong() { return ngayNhanPhong; }
    public void setNgayNhanPhong(Date ngayNhanPhong) { this.ngayNhanPhong = ngayNhanPhong; }

    public Date getNgayTraPhong() { return ngayTraPhong; }
    public void setNgayTraPhong(Date ngayTraPhong) { this.ngayTraPhong = ngayTraPhong; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public BigDecimal getTienPhong() { return tienPhong; }
    public void setTienPhong(BigDecimal tienPhong) { this.tienPhong = tienPhong; }

    public BigDecimal getTienDichVu() { return tienDichVu; }
    public void setTienDichVu(BigDecimal tienDichVu) { this.tienDichVu = tienDichVu; }

    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public TaiKhoan getNhanVienXuLy() { return nhanVienXuLy; }
    public void setNhanVienXuLy(TaiKhoan nhanVienXuLy) { this.nhanVienXuLy = nhanVienXuLy; }

    public List<ChiTietDatPhong> getChiTietDatPhongs() { return chiTietDatPhongs; }
    public void setChiTietDatPhongs(List<ChiTietDatPhong> chiTietDatPhongs) { this.chiTietDatPhongs = chiTietDatPhongs; }

    public List<ThanhToan> getThanhToans() { return thanhToans; }
    public void setThanhToans(List<ThanhToan> thanhToans) { this.thanhToans = thanhToans; }
}
