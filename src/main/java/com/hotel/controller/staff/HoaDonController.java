package com.hotel.controller.staff;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Mock invoice screen. It is intentionally read-only and does not access or modify payment data.
 */
@Controller
public class HoaDonController {

    @GetMapping({"/staff/don/{maDp}/hoa-don", "/admin/dat-phong/{maDp}/hoa-don"})
    public String showInvoice(@PathVariable int maDp, HttpServletRequest request, Model model) {
        BigDecimal tienPhong = new BigDecimal("1600000");
        BigDecimal tienDichVu = new BigDecimal("700000");
        BigDecimal tongTien = tienPhong.add(tienDichVu);
        BigDecimal daThanhToan = new BigDecimal("690000");

        boolean isAdmin = request.getServletPath().startsWith("/admin/");
        model.addAttribute("isAdminView", isAdmin);
        model.addAttribute("invoiceCode", "HD-MOCK-" + maDp);
        model.addAttribute("bookingCode", "HTL-MOCK-" + maDp);
        model.addAttribute("customerName", "Nguyễn Văn Minh");
        model.addAttribute("customerPhone", "0901 234 567");
        model.addAttribute("customerEmail", "minh.nguyen@example.com");
        model.addAttribute("checkIn", "24/11/2026");
        model.addAttribute("checkOut", "26/11/2026");
        model.addAttribute("roomItems", Arrays.asList(
                new InvoiceItem("Phòng 201 - Deluxe", "2 đêm x 800.000 đ", tienPhong)
        ));
        model.addAttribute("serviceItems", Arrays.asList(
                new InvoiceItem("Ăn sáng buffet", "2 suất x 150.000 đ", new BigDecimal("300000")),
                new InvoiceItem("Massage - Spa", "1 lần x 400.000 đ", new BigDecimal("400000"))
        ));
        model.addAttribute("paymentItems", Arrays.asList(
                new PaymentItem("Đặt cọc", "VNPay Sandbox", "24/11/2026 10:30", new BigDecimal("690000"))
        ));
        model.addAttribute("tienPhong", tienPhong);
        model.addAttribute("tienDichVu", tienDichVu);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("daThanhToan", daThanhToan);
        model.addAttribute("conLai", tongTien.subtract(daThanhToan));
        model.addAttribute("invoiceBasePath", (isAdmin ? "/admin/dat-phong/" : "/staff/don/") + maDp);
        return "staff/hoa-don";
    }

    public static class InvoiceItem {
        private final String name;
        private final String description;
        private final BigDecimal amount;

        public InvoiceItem(String name, String description, BigDecimal amount) {
            this.name = name;
            this.description = description;
            this.amount = amount;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public BigDecimal getAmount() { return amount; }
    }

    public static class PaymentItem {
        private final String type;
        private final String method;
        private final String paidAt;
        private final BigDecimal amount;

        public PaymentItem(String type, String method, String paidAt, BigDecimal amount) {
            this.type = type;
            this.method = method;
            this.paidAt = paidAt;
            this.amount = amount;
        }

        public String getType() { return type; }
        public String getMethod() { return method; }
        public String getPaidAt() { return paidAt; }
        public BigDecimal getAmount() { return amount; }
    }
}
