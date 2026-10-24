package com.hotel.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/admin/khuyen-mai")
public class KhuyenMaiAdminController {

    private static final List<Map<String, Object>> MOCK_PROMOTIONS = new ArrayList<>();

    static {
        if (MOCK_PROMOTIONS.isEmpty()) {
            Map<String, Object> p1 = new HashMap<>();
            p1.put("id", 1);
            p1.put("maCode", "SUMMER2026");
            p1.put("tenKhuyenMai", "Giảm giá mùa hè 2026");
            p1.put("mucGiam", "15%");
            p1.put("ngayBatDau", "2026-06-01");
            p1.put("ngayKetThuc", "2026-08-31");
            p1.put("dieuKien", "Đơn hàng tối thiểu 1,000,000đ");
            p1.put("trangThai", "Đang áp dụng");
            MOCK_PROMOTIONS.add(p1);

            Map<String, Object> p2 = new HashMap<>();
            p2.put("id", 2);
            p2.put("maCode", "WELCOMEVIP");
            p2.put("tenKhuyenMai", "Chào mừng khách hàng mới");
            p2.put("mucGiam", "200,000đ");
            p2.put("ngayBatDau", "2026-01-01");
            p2.put("ngayKetThuc", "2026-12-31");
            p2.put("dieuKien", "Áp dụng cho lần đặt phòng đầu tiên");
            p2.put("trangThai", "Đang áp dụng");
            MOCK_PROMOTIONS.add(p2);
        }
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("promotions", MOCK_PROMOTIONS);
        return "admin/quan-ly-khuyen-mai";
    }

    @PostMapping("/them")
    public String create(@RequestParam String maCode,
                         @RequestParam String tenKhuyenMai,
                         @RequestParam String mucGiam,
                         @RequestParam String ngayBatDau,
                         @RequestParam String ngayKetThuc,
                         @RequestParam String dieuKien,
                         RedirectAttributes redirectAttributes) {
        Map<String, Object> p = new HashMap<>();
        p.put("id", MOCK_PROMOTIONS.isEmpty() ? 1 : ((int) MOCK_PROMOTIONS.get(MOCK_PROMOTIONS.size() - 1).get("id") + 1));
        p.put("maCode", maCode.toUpperCase());
        p.put("tenKhuyenMai", tenKhuyenMai);
        p.put("mucGiam", mucGiam);
        p.put("ngayBatDau", ngayBatDau);
        p.put("ngayKetThuc", ngayKetThuc);
        p.put("dieuKien", dieuKien);
        p.put("trangThai", "Đang áp dụng");
        MOCK_PROMOTIONS.add(p);

        redirectAttributes.addFlashAttribute("success", "Thêm mã khuyến mãi thành công.");
        return "redirect:/admin/khuyen-mai";
    }

    @PostMapping("/sua")
    public String update(@RequestParam Integer id,
                         @RequestParam String maCode,
                         @RequestParam String tenKhuyenMai,
                         @RequestParam String mucGiam,
                         @RequestParam String ngayBatDau,
                         @RequestParam String ngayKetThuc,
                         @RequestParam String dieuKien,
                         RedirectAttributes redirectAttributes) {
        for (Map<String, Object> p : MOCK_PROMOTIONS) {
            if (Objects.equals(p.get("id"), id)) {
                p.put("maCode", maCode.toUpperCase());
                p.put("tenKhuyenMai", tenKhuyenMai);
                p.put("mucGiam", mucGiam);
                p.put("ngayBatDau", ngayBatDau);
                p.put("ngayKetThuc", ngayKetThuc);
                p.put("dieuKien", dieuKien);
                break;
            }
        }
        redirectAttributes.addFlashAttribute("success", "Cập nhật mã khuyến mãi thành công.");
        return "redirect:/admin/khuyen-mai";
    }

    @PostMapping("/xoa")
    public String delete(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
        MOCK_PROMOTIONS.removeIf(p -> Objects.equals(p.get("id"), id));
        redirectAttributes.addFlashAttribute("success", "Xóa mã khuyến mãi thành công.");
        return "redirect:/admin/khuyen-mai";
    }
}
