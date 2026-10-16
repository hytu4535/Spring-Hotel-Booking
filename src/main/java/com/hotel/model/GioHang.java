package com.hotel.model;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GioHang {
    private String ngayNhan;
    private String ngayTra;
    private int soDem;
    private List<CartItem> items = new ArrayList<>();

    public BigDecimal getTongTien() {
        BigDecimal tong = BigDecimal.ZERO;
        for (CartItem item : items) {
            tong = tong.add(item.getThanhTien());
        }
        return tong;
    }

    // Nút thêm phòng vào giỏ (Tránh thêm trùng phòng)
    public void addPhong(CartItem newItem) {
        for (CartItem item : items) {
            if (item.getMaPhong() == newItem.getMaPhong()) return; // Đã có trong giỏ
        }
        items.add(newItem);
    }
    
    // Nút xóa phòng
    public void removePhong(int maPhong) {
        items.removeIf(item -> item.getMaPhong() == maPhong);
    }

    // Getters and Setters
    public String getNgayNhan() { return ngayNhan; }
    public void setNgayNhan(String ngayNhan) { this.ngayNhan = ngayNhan; }
    public String getNgayTra() { return ngayTra; }
    public void setNgayTra(String ngayTra) { this.ngayTra = ngayTra; }
    public int getSoDem() { return soDem; }
    public void setSoDem(int soDem) { this.soDem = soDem; }
    public List<CartItem> getItems() { return items; }
}