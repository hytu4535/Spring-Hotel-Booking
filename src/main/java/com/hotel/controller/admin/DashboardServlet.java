package com.hotel.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardServlet {

    @GetMapping("/admin/dashboard")
    public String doGet() {
            
        // Load giao diện Dashboard (đã có AdminFilter bảo vệ chặn người lạ bên ngoài)
        return "admin/dashboard";
    }
} 