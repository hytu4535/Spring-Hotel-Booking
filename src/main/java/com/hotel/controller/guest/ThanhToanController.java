package com.hotel.controller.guest;

import com.hotel.model.DatPhong;
import com.hotel.model.ThanhToan;
import com.hotel.service.DatPhongService;
import com.hotel.service.ThanhToanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class ThanhToanController {

    private final ThanhToanService thanhToanService;
    private final DatPhongService datPhongService;

    public ThanhToanController(ThanhToanService thanhToanService, DatPhongService datPhongService) {
        this.thanhToanService = thanhToanService;
        this.datPhongService = datPhongService;
    }

    @GetMapping("/thanh-toan/dat-coc")
    public String depositPage(@RequestParam("maTraCuu") String maTraCuu, Model model) {
        DatPhong dp = datPhongService.findByMaTraCuu(maTraCuu);
        if (dp == null) {
            model.addAttribute("error", "Không tìm thấy đơn đặt phòng.");
            return "error/404";
        }
        BigDecimal depositAmount = thanhToanService.calculateDepositAmount(dp.getTongTien());
        List<ThanhToan> paymentHistory = thanhToanService.getThanhToanByMaDp(dp.getMaDp());

        model.addAttribute("datPhong", dp);
        model.addAttribute("depositAmount", depositAmount);
        model.addAttribute("paymentHistory", paymentHistory);
        return "guest/thanh-toan-coc";
    }

    @GetMapping("/thanh-toan/vnpay")
    public String payWithVnpay(@RequestParam("maTraCuu") String maTraCuu, HttpServletRequest request) {
        String paymentUrl = thanhToanService.createVnpayPaymentUrl(maTraCuu, request);
        return "redirect:" + paymentUrl;
    }

    @GetMapping("/thanh-toan/vnpay-return")
    public String vnpayReturn(HttpServletRequest request, Model model) {
        Map<String, String> params = new java.util.HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 0) {
                params.put(k, v[0]);
            }
        });

        Map<String, Object> returnResult = thanhToanService.processVnpayReturn(params);
        model.addAllAttributes(returnResult);

        if (returnResult.containsKey("datPhong")) {
            DatPhong dp = (DatPhong) returnResult.get("datPhong");
            model.addAttribute("paymentHistory", thanhToanService.getThanhToanByMaDp(dp.getMaDp()));
        }

        return "guest/ket-qua-thanh-toan";
    }
}
