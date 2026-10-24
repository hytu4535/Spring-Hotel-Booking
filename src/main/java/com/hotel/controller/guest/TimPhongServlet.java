package com.hotel.controller.guest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.dao.PhongDAO;
import com.hotel.model.PhongTrongDTO;
import com.hotel.util.DateUtil;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
public class TimPhongServlet {

    private final PhongDAO phongDAO = new PhongDAO();

    @GetMapping("/tim-phong")
    public String doGet(@RequestParam(required = false) String ngayNhan,
                        @RequestParam(required = false) String ngayTra,
                        Model model) {

        model.addAttribute("ngayNhan", ngayNhan);
        model.addAttribute("ngayTra", ngayTra);

        if (ngayNhan == null || ngayTra == null) {
            model.addAttribute("danhSachPhong", Collections.emptyList());
            return "guest/tim-phong";

        }

        LocalDate dNhan = DateUtil.parse(ngayNhan);
        LocalDate dTra = DateUtil.parse(ngayTra);

        if (!DateUtil.isValidRange(dNhan, dTra)) {
            model.addAttribute("error", "Ngày nhận/trả phòng không hợp lệ.");
            model.addAttribute("danhSachPhong", Collections.emptyList());
            return "guest/tim-phong";

        }

        int soDem = DateUtil.soDem(dNhan, dTra);
        model.addAttribute("soDem", soDem);

        List<PhongTrongDTO> danhSachPhong = phongDAO.timPhongTrong(Date.valueOf(dNhan), Date.valueOf(dTra));
        model.addAttribute("danhSachPhong", danhSachPhong);
        return "guest/tim-phong";
    }
}