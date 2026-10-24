package com.hotel.controller.admin;

import com.hotel.service.ThanhToanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/admin/thanh-toan", "/staff/thanh-toan"})
public class ThanhToanAdminController {

    private final ThanhToanService thanhToanService;

    public ThanhToanAdminController(ThanhToanService thanhToanService) {
        this.thanhToanService = thanhToanService;
    }

    @GetMapping
    public String listTransactions(
            @RequestParam(required = false) String loaiThanhToan,
            @RequestParam(required = false) String phuongThuc,
            @RequestParam(required = false) String keyword,
            Model model,
            javax.servlet.http.HttpServletRequest request) {

        model.addAttribute("transactions", thanhToanService.searchTransactions(loaiThanhToan, phuongThuc, keyword));
        model.addAttribute("loaiThanhToan", loaiThanhToan);
        model.addAttribute("phuongThuc", phuongThuc);
        model.addAttribute("keyword", keyword);

        Map<String, Object> reconciliation = thanhToanService.getOnlineReconciliation();
        model.addAttribute("reconciliation", reconciliation);
        
        List<com.hotel.model.DatPhongDTO> danhSachDon = new com.hotel.dao.DatPhongDAO().findAllWithKhachHang();
        model.addAttribute("danhSachDon", danhSachDon);
        
        boolean isAdmin = request.getRequestURI().startsWith("/admin/");
        model.addAttribute("isAdminView", isAdmin);
        model.addAttribute("paymentBasePath", isAdmin ? "/admin/thanh-toan" : "/staff/thanh-toan");

        return "admin/quan-ly-thanh-toan";
    }

    @org.springframework.web.bind.annotation.PostMapping("/them")
    public String addThanhToan(
            @RequestParam Integer maDp,
            @RequestParam BigDecimal soTien,
            @RequestParam String loaiThanhToan,
            @RequestParam String phuongThuc,
            @RequestParam(required = false) String maGiaoDich,
            @RequestParam(required = false) String ghiChu,
            javax.servlet.http.HttpServletRequest request,
            javax.servlet.http.HttpSession session,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            com.hotel.model.TaiKhoan currentUser = (com.hotel.model.TaiKhoan) session.getAttribute("currentUser");
            thanhToanService.ghiNhanThanhToanThuCong(maDp, soTien, loaiThanhToan, phuongThuc, maGiaoDich, ghiChu, currentUser);
            redirectAttributes.addFlashAttribute("successMsg", "Ghi nhận thanh toán thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi ghi nhận thanh toán: " + e.getMessage());
        }
        String redirectPath = request.getRequestURI().startsWith("/admin/") ? "/admin/thanh-toan" : "/staff/thanh-toan";
        return "redirect:" + redirectPath;
    }
}
