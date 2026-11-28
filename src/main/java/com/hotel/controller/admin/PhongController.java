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
    public String showListPage(@RequestParam(name = "soPhong", required = false) String soPhong, Model model) {
        prepareListModel(soPhong, model);
        model.addAttribute("phongForm", new PhongRequestDTO(null, null, null, null));
        model.addAttribute("formAction", "/admin/phong/create");
        model.addAttribute("openPhongModal", false);
        return "admin/phong/list";
    }

    @PostMapping("/create")
    public String handleCreate(
            @Valid @ModelAttribute("phongForm") PhongRequestDTO request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareListModel(null, model);
            model.addAttribute("formAction", "/admin/phong/create");
            model.addAttribute("openPhongModal", true);
            return "admin/phong/list";
        }

        try {
            phongService.createPhong(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới phòng thành công!");
        } catch (RuntimeException e) {
            if (!addBusinessFieldError(bindingResult, e)) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/admin/phong";
            }
            prepareListModel(null, model);
            model.addAttribute("formAction", "/admin/phong/create");
            model.addAttribute("openPhongModal", true);
            return "admin/phong/list";
        }
        return "redirect:/admin/phong";
    }

    @PostMapping("/edit/{id}")
    public String handleUpdate(
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute("phongForm") PhongRequestDTO request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareListModel(null, model);
            model.addAttribute("formAction", "/admin/phong/edit/" + id);
            model.addAttribute("openPhongModal", true);
            return "admin/phong/list";
        }

        try {
            phongService.updatePhong(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật phòng thành công!");
        } catch (RuntimeException e) {
            if (!addBusinessFieldError(bindingResult, e)) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/admin/phong";
            }
            prepareListModel(null, model);
            model.addAttribute("formAction", "/admin/phong/edit/" + id);
            model.addAttribute("openPhongModal", true);
            return "admin/phong/list";
        }
        return "redirect:/admin/phong";
    }

    private void prepareListModel(String soPhong, Model model) {
        List<PhongResponseDTO> dsPhong = phongService.searchPhong(soPhong);
        model.addAttribute("dsPhong", dsPhong);
        model.addAttribute("dsLoaiPhong", loaiPhongService.getAllLoaiPhong());
    }

    private boolean addBusinessFieldError(BindingResult bindingResult, RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null) {
            return false;
        }
        if (message.contains("phòng")) {
            bindingResult.rejectValue("soPhong", "duplicate", message);
            return true;
        }
        if (message.contains("Loại phòng")) {
            bindingResult.rejectValue("maLoaiPhong", "invalid", message);
            return true;
        }
        return false;
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