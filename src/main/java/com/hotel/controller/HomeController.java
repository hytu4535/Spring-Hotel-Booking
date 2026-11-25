package com.hotel.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDate;

@Controller
public class HomeController {

    @GetMapping({"/", "/trang-chu"})
    public String home(Model model) {
        model.addAttribute("today", LocalDate.now());
        return "guest/index";
    }
}
