
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
    public String search(
            @RequestParam(required = false) String ngayNhan,
            @RequestParam(required = false) String ngayTra,
            @RequestParam(defaultValue = "1") int soKhach,
            Model model
    ) {

        String viewName = "guest/tim-phong";

        model.addAttribute("ngayNhan", ngayNhan);
        model.addAttribute("ngayTra", ngayTra);
        model.addAttribute("soKhach", soKhach);
        model.addAttribute("today", LocalDate.now());

        // KIỂM TRA SỐ KHÁCH
        if (soKhach < 1) {
            model.addAttribute(
                    "error",
                    "Số khách phải lớn hơn hoặc bằng 1."
            );

            model.addAttribute("danhSachPhong", Collections.emptyList());

            return viewName;
        }

        boolean chuaNhapNgayNhan =
                ngayNhan == null || ngayNhan.isBlank();

        boolean chuaNhapNgayTra =
                ngayTra == null || ngayTra.isBlank();


        if (chuaNhapNgayNhan && chuaNhapNgayTra) {

            model.addAttribute(
                    "danhSachPhong",
                    roomSearchService.findAll(soKhach)
            );

            return viewName;
        }

        if (chuaNhapNgayNhan || chuaNhapNgayTra) {

            model.addAttribute(
                    "error",
                    "Vui lòng chọn đầy đủ ngày nhận phòng và ngày trả phòng."
            );

            model.addAttribute("danhSachPhong", Collections.emptyList());

            return viewName;
        }
        try {

            LocalDate checkIn = DateUtil.parse(ngayNhan);
            LocalDate checkOut = DateUtil.parse(ngayTra);

            if (!DateUtil.isValidBookingRange(checkIn, checkOut)) {
                throw new IllegalArgumentException();
            }

            model.addAttribute(
                    "soDem",
                    DateUtil.soDem(checkIn, checkOut)
            );

            model.addAttribute(
                    "danhSachPhong",
                    roomSearchService.findAvailable(
                            Date.valueOf(checkIn),
                            Date.valueOf(checkOut),
                            soKhach
                    )
            );

        } catch (RuntimeException ex) {

            model.addAttribute(
                    "error",
                    "Ngày nhận/trả phòng không hợp lệ."
            );

            model.addAttribute("danhSachPhong", Collections.emptyList());
        }

        return viewName;
    }

    @GetMapping("/chi-tiet-phong")
    public String details(
            @RequestParam int maPhong,
            @RequestParam(required = false) String ngayNhan,
            @RequestParam(required = false) String ngayTra,
            @RequestParam(defaultValue = "1") int soKhach,
            Model model
    ) {

        PhongTrongDTO phong = roomSearchService.findDetails(maPhong);

        if (phong == null) {
            return "redirect:/tim-phong";
        }

        model.addAttribute("phong", phong);
        model.addAttribute("ngayNhan", ngayNhan);
        model.addAttribute("ngayTra", ngayTra);
        model.addAttribute("soKhach", soKhach);

        return "guest/chi-tiet-phong";
    }
}
