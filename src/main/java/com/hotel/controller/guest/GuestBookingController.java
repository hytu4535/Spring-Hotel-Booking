package com.hotel.controller.guest;

import com.hotel.model.CartItem;
import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.DatPhong;
import com.hotel.model.GioHang;
import com.hotel.model.KhachHang;
import com.hotel.model.TaiKhoan;
import com.hotel.service.DatPhongService;
import com.hotel.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GuestBookingController {

    private final DatPhongService datPhongService;

    public GuestBookingController(DatPhongService datPhongService) {
        this.datPhongService = datPhongService;
    }

    // ================= XỬ LÝ GIỎ HÀNG =================
    @GetMapping("/gio-hang")
    public String viewCart(HttpSession session, @RequestParam(required = false) String action,
            @RequestParam(required = false) Integer maPhong) {
        GioHang cart = getCart(session);
        if ("remove".equals(action) && maPhong != null) {
            cart.removePhong(maPhong);
        }
        return "guest/gio-hang";
    }

    @PostMapping("/gio-hang")
    public String addToCart(HttpSession session, @RequestParam int maPhong, @RequestParam String soPhong,
            @RequestParam String tenLoai, @RequestParam BigDecimal giaPhong,
            @RequestParam String ngayNhan, @RequestParam String ngayTra) {
        GioHang cart = getCart(session);

        if (cart.getNgayNhan() == null) {
            cart.setNgayNhan(ngayNhan);
            cart.setNgayTra(ngayTra);
            cart.setSoDem(DateUtil.soDem(DateUtil.parse(ngayNhan), DateUtil.parse(ngayTra)));
        }

        CartItem item = new CartItem();
        item.setMaPhong(maPhong);
        item.setSoPhong(soPhong);
        item.setTenLoai(tenLoai);
        item.setGiaPhong(giaPhong);
        item.setSoDem(cart.getSoDem());
        item.setThanhTien(giaPhong.multiply(new BigDecimal(cart.getSoDem())));

        cart.addPhong(item);
        return "redirect:/gio-hang";
    }

    // ================= XỬ LÝ ĐẶT PHÒNG =================
    @GetMapping("/dat-phong")
    public String showCheckoutForm(HttpSession session, Model model) {
        GioHang cart = getCart(session);
        if (cart.getItems().isEmpty()) {
            return "redirect:/tim-phong";
        }

        // Nếu là Customer đã login, tự điền thông tin
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser != null && "CUSTOMER".equals(currentUser.getVaiTro())) {
            model.addAttribute("hoTen", currentUser.getHoTen());
            model.addAttribute("email", currentUser.getEmail());
            model.addAttribute("soDienThoai", currentUser.getSoDienThoai());
        }
        return "guest/dat-phong";
    }

    @PostMapping("/dat-phong")
    public String submitBooking(HttpSession session,
            @ModelAttribute KhachHang khachHang,
            RedirectAttributes redirectAttributes) {
        GioHang cart = getCart(session);
        if (cart.getItems().isEmpty())
            return "redirect:/tim-phong";

        try {
            DatPhong dp = new DatPhong();
            dp.setNgayNhanPhong(java.sql.Date.valueOf(cart.getNgayNhan()));
            dp.setNgayTraPhong(java.sql.Date.valueOf(cart.getNgayTra()));
            dp.setTienPhong(cart.getTongTien());

            List<ChiTietDatPhong> ctList = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                ChiTietDatPhong ct = new ChiTietDatPhong();
                ct.setMaPhong(item.getMaPhong());
                ct.setGiaPhong(item.getGiaPhong());
                ct.setSoDem(item.getSoDem());
                ct.setThanhTien(item.getThanhTien());
                ctList.add(ct);
            }

            // Transaction + Kafka
            String maTraCuu = datPhongService.taoDonDatPhong(khachHang, dp, ctList);

            session.removeAttribute("cart");
            session.setAttribute("maTraCuu", maTraCuu);
            session.setAttribute("emailDatPhong", khachHang.getEmail());

            return "redirect:/dat-phong-thanh-cong";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/dat-phong";
        }
    }

    @GetMapping("/dat-phong-thanh-cong")
    public String bookingSuccess(HttpSession session) {
        if (session.getAttribute("maTraCuu") == null)
            return "redirect:/";
        return "guest/dat-phong-thanh-cong";
    }

    private GioHang getCart(HttpSession session) {
        GioHang cart = (GioHang) session.getAttribute("cart");
        if (cart == null) {
            cart = new GioHang();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}