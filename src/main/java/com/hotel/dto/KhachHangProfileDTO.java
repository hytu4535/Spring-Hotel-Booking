package com.hotel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class KhachHangProfileDTO {
    @NotBlank(message = "Họ tên là bắt buộc.")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự.")
    private String hoTen;
    @NotBlank(message = "Email là bắt buộc.")
    @Email(message = "Email không đúng định dạng.")
    @Size(max = 100, message = "Email tối đa 100 ký tự.")
    private String email;
    @Pattern(regexp = "^$|^0\\d{9}$", message = "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.")
    private String soDienThoai;
    @Pattern(regexp = "^$|^\\d{9,12}$", message = "CCCD phải gồm 9 hoặc 12 chữ số.")
    private String cccd;
    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự.")
    private String diaChi;

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
}
