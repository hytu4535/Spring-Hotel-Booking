package com.hotel.controller.guest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.model.CartItem;
import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.DatPhong;
import com.hotel.model.GioHang;
import com.hotel.model.KhachHang;
import com.hotel.service.DatPhongService;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DatPhongServlet {

    private final DatPhongService datPhongService = new DatPhongService();

    @GetMapping("/dat-phong")
    public String doGet(HttpSession session) {
        GioHang cart = (GioHang) session.getAttribute("cart");

        // 1. Nếu giỏ hàng rỗng -> Đá về trang Tìm phòng
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return "redirect:/tim-phong";

        }

        // 2. Nếu có đồ trong giỏ -> Mở trang điền thông tin khách
        return "guest/dat-phong";
    }

    @PostMapping("/dat-phong")
    public String doPost(@RequestParam(required = false) String hoTen,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String soDienThoai,
                         @RequestParam(required = false) String cccd,
                         @RequestParam(required = false) String diaChi,
                         HttpSession session, Model model) {
        GioHang cart = (GioHang) session.getAttribute("cart");

        // Kiểm tra lại lần nữa lúc Submit form lỡ giỏ hàng bị timeout
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return "redirect:/tim-phong";

        }

        try {
            // 1. Lấy thông tin khách hàng từ Form
            KhachHang kh = new KhachHang();
            kh.setHoTen(hoTen);
            kh.setEmail(email);
            kh.setSoDienThoai(soDienThoai);
            kh.setCccd(cccd);
            kh.setDiaChi(diaChi);

            // 2. Map dữ liệu Đặt Phòng từ Giỏ hàng
            DatPhong dp = new DatPhong();
            dp.setNgayNhanPhong(java.sql.Date.valueOf(cart.getNgayNhan()));
            dp.setNgayTraPhong(java.sql.Date.valueOf(cart.getNgayTra()));
            dp.setTienPhong(cart.getTongTien());

            // 3. Map chi tiết các phòng đặt từ Giỏ hàng
            List<ChiTietDatPhong> ctList = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                ChiTietDatPhong ct = new ChiTietDatPhong();
                ct.setMaPhong(item.getMaPhong());
                ct.setGiaPhong(item.getGiaPhong());
                ct.setSoDem(item.getSoDem());
                ct.setThanhTien(item.getThanhTien());
                ctList.add(ct);
            }

            // 4. Gọi Service thực thi Transaction lưu vào DB
            String maTraCuu = datPhongService.taoDonDatPhong(kh, dp, ctList);

            if (maTraCuu != null) {
                // Đặt thành công: Xóa giỏ hàng và chuyển hướng
                session.removeAttribute("cart"); 
                session.setAttribute("maTraCuu", maTraCuu);
                session.setAttribute("emailDatPhong", kh.getEmail());
                
                // (Chỗ này sau này Trung Kiên sẽ đổi sang redirect VNPay)
                return "redirect:/dat-phong-thanh-cong";
            } else {
                // Thất bại do ai đó nhanh tay đặt mất phòng
                model.addAttribute("error", "Một số phòng bạn chọn vừa bị người khác đặt mất. Vui lòng kiểm tra lại sơ đồ phòng!");
                return "guest/dat-phong";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Dữ liệu nhập vào chưa hợp lệ, vui lòng kiểm tra lại.");
            return "guest/dat-phong";
        }
    }
}