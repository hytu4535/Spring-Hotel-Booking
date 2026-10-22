package com.hotel.controller.guest;

import com.hotel.model.DatPhong;
import com.hotel.service.DatPhongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tra-cuu-don")
public class OrderTrackingController {

    private final DatPhongService datPhongService;

    public OrderTrackingController(DatPhongService datPhongService) {
        this.datPhongService = datPhongService;
    }

    @GetMapping
    public String showForm() {
        return "guest/tra-cuu-don";
    }

    @PostMapping(params = "action=search")
    public String searchBooking(@RequestParam String maTraCuu, @RequestParam String email, Model model) {
        DatPhong dp = datPhongService.traCuuDon(maTraCuu, email);
        if (dp != null) {
            model.addAttribute("donDatPhong", dp);
            model.addAttribute("khachHang", dp.getKhachHang());
        } else {
            model.addAttribute("error", "Không tìm thấy đơn hàng hoặc Email không khớp!");
        }
        model.addAttribute("maTraCuuInput", maTraCuu);
        model.addAttribute("emailInput", email);
        return "guest/tra-cuu-don";
    }

    @PostMapping(params = "action=cancel")
    public String cancelBooking(@RequestParam int maDp, RedirectAttributes redirectAttributes) {
        datPhongService.huyDonPhong(maDp);
        redirectAttributes.addFlashAttribute("success", "Đã hủy đơn đặt phòng thành công!");
        return "redirect:/tra-cuu-don";
    }
}