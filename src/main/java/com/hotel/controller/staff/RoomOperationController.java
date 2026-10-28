package com.hotel.controller.staff;

import com.hotel.model.TaiKhoan;
import com.hotel.service.DatPhongStaffService;
import com.hotel.service.PhongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class RoomOperationController {
    private final PhongService phongService;
    private final DatPhongStaffService datPhongStaffService;

    public RoomOperationController(PhongService phongService, DatPhongStaffService datPhongStaffService) {
        this.phongService = phongService;
        this.datPhongStaffService = datPhongStaffService;
    }

    @GetMapping({"/staff/nhan-tra-phong", "/admin/nhan-tra-phong"})
    public String roomMap(HttpServletRequest request, Model model) {
        boolean admin = request.getServletPath().startsWith("/admin/");
        model.addAttribute("danhSachPhong", phongService.getSoDoPhong());
        model.addAttribute("isAdminView", admin);
        model.addAttribute("roomBasePath", admin ? "/admin/nhan-tra-phong" : "/staff/nhan-tra-phong");
        return admin ? "admin/so-do-phong" : "staff/so-do-phong";
    }

    @PostMapping({"/staff/nhan-tra-phong", "/admin/nhan-tra-phong"})
    public String process(@RequestParam String action, @RequestParam int maDp,
                          HttpServletRequest request, HttpSession session,
                          RedirectAttributes redirectAttributes) {
        TaiKhoan user = (TaiKhoan) session.getAttribute("currentUser");
        String status = "check_in".equals(action) ? "DA_NHAN_PHONG"
                : "check_out".equals(action) ? "DA_TRA_PHONG" : null;
        boolean success = status != null && user != null
                && datPhongStaffService.updateTrangThaiTransaction(maDp, status, user.getMaTk());
        redirectAttributes.addFlashAttribute(success ? "successMsg" : "errorMsg",
                success ? ("check_in".equals(action) ? "Đã Check-in nhận phòng thành công!" : "Đã Check-out trả phòng thành công!")
                        : "Không thể cập nhật trạng thái phòng.");
        return "redirect:" + (request.getServletPath().startsWith("/admin/")
                ? "/admin/nhan-tra-phong" : "/staff/nhan-tra-phong");
    }
}
