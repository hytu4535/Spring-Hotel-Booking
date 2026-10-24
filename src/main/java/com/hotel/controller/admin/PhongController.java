package com.hotel.controller.admin;

import com.hotel.dto.PhongRequestDTO;
import com.hotel.dto.PhongResponseDTO;
import com.hotel.service.LoaiPhongService;
import com.hotel.service.PhongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/phong")
@RequiredArgsConstructor
public class PhongController {

    private final PhongService phongService;
    private final LoaiPhongService loaiPhongService;

    @GetMapping
    public String showListPage(Model model) {
        List<PhongResponseDTO> dsPhong = phongService.getAllPhong();
        model.addAttribute("dsPhong", dsPhong);
        // Lấy danh sách loại phòng để render dropdown select
        model.addAttribute("dsLoaiPhong", loaiPhongService.getAllLoaiPhong());
        return "admin/phong/list";
    }

    @PostMapping("/create")
    public String handleCreate(
            @Valid @ModelAttribute PhongRequestDTO request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dữ liệu nhập vào không hợp lệ!");
            return "redirect:/admin/phong";
        }

        try {
            phongService.createPhong(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới phòng thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/phong";
    }

    @PostMapping("/edit/{id}")
    public String handleUpdate(
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute PhongRequestDTO request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dữ liệu cập nhật không hợp lệ!");
            return "redirect:/admin/phong";
        }

        try {
            phongService.updatePhong(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật phòng thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/phong";
    }

    @PostMapping("/delete/{id}")
    public String handleDelete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            phongService.deletePhong(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa phòng thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/phong";
    }
}