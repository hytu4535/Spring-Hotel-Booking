package com.hotel.controller.guest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.model.CartItem;
import com.hotel.model.GioHang;
import com.hotel.util.DateUtil;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class GioHangServlet {

    @GetMapping("/gio-hang")
    public String doGet(@RequestParam(required = false) String action,
                        @RequestParam(required = false) Integer maPhong,
                        HttpSession session) {
        GioHang cart = (GioHang) session.getAttribute("cart");

        if (cart == null) {
            cart = new GioHang();
            session.setAttribute("cart", cart);
        }

        if ("remove".equals(action)) {
            cart.removePhong(maPhong);
        }

        return "guest/gio-hang";
    }

    @PostMapping("/gio-hang")
    public String doPost(@RequestParam String ngayNhan, @RequestParam String ngayTra,
                         @RequestParam int maPhong, @RequestParam String soPhong,
                         @RequestParam String tenLoai, @RequestParam BigDecimal giaPhong,
                         HttpSession session) {
        // Hành động thêm phòng vào giỏ (từ trang Tìm Phòng của Nguyễn Ý)
        GioHang cart = (GioHang) session.getAttribute("cart");

        if (cart == null) {
            cart = new GioHang();
            session.setAttribute("cart", cart);
        }

        // Khởi tạo ngày nhận/trả cho Giỏ nếu chưa có
        if (cart.getNgayNhan() == null) {
            cart.setNgayNhan(ngayNhan);
            cart.setNgayTra(ngayTra);
            LocalDate d1 = DateUtil.parse(ngayNhan);
            LocalDate d2 = DateUtil.parse(ngayTra);
            cart.setSoDem(DateUtil.soDem(d1, d2));
        }

        // Lấy thông tin phòng vừa click thêm
        CartItem item = new CartItem();
        item.setMaPhong(maPhong);
        item.setSoPhong(soPhong);
        item.setTenLoai(tenLoai);
        item.setGiaPhong(giaPhong);
        item.setSoDem(cart.getSoDem());
        item.setThanhTien(item.getGiaPhong().multiply(new BigDecimal(cart.getSoDem())));

        cart.addPhong(item);

        // Quay về trang giỏ hàng xem
        return "redirect:/gio-hang";
    }
}