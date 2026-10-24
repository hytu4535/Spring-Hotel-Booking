package com.hotel.controller.staff;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.hotel.dao.DatPhongDAO;
import com.hotel.model.DatPhongDTO;
import com.hotel.model.TaiKhoan;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DonDatPhongServlet {

    private final DatPhongDAO datPhongDAO = new DatPhongDAO();

    @GetMapping({"/staff/don", "/admin/dat-phong"})
    public String doGet(HttpSession session, Model model) {
        // Lấy danh sách toàn bộ đơn đặt phòng
        List<DatPhongDTO> danhSachDon = datPhongDAO.findAllWithKhachHang();
        model.addAttribute("danhSachDon", danhSachDon);
        
        TaiKhoan user = (TaiKhoan) session.getAttribute("currentUser");
        boolean admin = user != null && "ADMIN".equals(user.getVaiTro());
        model.addAttribute("bookingBasePath", admin ? "/admin/dat-phong" : "/staff/don");
        model.addAttribute("isAdminView", admin);
        return "staff/quan-ly-don";
    }

    @PostMapping({"/staff/don", "/admin/dat-phong"})
    public String doPost(@RequestParam String action,
                         @RequestParam(required = false) Integer maDp,
                         @RequestParam(required = false) String trangThaiMoi,
                         HttpSession session) {
        
        if ("update_status".equals(action)) {
            
            // Lấy mã NV đang thao tác từ Session
            TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
            
            // Đổi trạng thái Đơn + Phòng bằng Transaction
            boolean success = datPhongDAO.updateTrangThaiTransaction(maDp, trangThaiMoi, currentUser.getMaTk());
            
            if (success) {
                session.setAttribute("successMsg", "Cập nhật trạng thái đơn thành công!");
            } else {
                session.setAttribute("errorMsg", "Lỗi khi cập nhật trạng thái!");
            }
        }
        
        // Cập nhật xong thì reload lại trang danh sách
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        String basePath = currentUser != null && "ADMIN".equals(currentUser.getVaiTro())
                ? "/admin/dat-phong" : "/staff/don";
        return "redirect:" + basePath;
    }
}