package com.hotel.dto;

public class KhachHangAdminDTO {
    private int maKh;
    private Integer maTk;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String cccd;
    private String diaChi;
    private long soDonDatPhong;

    public int getMaKh() { return maKh; }
    public void setMaKh(int maKh) { this.maKh = maKh; }
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
    public long getSoDonDatPhong() { return soDonDatPhong; }
    public void setSoDonDatPhong(long soDonDatPhong) { this.soDonDatPhong = soDonDatPhong; }
}
