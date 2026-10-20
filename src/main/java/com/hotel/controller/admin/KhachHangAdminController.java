package com.hotel.controller.admin;

import com.hotel.dto.KhachHangProfileDTO;
import com.hotel.service.KhachHangService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/khach-hang")
public class KhachHangAdminController {
    private final KhachHangService khachHangService;

    public KhachHangAdminController(KhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("khachHangList", khachHangService.search(keyword));
        return "admin/quan-ly-khach-hang";
    }

    @GetMapping("/moi")
    public String createForm(Model model) {
        model.addAttribute("customerForm", new KhachHangProfileDTO());
        model.addAttribute("formTitle", "Thêm khách hàng");
        model.addAttribute("formAction", "/admin/khach-hang/moi");
        return "admin/form-khach-hang";
    }

    @PostMapping("/moi")
    public String create(@Valid @ModelAttribute("customerForm") KhachHangProfileDTO form,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Thêm khách hàng");
            model.addAttribute("formAction", "/admin/khach-hang/moi");
            return "admin/form-khach-hang";
        }
        try {
            khachHangService.createProfile(form);
            redirectAttributes.addFlashAttribute("success", "Đã thêm khách hàng.");
            return "redirect:/admin/khach-hang";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("formTitle", "Thêm khách hàng");
            model.addAttribute("formAction", "/admin/khach-hang/moi");
            return "admin/form-khach-hang";
        }
    }

    @GetMapping("/{maKh}")
    public String detail(@PathVariable int maKh, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("customer", khachHangService.getAdmin(maKh));
            model.addAttribute("bookingHistory", khachHangService.history(maKh));
            return "admin/chi-tiet-khach-hang";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/khach-hang";
        }
    }

    @GetMapping("/{maKh}/sua")
    public String editForm(@PathVariable int maKh, Model model, RedirectAttributes redirectAttributes) {
        try {
            com.hotel.dto.KhachHangAdminDTO customer = khachHangService.getAdmin(maKh);
            KhachHangProfileDTO form = new KhachHangProfileDTO();
            form.setHoTen(customer.getHoTen());
            form.setEmail(customer.getEmail());
            form.setSoDienThoai(customer.getSoDienThoai());
            form.setCccd(customer.getCccd());
            form.setDiaChi(customer.getDiaChi());
            model.addAttribute("customerForm", form);
            model.addAttribute("formTitle", "Cập nhật khách hàng");
            model.addAttribute("formAction", "/admin/khach-hang/" + maKh + "/sua");
            return "admin/form-khach-hang";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/khach-hang";
        }
    }

    @PostMapping("/{maKh}/sua")
    public String update(@PathVariable int maKh,
                         @Valid @ModelAttribute("customerForm") KhachHangProfileDTO form,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Cập nhật khách hàng");
            model.addAttribute("formAction", "/admin/khach-hang/" + maKh + "/sua");
            return "admin/form-khach-hang";
        }
        try {
            khachHangService.updateProfileByAdmin(maKh, form);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật thông tin khách hàng.");
            return "redirect:/admin/khach-hang/" + maKh;
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("formTitle", "Cập nhật khách hàng");
            model.addAttribute("formAction", "/admin/khach-hang/" + maKh + "/sua");
            return "admin/form-khach-hang";
        }
    }

    @PostMapping("/{maKh}/xoa")
    public String delete(@PathVariable int maKh, RedirectAttributes redirectAttributes) {
        try {
            khachHangService.deleteProfile(maKh);
            redirectAttributes.addFlashAttribute("success", "Đã xóa hồ sơ khách hàng.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/khach-hang";
    }

    @PostMapping("/{duplicateId}/gop")
    public String merge(@PathVariable int duplicateId, @RequestParam int primaryId,
                        RedirectAttributes redirectAttributes) {
        try {
            khachHangService.merge(primaryId, duplicateId);
            redirectAttributes.addFlashAttribute("success", "Đã gộp hồ sơ trùng và chuyển toàn bộ lịch sử đặt phòng.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/khach-hang";
    }
}
