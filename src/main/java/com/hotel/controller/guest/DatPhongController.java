package com.hotel.controller.guest;

import com.hotel.model.*;
import com.hotel.service.DatPhongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DatPhongController {

    private final DatPhongService datPhongService;

    public DatPhongController(DatPhongService datPhongService) {
        this.datPhongService = datPhongService;
    }

    @GetMapping("/dat-phong")
    public String bookingForm(HttpSession session, Model model) {
        GioHang cart = (GioHang) session.getAttribute("cart");
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return "redirect:/tim-phong";
        }
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("currentUser", currentUser);
        }
        return "guest/dat-phong";
    }

    @PostMapping("/dat-phong")
    public String submitBooking(HttpServletRequest request, HttpSession session, Model model) {
        GioHang cart = (GioHang) session.getAttribute("cart");
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return "redirect:/tim-phong";
        }

        try {
            KhachHang kh = new KhachHang();
            kh.setHoTen(request.getParameter("hoTen"));
            kh.setEmail(request.getParameter("email"));
            kh.setSoDienThoai(request.getParameter("soDienThoai"));
            kh.setCccd(request.getParameter("cccd"));
            kh.setDiaChi(request.getParameter("diaChi"));

            DatPhong dp = new DatPhong();
            dp.setNgayNhanPhong(java.sql.Date.valueOf(cart.getNgayNhan()));
            dp.setNgayTraPhong(java.sql.Date.valueOf(cart.getNgayTra()));
            dp.setTienPhong(cart.getTienPhong());
            dp.setTienDichVu(cart.getTienDichVu());
            dp.setTongTien(cart.getTongTien());

            List<ChiTietDatPhong> ctList = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                ChiTietDatPhong ct = new ChiTietDatPhong();
                Phong phong = new Phong();
                phong.setMaPhong(item.getMaPhong());
                ct.setPhong(phong);
                ct.setGiaPhong(item.getGiaPhong());
                ct.setSoDem(item.getSoDem());
                ct.setThanhTien(item.getThanhTien());
                ctList.add(ct);
            }

            TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
            String maTraCuu = datPhongService.taoDonDatPhong(kh, dp, ctList, cart.getServices(), currentUser);

            if (maTraCuu != null) {
                session.removeAttribute("cart");
                session.setAttribute("maTraCuu", maTraCuu);
                session.setAttribute("emailDatPhong", kh.getEmail());
                return "redirect:/thanh-toan/dat-coc?maTraCuu=" + maTraCuu;
            } else {
                model.addAttribute("error", "Một số phòng bạn chọn vừa bị người khác đặt mất. Vui lòng kiểm tra lại sơ đồ phòng!");
                return "guest/dat-phong";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Dữ liệu nhập vào chưa hợp lệ: " + e.getMessage());
            return "guest/dat-phong";
        }
    }
}
