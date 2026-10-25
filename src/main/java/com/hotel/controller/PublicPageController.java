package com.hotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Static public pages that used to be rendered through JSP includes.
 */
@Controller
public class PublicPageController {
    @GetMapping("/gioi-thieu")
    public String about() {
        return "guest/gioi-thieu";
    }

    @GetMapping("/lien-he")
    public String contact() {
        return "guest/lien-he";
    }
}
