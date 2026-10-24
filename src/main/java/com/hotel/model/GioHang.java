package com.hotel.model;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GioHang {
    private String ngayNhan;
    private String ngayTra;
    private int soDem;
    private List<CartItem> items = new ArrayList<>();
    private List<CartServiceItem> services = new ArrayList<>();

    public BigDecimal getTienPhong() {
        BigDecimal tong = BigDecimal.ZERO;
        for (CartItem item : items) {
            tong = tong.add(item.getThanhTien());
        }
        return tong;
    }

    public BigDecimal getTienDichVu() {
        BigDecimal tong = BigDecimal.ZERO;
        for (CartServiceItem s : services) {
            tong = tong.add(s.getThanhTien());
        }
        return tong;
    }

    public BigDecimal getTongTien() {
        return getTienPhong().add(getTienDichVu());
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

    // Thêm dịch vụ phát sinh vào giỏ
    public void addService(CartServiceItem newService) {
        for (CartServiceItem s : services) {
            if (s.getMaDv() == newService.getMaDv()) {
                s.setSoLuong(s.getSoLuong() + newService.getSoLuong());
                return;
            }
        }
        services.add(newService);
    }

    // Xóa dịch vụ phát sinh khỏi giỏ
    public void removeService(int maDv) {
        services.removeIf(s -> s.getMaDv() == maDv);
    }

    // Getters and Setters
    public String getNgayNhan() { return ngayNhan; }
    public void setNgayNhan(String ngayNhan) { this.ngayNhan = ngayNhan; }
    public String getNgayTra() { return ngayTra; }
    public void setNgayTra(String ngayTra) { this.ngayTra = ngayTra; }
    public int getSoDem() { return soDem; }
    public void setSoDem(int soDem) { this.soDem = soDem; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public List<CartServiceItem> getServices() { return services; }
    public void setServices(List<CartServiceItem> services) { this.services = services; }
}
