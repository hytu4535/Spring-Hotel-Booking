package com.hotel.controller.guest;

import com.hotel.model.CartItem;
import com.hotel.model.CartServiceItem;
import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.DatPhong;
import com.hotel.model.DichVu;
import com.hotel.model.GioHang;
import com.hotel.model.KhachHang;
import com.hotel.model.TaiKhoan;
import com.hotel.service.DatPhongService;
import com.hotel.service.DichVuService;
import com.hotel.service.KhachHangService;
import com.hotel.service.ThanhToanService;
import com.hotel.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GuestBookingController {

    private static final Logger log = LoggerFactory.getLogger(GuestBookingController.class);
    private static final String NAME_REGEX = "^[\\p{L}][\\p{L} .'-]{1,99}$";
    private static final String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    private static final String PHONE_REGEX = "^0\\d{9}$";
    private final DatPhongService datPhongService;
    private final DichVuService dichVuService;
    private final ThanhToanService thanhToanService;
    private final KhachHangService khachHangService;

    public GuestBookingController(DatPhongService datPhongService, DichVuService dichVuService,
            ThanhToanService thanhToanService, KhachHangService khachHangService) {
        this.datPhongService = datPhongService;
        this.dichVuService = dichVuService;
        this.thanhToanService = thanhToanService;
        this.khachHangService = khachHangService;
    }

    // ================= XỬ LÝ GIỎ HÀNG =================
    @GetMapping("/gio-hang")
    public String viewCart(HttpSession session, @RequestParam(required = false) String action,
            @RequestParam(required = false) Integer maPhong, Model model) {
        GioHang cart = getCart(session);
        if ("remove".equals(action) && maPhong != null) {
            cart.removePhong(maPhong);
        }
        if ("remove-service".equals(action) && maPhong != null) {
            cart.removeService(maPhong);
        }
        model.addAttribute("tongTien", cart.getTongTien());
        model.addAttribute("tienCoc", thanhToanService.calculateDepositAmount(cart.getTongTien()));
        return "guest/gio-hang";
    }

    @ModelAttribute("dichVuList")
    public List<DichVu> activeServices() {
        return dichVuService.search(null).stream()
                .filter(dichVu -> dichVu.getTrangThai() == 1)
                .collect(java.util.stream.Collectors.toList());
    }

    @PostMapping("/gio-hang")
    public String addToCart(HttpSession session, @RequestParam int maPhong, @RequestParam String soPhong,
            @RequestParam String tenLoai, @RequestParam BigDecimal giaPhong,
            @RequestParam String ngayNhan, @RequestParam String ngayTra,
            RedirectAttributes redirectAttributes) {
        GioHang cart = getCart(session);
        LocalDate checkIn = DateUtil.parse(ngayNhan);
        LocalDate checkOut = DateUtil.parse(ngayTra);
        if (!DateUtil.isValidBookingRange(checkIn, checkOut)) {
            redirectAttributes.addFlashAttribute("error",
                    "Ngày nhận phòng không được ở quá khứ và phải trước ngày trả phòng.");
            return "redirect:/tim-phong";
        }

        if (cart.getNgayNhan() == null) {
            cart.setNgayNhan(ngayNhan);
            cart.setNgayTra(ngayTra);
            cart.setSoDem(DateUtil.soDem(DateUtil.parse(ngayNhan), DateUtil.parse(ngayTra)));
        }

        CartItem item = new CartItem();
        item.setMaPhong(maPhong);
        item.setSoPhong(soPhong);
        item.setTenLoai(tenLoai);
        item.setGiaPhong(giaPhong);
        item.setSoDem(cart.getSoDem());
        item.setThanhTien(giaPhong.multiply(new BigDecimal(cart.getSoDem())));

        cart.addPhong(item);
        return "redirect:/gio-hang";
    }

    @PostMapping(value = "/gio-hang", params = "action=add-service")
    public String addServiceToCart(HttpSession session, @RequestParam int maDv,
            @RequestParam int soLuong, RedirectAttributes redirectAttributes) {
        try {
            if (soLuong < 1 || soLuong > 100) {
                throw new IllegalArgumentException("Số lượng dịch vụ phải từ 1 đến 100.");
            }
            DichVu dichVu = dichVuService.get(maDv);
            if (dichVu.getTrangThai() != 1) {
                throw new IllegalArgumentException("Dịch vụ đã ngừng cung cấp.");
            }
            getCart(session).addService(new CartServiceItem(
                    dichVu.getMaDv(), dichVu.getTenDv(), dichVu.getDonGia(), soLuong));
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/gio-hang";
    }

    @PostMapping(value = "/gio-hang", params = "action=update-dates")
    public String updateCartDates(HttpSession session, @RequestParam String ngayNhan,
            @RequestParam String ngayTra, RedirectAttributes redirectAttributes) {
        GioHang cart = getCart(session);
        LocalDate checkIn = DateUtil.parse(ngayNhan);
        LocalDate checkOut = DateUtil.parse(ngayTra);
        if (!DateUtil.isValidBookingRange(checkIn, checkOut)) {
            redirectAttributes.addFlashAttribute("error",
                    "Ngày nhận phòng không được ở quá khứ và phải trước ngày trả phòng.");
            return "redirect:/gio-hang";
        }
        cart.updateDates(ngayNhan, ngayTra, DateUtil.soDem(checkIn, checkOut));
        return "redirect:/gio-hang";
    }

    // ================= XỬ LÝ ĐẶT PHÒNG =================
    @GetMapping("/dat-phong")
    public String showCheckoutForm(HttpSession session, Model model) {
        GioHang cart = getCart(session);
        if (cart.getItems().isEmpty()) {
            return "redirect:/tim-phong";
        }

        // Nếu là Customer đã login, tự điền thông tin
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser != null && "CUSTOMER".equals(currentUser.getVaiTro())) {
            model.addAttribute("hoTen", currentUser.getHoTen());
            model.addAttribute("email", currentUser.getEmail());
            model.addAttribute("soDienThoai", currentUser.getSoDienThoai());
            KhachHang profile = khachHangService.getForAccount(currentUser.getMaTk());
            if (profile != null) {
                model.addAttribute("cccd", profile.getCccd());
                model.addAttribute("diaChi", profile.getDiaChi());
            }
        }
        return "guest/dat-phong";
    }

    @PostMapping("/dat-phong")
    public String submitBooking(HttpSession session,
            @ModelAttribute KhachHang khachHang,
            @RequestParam(required = false) String ghiChu,
            RedirectAttributes redirectAttributes) {
        GioHang cart = getCart(session);
        if (cart.getItems().isEmpty())
            return "redirect:/tim-phong";

        try {
            TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
            if (currentUser != null && "CUSTOMER".equals(currentUser.getVaiTro())) {
                khachHang.setMaTk(currentUser.getMaTk());
                KhachHang profile = khachHangService.getForAccount(currentUser.getMaTk());
                khachHang.setHoTen(currentUser.getHoTen());
                khachHang.setEmail(currentUser.getEmail());
                khachHang.setSoDienThoai(currentUser.getSoDienThoai());
                khachHang.setCccd(profile == null ? null : profile.getCccd());
                khachHang.setDiaChi(profile == null ? null : profile.getDiaChi());
            }
            validateBookingCustomer(khachHang);
            if (ghiChu != null && ghiChu.length() > 255) {
                throw new IllegalArgumentException("Ghi chú không được vượt quá 255 ký tự.");
            }
            DatPhong dp = new DatPhong();
            dp.setNgayNhanPhong(java.sql.Date.valueOf(cart.getNgayNhan()));
            dp.setNgayTraPhong(java.sql.Date.valueOf(cart.getNgayTra()));
            dp.setTienPhong(cart.getTongTien());
            dp.setGhiChu(ghiChu == null ? null : ghiChu.trim());

            List<ChiTietDatPhong> ctList = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                ChiTietDatPhong ct = new ChiTietDatPhong();
                ct.setMaPhong(item.getMaPhong());
                ct.setGiaPhong(item.getGiaPhong());
                ct.setSoDem(item.getSoDem());
                ct.setThanhTien(item.getThanhTien());
                ctList.add(ct);
            }

            // Transaction + Kafka
            String maTraCuu = datPhongService.taoDonDatPhong(khachHang, dp, ctList, cart.getServices());

            session.removeAttribute("cart");
            session.setAttribute("maTraCuu", maTraCuu);
            session.setAttribute("emailDatPhong", khachHang.getEmail());

            return "redirect:/dat-phong-thanh-cong";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/dat-phong";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/dat-phong";
        } catch (RuntimeException e) {
            log.error("Không thể tạo đơn đặt phòng", e);
            redirectAttributes.addFlashAttribute("error",
                    "Không thể hoàn tất đặt phòng lúc này. Vui lòng kiểm tra thông tin và thử lại.");
            return "redirect:/dat-phong";
        }
    }

    private void validateBookingCustomer(KhachHang khachHang) {
        String hoTen = trim(khachHang.getHoTen());
        String email = trim(khachHang.getEmail());
        String soDienThoai = trim(khachHang.getSoDienThoai());
        String diaChi = trim(khachHang.getDiaChi());
        if (hoTen.isEmpty() || !hoTen.matches(NAME_REGEX)) {
            throw new IllegalArgumentException("Họ tên bắt buộc và chỉ được chứa chữ cái, khoảng trắng, dấu chấm, gạch nối hoặc dấu nháy.");
        }
        if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Email bắt buộc và phải đúng định dạng, ví dụ example@email.com.");
        }
        if (soDienThoai.isEmpty() || !soDienThoai.matches(PHONE_REGEX)) {
            throw new IllegalArgumentException("Số điện thoại bắt buộc, gồm 10 chữ số và bắt đầu bằng 0.");
        }
        if (diaChi.isEmpty() || diaChi.length() > 255) {
            throw new IllegalArgumentException("Địa chỉ bắt buộc và không được vượt quá 255 ký tự.");
        }
        khachHang.setHoTen(hoTen);
        khachHang.setEmail(email);
        khachHang.setSoDienThoai(soDienThoai);
        khachHang.setDiaChi(diaChi);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    @GetMapping("/dat-phong-thanh-cong")
    public String bookingSuccess(HttpSession session, Model model) {
        String maTraCuu = (String) session.getAttribute("maTraCuu");
        if (maTraCuu == null)
            return "redirect:/";
        DatPhong booking = datPhongService.timTheoMaTraCuu(maTraCuu);
        if (booking == null) {
            session.removeAttribute("maTraCuu");
            return "redirect:/";
        }
        model.addAttribute("booking", booking);
        return "guest/dat-phong-thanh-cong";
    }

    private GioHang getCart(HttpSession session) {
        GioHang cart = (GioHang) session.getAttribute("cart");
        if (cart == null) {
            cart = new GioHang();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}