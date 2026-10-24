package com.hotel.controller.guest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class DatPhongThanhCongServlet {
    @GetMapping("/dat-phong-thanh-cong")
    public String doGet(HttpSession session) {
        String maTraCuu = (String) session.getAttribute("maTraCuu");
        
        if (maTraCuu == null) {
            return "redirect:/";

        }
        
        return "guest/dat-phong-thanh-cong";
    }
}