package com.hotel.controller.staff;

import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpSession;

public class LogoutServlet {

    @GetMapping("/logout")
    public String doGet(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}
