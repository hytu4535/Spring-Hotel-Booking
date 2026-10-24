package com.hotel.controller.guest;

import com.hotel.model.DatPhong;
import com.hotel.model.DanhGia;
import com.hotel.model.KhachHang;
import com.hotel.model.TaiKhoan;
import com.hotel.model.ThanhToan;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.KhachHangRepository;
import com.hotel.service.DanhGiaService;
import com.hotel.service.ThanhToanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class TraCuuDonController {

    private final DatPhongRepository datPhongRepository;
    private final KhachHangRepository khachHangRepository;
    private final ThanhToanService thanhToanService;
    private final DanhGiaService danhGiaService;

    public TraCuuDonController(DatPhongRepository datPhongRepository,
                               KhachHangRepository khachHangRepository,
                               ThanhToanService thanhToanService,
                               DanhGiaService danhGiaService) {
        this.datPhongRepository = datPhongRepository;
        this.khachHangRepository = khachHangRepository;
        this.thanhToanService = thanhToanService;
        this.danhGiaService = danhGiaService;
    }

    @GetMapping("/tra-cuu-don")
    public String traCuuForm() {
        return "guest/tra-cuu-don";
    }

    @GetMapping("/khach-hang/lich-su-don")
    public String customerBookingHistory(HttpSession session, Model model) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/staff/login";
        }

        Optional<KhachHang> khOpt = khachHangRepository.findByMaTk(currentUser.getMaTk());
        if (!khOpt.isPresent()) {
            khOpt = khachHangRepository.findByEmailIgnoreCase(currentUser.getEmail());
        }

        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            List<DatPhong> danhSachDon = datPhongRepository.findByKhachHang_MaKhOrderByNgayDatDesc(kh.getMaKh());
            for (DatPhong dp : danhSachDon) {
                if (dp.getChiTietDatPhongs() != null) {
                    dp.getChiTietDatPhongs().size();
                    for (com.hotel.model.ChiTietDatPhong ctdp : dp.getChiTietDatPhongs()) {
                        if (ctdp.getPhong() != null) {
                            ctdp.getPhong().getSoPhong();
                            if (ctdp.getPhong().getLoaiPhong() != null) {
                                ctdp.getPhong().getLoaiPhong().getTenLoai();
                            }
                        }
                    }
                }
            }
            model.addAttribute("danhSachDon", danhSachDon);

            Map<Integer, List<ThanhToan>> paymentMap = new HashMap<>();
            Map<Integer, DanhGia> reviewMap = new HashMap<>();
            Map<Integer, List<com.hotel.model.DatDichVu>> serviceMap = new HashMap<>();
            com.hotel.dao.DatDichVuDAO datDichVuDAO = new com.hotel.dao.DatDichVuDAO();
            for (DatPhong dp : danhSachDon) {
                paymentMap.put(dp.getMaDp(), thanhToanService.getThanhToanByMaDp(dp.getMaDp()));
                Optional<DanhGia> dgOpt = danhGiaService.getDanhGiaByMaDp(dp.getMaDp());
                dgOpt.ifPresent(danhGia -> reviewMap.put(dp.getMaDp(), danhGia));
                serviceMap.put(dp.getMaDp(), datDichVuDAO.findByMaDp(dp.getMaDp()));
            }
            model.addAttribute("paymentMap", paymentMap);
            model.addAttribute("reviewMap", reviewMap);
            model.addAttribute("serviceMap", serviceMap);
        }

        return "guest/lich-su-don";
    }

    @PostMapping("/khach-hang/danh-gia")
    public String submitReview(@RequestParam("maDp") Integer maDp,
                               @RequestParam("soSao") Integer soSao,
                               @RequestParam("noiDung") String noiDung,
                               HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/staff/login";
        }

        Optional<KhachHang> khOpt = khachHangRepository.findByMaTk(currentUser.getMaTk());
        if (!khOpt.isPresent()) {
            khOpt = khachHangRepository.findByEmailIgnoreCase(currentUser.getEmail());
        }

        if (khOpt.isPresent()) {
            danhGiaService.submitReview(maDp, khOpt.get().getMaKh(), soSao, noiDung);
        }

        return "redirect:/khach-hang/lich-su-don";
    }

    @PostMapping("/tra-cuu-don")
    public String handleAction(@RequestParam("action") String action,
                               @RequestParam(value = "maTraCuu", required = false) String maTraCuu,
                               @RequestParam(value = "email", required = false) String email,
                               @RequestParam(value = "maDp", required = false) Integer maDp,
                               Model model) {
        if ("search".equals(action)) {
            if (maTraCuu != null) maTraCuu = maTraCuu.trim();
            if (email != null) email = email.trim();

            Optional<DatPhong> dpOpt = datPhongRepository.findByMaTraCuu(maTraCuu);
            if (dpOpt.isPresent()) {
                DatPhong dp = dpOpt.get();
                KhachHang kh = dp.getKhachHang();
                if (kh != null && kh.getEmail().equalsIgnoreCase(email)) {
                    model.addAttribute("donDatPhong", dp);
                    model.addAttribute("khachHang", kh);
                    List<ThanhToan> paymentHistory = thanhToanService.getThanhToanByMaDp(dp.getMaDp());
                    model.addAttribute("paymentHistory", paymentHistory);
                } else {
                    model.addAttribute("error", "Email không khớp với mã tra cứu!");
                }
            } else {
                model.addAttribute("error", "Không tìm thấy đơn hàng với mã này!");
            }

            model.addAttribute("maTraCuuInput", maTraCuu);
            model.addAttribute("emailInput", email);
        } else if ("cancel".equals(action) && maDp != null) {
            Optional<DatPhong> dpOpt = datPhongRepository.findById(maDp);
            if (dpOpt.isPresent()) {
                DatPhong dp = dpOpt.get();
                if ("CHO_XAC_NHAN".equals(dp.getTrangThai())) {
                    dp.setTrangThai("DA_HUY");
                    datPhongRepository.save(dp);
                    model.addAttribute("success", "Đã hủy đơn đặt phòng thành công!");
                    model.addAttribute("donDatPhong", dp);
                    model.addAttribute("khachHang", dp.getKhachHang());
                    model.addAttribute("paymentHistory", thanhToanService.getThanhToanByMaDp(dp.getMaDp()));
                }
            }
        }

        return "guest/tra-cuu-don";
    }
}
