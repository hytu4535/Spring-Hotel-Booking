package com.hotel.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

public class KhachHangAdminForm {
    @NotNull(message = "Vui lòng chọn tên tài khoản.")
    private Integer maTk;

    @Pattern(regexp = "^$|^\\d{9,12}$", message = "CCCD phải gồm 9 hoặc 12 chữ số.")
    private String cccd;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự.")
    private String diaChi;

    public Integer getMaTk() { return maTk; }
    public void setMaTk(Integer maTk) { this.maTk = maTk; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
}
