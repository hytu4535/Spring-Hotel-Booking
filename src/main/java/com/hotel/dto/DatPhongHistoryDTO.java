package com.hotel.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.hotel.model.ThanhToan;

public class DatPhongHistoryDTO {
    private int maDp;
    private String maTraCuu;
    private Timestamp ngayDat;
    private Date ngayNhanPhong;
    private Date ngayTraPhong;
    private String trangThai;
    private BigDecimal tongTien;
    private BigDecimal tienPhong;
    private BigDecimal tienDichVu;
    private List<String> soPhong = new ArrayList<>();
    private List<ThanhToanHistoryDTO> lichSuThanhToan = new ArrayList<>();

    public int getMaDp() {
        return maDp;
    }

    public void setMaDp(int maDp) {
        this.maDp = maDp;
    }

    public String getMaTraCuu() {
        return maTraCuu;
    }

    public void setMaTraCuu(String maTraCuu) {
        this.maTraCuu = maTraCuu;
    }

    public Timestamp getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(Timestamp ngayDat) {
        this.ngayDat = ngayDat;
    }

    public Date getNgayNhanPhong() {
        return ngayNhanPhong;
    }

    public void setNgayNhanPhong(Date ngayNhanPhong) {
        this.ngayNhanPhong = ngayNhanPhong;
    }

    public Date getNgayTraPhong() {
        return ngayTraPhong;
    }

    public void setNgayTraPhong(Date ngayTraPhong) {
        this.ngayTraPhong = ngayTraPhong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public BigDecimal getTienPhong() {
        return tienPhong;
    }

    public void setTienPhong(BigDecimal tienPhong) {
        this.tienPhong = tienPhong;
    }

    public BigDecimal getTienDichVu() {
        return tienDichVu;
    }

    public void setTienDichVu(BigDecimal tienDichVu) {
        this.tienDichVu = tienDichVu;
    }

    public List<String> getSoPhong() {
        return soPhong;
    }

    public void setSoPhong(List<String> soPhong) {
        this.soPhong = soPhong;
    }

    public List<ThanhToanHistoryDTO> getLichSuThanhToan() {
        return lichSuThanhToan;
    }

    public void setLichSuThanhToan(List<ThanhToanHistoryDTO> lichSuThanhToan) {
        this.lichSuThanhToan = lichSuThanhToan;
    }
}
