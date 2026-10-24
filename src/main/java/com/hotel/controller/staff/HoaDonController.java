package com.hotel.controller.staff;

import com.hotel.dao.DatPhongDAO;
import com.hotel.model.DatPhongDTO;
import com.hotel.model.ThanhToan;
import com.hotel.model.TaiKhoan;
import com.hotel.repository.ThanhToanRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class HoaDonController {

    private final DatPhongDAO datPhongDAO = new DatPhongDAO();
    private final ThanhToanRepository thanhToanRepository;

    public HoaDonController(ThanhToanRepository thanhToanRepository) {
        this.thanhToanRepository = thanhToanRepository;
    }

    @GetMapping({"/staff/hoa-don", "/admin/hoa-don"})
    public String viewHoaDon(@RequestParam Integer maDp, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;
        if (user == null || (!"ADMIN".equals(user.getVaiTro()) && !"NHANVIEN".equals(user.getVaiTro()))) {
            return "redirect:/login";
        }

        List<DatPhongDTO> allDon = datPhongDAO.findAllWithKhachHang();
        DatPhongDTO don = allDon.stream().filter(d -> d.getMaDp() == maDp).findFirst().orElse(null);

        if (don == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn đặt phòng với ID: " + maDp);
        }

        List<ThanhToan> danhSachThanhToan = thanhToanRepository.findByDatPhong_MaDp(maDp);

        BigDecimal tongDaThanhToan = BigDecimal.ZERO;
        BigDecimal tongHoanTien = BigDecimal.ZERO;
        for (ThanhToan tt : danhSachThanhToan) {
            if ("HOAN_TIEN".equals(tt.getLoaiThanhToan())) {
                tongHoanTien = tongHoanTien.add(tt.getSoTien() != null ? tt.getSoTien() : BigDecimal.ZERO);
            } else {
                tongDaThanhToan = tongDaThanhToan.add(tt.getSoTien() != null ? tt.getSoTien() : BigDecimal.ZERO);
            }
        }
        BigDecimal thucThu = tongDaThanhToan.subtract(tongHoanTien);
        BigDecimal tongTien = don.getTongTien() != null ? don.getTongTien() : BigDecimal.ZERO;
        BigDecimal soTienConLai = tongTien.subtract(thucThu);
        if (soTienConLai.compareTo(BigDecimal.ZERO) < 0) {
            soTienConLai = BigDecimal.ZERO;
        }

        model.addAttribute("don", don);
        model.addAttribute("danhSachThanhToan", danhSachThanhToan);
        model.addAttribute("tongDaThanhToan", tongDaThanhToan);
        model.addAttribute("tongHoanTien", tongHoanTien);
        model.addAttribute("thucThu", thucThu);
        model.addAttribute("soTienConLai", soTienConLai);
        model.addAttribute("isAdminView", request.getRequestURI().startsWith("/admin/"));

        return "staff/hoa-don";
    }
}
