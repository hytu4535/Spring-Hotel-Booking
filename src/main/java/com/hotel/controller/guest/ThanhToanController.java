package com.hotel.controller.guest;

import com.hotel.service.ThanhToanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/thanh-toan")
public class ThanhToanController {

    private final ThanhToanService thanhToanService;

    public ThanhToanController(ThanhToanService thanhToanService) {
        this.thanhToanService = thanhToanService;
    }

    @GetMapping("/vnpay")
    public String createVnpayPayment(@RequestParam String maTraCuu, HttpServletRequest request, Model model) {
        try {
            return "redirect:" + thanhToanService.createVnpayPaymentUrl(maTraCuu.trim(), request);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("status", "INVALID_PARAMS");
            model.addAttribute("message", exception.getMessage());
            return "guest/vnpay-return";
        }
    }

    @GetMapping("/vnpay-return")
    public String handleVnpayReturn(@RequestParam Map<String, String> params, Model model) {
        Map<String, Object> result = thanhToanService.processVnpayReturn(params);
        model.addAllAttributes(result);
        return "guest/vnpay-return";
    }
}
