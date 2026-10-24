package com.hotel.controller.admin;

import com.hotel.dto.LoaiPhongRequestDTO;
import com.hotel.dto.LoaiPhongResponseDTO;
import com.hotel.service.LoaiPhongService;
import jakarta.validation.Valid;
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
    public String showListPage(Model model) {
        List<LoaiPhongResponseDTO> dsLoaiPhong = loaiPhongService.getAllLoaiPhong();
        model.addAttribute("dsLoaiPhong", dsLoaiPhong);
        return "admin/loai-phong/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("loaiPhongForm", new LoaiPhongRequestDTO("", "", null, 1, null, ""));
        return "admin/loai-phong/form";
    }

    @PostMapping("/create")
    public String handleCreate(
            @Valid @ModelAttribute("loaiPhongForm") LoaiPhongRequestDTO request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/loai-phong/form";
        }

        try {
            loaiPhongService.createLoaiPhong(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới loại phòng thành công!");
            return "redirect:/admin/loai-phong";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/loai-phong/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            LoaiPhongResponseDTO response = loaiPhongService.getLoaiPhongById(id);

            LoaiPhongRequestDTO form = new LoaiPhongRequestDTO(
                    response.tenLoai(),
                    response.moTa(),
                    response.giaMacDinh(),
                    response.sucChua(),
                    response.dienTich(),
                    response.hinhAnh()
            );

            model.addAttribute("loaiPhongForm", form);
            model.addAttribute("loaiPhongId", id);
            return "admin/loai-phong/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/loai-phong";
        }
    }


    @PostMapping("/edit/{id}")
    public String handleUpdate(
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute("loaiPhongForm") LoaiPhongRequestDTO requestDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loaiPhongId", id);
            return "admin/loai-phong/form";
        }

        try {
            loaiPhongService.updateLoaiPhong(id, requestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật loại phòng thành công!");
            return "redirect:/admin/loai-phong";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("loaiPhongId", id);
            return "admin/loai-phong/form";
        }
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