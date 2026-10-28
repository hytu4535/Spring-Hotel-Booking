package com.hotel.controller.staff;

import com.hotel.service.DichVuService;
import com.hotel.service.KhachHangService;
import com.hotel.service.LoaiPhongService;
import com.hotel.service.PhongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Staff-facing read screens. Mutating operations remain in the admin area.
 */
@Controller
public class StaffFeatureController {
    private final LoaiPhongService loaiPhongService;
    private final PhongService phongService;
    private final DichVuService dichVuService;
    private final KhachHangService khachHangService;

    public StaffFeatureController(LoaiPhongService loaiPhongService, PhongService phongService,
                                  DichVuService dichVuService, KhachHangService khachHangService) {
        this.loaiPhongService = loaiPhongService;
        this.phongService = phongService;
        this.dichVuService = dichVuService;
        this.khachHangService = khachHangService;
    }

    @GetMapping("/staff/loai-phong")
    public String roomTypes(@RequestParam(required = false) String tenLoai, Model model) {
        model.addAttribute("tenLoai", tenLoai == null ? "" : tenLoai);
        model.addAttribute("loaiPhongList", loaiPhongService.getAllLoaiPhong());
        return "staff/loai-phong";
    }

    @GetMapping("/staff/phong")
    public String rooms(Model model) {
        model.addAttribute("phongList", phongService.getAllPhong());
        return "staff/phong";
    }

    @GetMapping("/staff/dich-vu")
    public String services(@RequestParam(required = false) String tenDv, Model model) {
        model.addAttribute("tenDv", tenDv == null ? "" : tenDv);
        model.addAttribute("dichVuList", dichVuService.search(tenDv));
        return "staff/dich-vu";
    }

    @GetMapping("/staff/khach-hang")
    public String customers(@RequestParam(required = false) String hoTen,
                            @RequestParam(required = false) String email,
                            @RequestParam(required = false) String soDienThoai,
                            @RequestParam(required = false) String cccd,
                            @RequestParam(required = false) String soDonOrder,
                            Model model) {
        model.addAttribute("hoTen", hoTen == null ? "" : hoTen);
        model.addAttribute("email", email == null ? "" : email);
        model.addAttribute("soDienThoai", soDienThoai == null ? "" : soDienThoai);
        model.addAttribute("cccd", cccd == null ? "" : cccd);
        model.addAttribute("soDonOrder", soDonOrder == null ? "" : soDonOrder);
        model.addAttribute("khachHangList",
                khachHangService.searchAdvanced(hoTen, email, soDienThoai, cccd, soDonOrder));
        return "staff/khach-hang";
    }

    @GetMapping("/staff/dich-vu-thanh-toan")
    public String servicePayment() {
        return "staff/dich-vu-thanh-toan";
    }
}
