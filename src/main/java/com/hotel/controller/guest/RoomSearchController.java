package com.hotel.controller.guest;

import com.hotel.model.PhongTrongDTO;
import com.hotel.service.RoomSearchService;
import com.hotel.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;

@Controller
public class RoomSearchController {
    private final RoomSearchService roomSearchService;

    public RoomSearchController(RoomSearchService roomSearchService) {
        this.roomSearchService = roomSearchService;
    }

    @GetMapping("/tim-phong")
    public String search(@RequestParam(required = false) String ngayNhan,
                         @RequestParam(required = false) String ngayTra, Model model) {
        model.addAttribute("ngayNhan", ngayNhan);
        model.addAttribute("ngayTra", ngayTra);
        if (ngayNhan == null || ngayTra == null) {
            model.addAttribute("danhSachPhong", Collections.emptyList());
            return "guest/tim-phong";
        }
        try {
            LocalDate checkIn = DateUtil.parse(ngayNhan), checkOut = DateUtil.parse(ngayTra);
            if (!DateUtil.isValidRange(checkIn, checkOut)) throw new IllegalArgumentException();
            model.addAttribute("soDem", DateUtil.soDem(checkIn, checkOut));
            model.addAttribute("danhSachPhong",
                    roomSearchService.findAvailable(Date.valueOf(checkIn), Date.valueOf(checkOut)));
        } catch (RuntimeException ex) {
            model.addAttribute("error", "Ngày nhận/trả phòng không hợp lệ.");
            model.addAttribute("danhSachPhong", Collections.emptyList());
        }
        return "guest/tim-phong";
    }

    @GetMapping("/chi-tiet-phong")
    public String details(@RequestParam int maPhong, @RequestParam(required = false) String ngayNhan,
                          @RequestParam(required = false) String ngayTra, Model model) {
        PhongTrongDTO phong = roomSearchService.findDetails(maPhong);
        if (phong == null) return "redirect:/tim-phong";
        model.addAttribute("phong", phong);
        model.addAttribute("ngayNhan", ngayNhan);
        model.addAttribute("ngayTra", ngayTra);
        return "guest/chi-tiet-phong";
    }
}
