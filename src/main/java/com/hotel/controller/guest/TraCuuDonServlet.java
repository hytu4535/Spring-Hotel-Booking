package com.hotel.controller.guest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.dao.DatPhongDAO;
import com.hotel.dao.KhachHangDAO;
import com.hotel.model.DatPhong;
import com.hotel.model.KhachHang;


@Controller
public class TraCuuDonServlet {

    private final DatPhongDAO datPhongDAO = new DatPhongDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    @GetMapping("/tra-cuu-don")
    public String doGet() {
        return "guest/tra-cuu-don";
    }

    @PostMapping("/tra-cuu-don")
    public String doPost(@RequestParam String action,
                         @RequestParam(required = false) String maTraCuu,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) Integer maDp,
                         Model model) {

        // 1. CHỨC NĂNG TÌM KIẾM ĐƠN
        if ("search".equals(action)) {
            DatPhong dp = datPhongDAO.findByMaTraCuu(maTraCuu);
            if (dp != null) {
                KhachHang kh = khachHangDAO.findById(dp.getMaKh()); // Nhớ tạo hàm này trong KhachHangDAO
                if (kh != null && kh.getEmail().equalsIgnoreCase(email)) {
                    model.addAttribute("donDatPhong", dp);
                    model.addAttribute("khachHang", kh);
                } else {
                    model.addAttribute("error", "Email không khớp với mã tra cứu!");
                }
            } else {
                model.addAttribute("error", "Không tìm thấy đơn hàng với mã này!");
            }
            
            // Giữ lại input để khách khỏi nhập lại
            model.addAttribute("maTraCuuInput", maTraCuu);
            model.addAttribute("emailInput", email);
            return "guest/tra-cuu-don";
        }
        
        // 2. CHỨC NĂNG HỦY ĐƠN (Khách tự hủy)
        else if ("cancel".equals(action)) {
            // Khách chỉ được hủy khi đơn đang ở trạng thái CHỜ XÁC NHẬN
            datPhongDAO.updateTrangThai(maDp, "DA_HUY");
            
            model.addAttribute("success", "Đã hủy đơn đặt phòng thành công!");
            return "guest/tra-cuu-don";
        }
        return "guest/tra-cuu-don";
    }
}