package com.hotel.controller.guest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.dao.PhongDAO;
import com.hotel.model.PhongTrongDTO;


@Controller
public class ChiTietPhongServlet {

    private final PhongDAO phongDAO = new PhongDAO();

    @GetMapping("/chi-tiet-phong")
    public String doGet(@RequestParam(required = false) String maPhong,
                        @RequestParam(required = false) String ngayNhan,
                        @RequestParam(required = false) String ngayTra,
                        Model model) {
        String maPhongRaw = maPhong;
        if (maPhongRaw == null) {
            return "redirect:/tim-phong";

        }

        try {
            int maPhongId = Integer.parseInt(maPhongRaw);
            PhongTrongDTO phong = phongDAO.findPhongChiTiet(maPhongId);
            if (phong == null) {
                return "redirect:/tim-phong";

            }

            model.addAttribute("phong", phong);
            model.addAttribute("ngayNhan", ngayNhan);
            model.addAttribute("ngayTra", ngayTra);
            return "guest/chi-tiet-phong";
        } catch (NumberFormatException e) {
            return "redirect:/tim-phong";
        }
    }
}