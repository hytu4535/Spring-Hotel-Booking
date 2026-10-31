package com.hotel.controller.admin;

import com.hotel.dto.LoaiPhongRequestDTO;
import com.hotel.dto.LoaiPhongResponseDTO;
import com.hotel.service.LoaiPhongService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/loai-phong")
@RequiredArgsConstructor
public class LoaiPhongController {

    private final LoaiPhongService loaiPhongService;

    @GetMapping
    public String showListPage(@RequestParam(name = "tenLoai", required = false) String tenLoai, Model model) {
        List<LoaiPhongResponseDTO> dsLoaiPhong = loaiPhongService.searchLoaiPhong(tenLoai);
        model.addAttribute("dsLoaiPhong", dsLoaiPhong);
        return "admin/loai-phong/list";
    }

    @PostMapping("/create")
    public String handleCreate(
            @Valid @ModelAttribute LoaiPhongRequestDTO request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dữ liệu nhập vào không hợp lệ!");
            return "redirect:admin/loai-phong";
        }

        try {
            loaiPhongService.createLoaiPhong(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới loại phòng thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/loai-phong";
    }

    @PostMapping("/edit/{id}")
    public String handleUpdate(@PathVariable("id") Integer id,
                               @Valid @ModelAttribute LoaiPhongRequestDTO loaiPhongRequestDTO,
                               BindingResult bindingResult ,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dữ liệu cập nhật không hợp lệ!");
            return "redirect:/admin/loai-phong";
        }
        try {
            loaiPhongService.updateLoaiPhong(id, loaiPhongRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật loại phòng thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/loai-phong";
    }

    @PostMapping("/delete/{id}")
    public String handleDelete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            loaiPhongService.deleteLoaiPhong(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa loại phòng thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/loai-phong";
    }
}