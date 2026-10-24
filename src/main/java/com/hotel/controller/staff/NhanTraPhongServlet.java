package com.hotel.controller.staff;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.hotel.dao.DatPhongDAO;
import com.hotel.dao.PhongDAO;
import com.hotel.model.PhongSoDoDTO;
import com.hotel.model.TaiKhoan;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NhanTraPhongServlet {

    private final PhongDAO phongDAO = new PhongDAO();
    private final DatPhongDAO datPhongDAO = new DatPhongDAO(); // Dùng lại Transaction đã làm đợt trước

    @GetMapping({"/staff/nhan-tra-phong", "/admin/nhan-tra-phong"})
    public String doGet(HttpSession session, Model model) {
        // Lấy dữ liệu hiển thị Sơ đồ
        List<PhongSoDoDTO> danhSachPhong = phongDAO.getSoDoPhong();
        model.addAttribute("danhSachPhong", danhSachPhong);
        TaiKhoan user = (TaiKhoan) session.getAttribute("currentUser");
        boolean admin = user != null && "ADMIN".equals(user.getVaiTro());
        model.addAttribute("roomBasePath", admin ? "/admin/nhan-tra-phong" : "/staff/nhan-tra-phong");
        model.addAttribute("isAdminView", admin);
        return "staff/so-do-phong";
    }

    @PostMapping({"/staff/nhan-tra-phong", "/admin/nhan-tra-phong"})
    public String doPost(@RequestParam String action, @RequestParam int maDp, HttpSession session) {
        
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");

        boolean success = false;
        
        // Gọi lại Transaction ông đã code hôm trước
        if ("check_in".equals(action)) {
            success = datPhongDAO.updateTrangThaiTransaction(maDp, "DA_NHAN_PHONG", currentUser.getMaTk());
            session.setAttribute(success ? "successMsg" : "errorMsg", 
                                 success ? "Đã Check-in nhận phòng thành công!" : "Lỗi Check-in!");
        } else if ("check_out".equals(action)) {
            success = datPhongDAO.updateTrangThaiTransaction(maDp, "DA_TRA_PHONG", currentUser.getMaTk());
            session.setAttribute(success ? "successMsg" : "errorMsg", 
                                 success ? "Đã Check-out trả phòng thành công!" : "Lỗi Check-out!");
        }

        String basePath = currentUser != null && "ADMIN".equals(currentUser.getVaiTro())
                ? "/admin/nhan-tra-phong" : "/staff/nhan-tra-phong";
        return "redirect:" + basePath;
    }
}