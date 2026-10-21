package com.hotel.controller.admin;

import com.hotel.dto.LoaiPhongRequestDTO;
import com.hotel.service.LoaiPhongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/loai-phong")
public class LoaiPhongAdminController {

    private final LoaiPhongService loaiPhongService;

    public LoaiPhongAdminController(LoaiPhongService loaiPhongService) {
        this.loaiPhongService = loaiPhongService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String tenLoai, Model model) {
        model.addAttribute("tenLoai", tenLoai == null ? "" : tenLoai);
        model.addAttribute("loaiPhongList", loaiPhongService.search(tenLoai));
        return "admin/quan-ly-loai-phong";
    }

    @GetMapping("/them")
    public String createForm(Model model) {
        model.addAttribute("formData", new LoaiPhongRequestDTO());
        model.addAttribute("isEdit", false);
        model.addAttribute("formAction", "/admin/loai-phong/them");
        return "admin/form-loai-phong";
    }

    @PostMapping("/them")
    public String create(@Valid @ModelAttribute("formData") LoaiPhongRequestDTO formData,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("fieldErrors", toFieldErrors(bindingResult));
            model.addAttribute("globalError", firstGlobalError(bindingResult));
            populateFormModel(model, false, null);
            return "admin/form-loai-phong";
        }

        try {
            loaiPhongService.create(formData);
            redirectAttributes.addFlashAttribute("success", "Thêm loại phòng thành công.");
            return "redirect:/admin/loai-phong";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("tenLoai", "duplicate.tenLoai", ex.getMessage());
            model.addAttribute("fieldErrors", toFieldErrors(bindingResult));
            populateFormModel(model, false, null);
            return "admin/form-loai-phong";
        }
    }

    @GetMapping("/{maLoai}/sua")
    public String editForm(@PathVariable Integer maLoai,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("formData", loaiPhongService.getForEdit(maLoai));
            model.addAttribute("isEdit", true);
            model.addAttribute("maLoai", maLoai);
            model.addAttribute("formAction", "/admin/loai-phong/" + maLoai + "/sua");
            return "admin/form-loai-phong";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/loai-phong";
        }
    }

    @PostMapping("/{maLoai}/sua")
    public String update(@PathVariable Integer maLoai,
                         @Valid @ModelAttribute("formData") LoaiPhongRequestDTO formData,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("fieldErrors", toFieldErrors(bindingResult));
            model.addAttribute("globalError", firstGlobalError(bindingResult));
            populateFormModel(model, true, maLoai);
            return "admin/form-loai-phong";
        }

        try {
            loaiPhongService.update(maLoai, formData);
            redirectAttributes.addFlashAttribute("success", "Cập nhật loại phòng thành công.");
            return "redirect:/admin/loai-phong";
        } catch (IllegalArgumentException ex) {
            String message = ex.getMessage();
            if ("Tên loại phòng đã tồn tại.".equals(message)) {
                bindingResult.rejectValue("tenLoai", "duplicate.tenLoai", message);
            } else {
                bindingResult.reject("update.loaiPhong", message);
            }
            model.addAttribute("fieldErrors", toFieldErrors(bindingResult));
            model.addAttribute("globalError", firstGlobalError(bindingResult));
            populateFormModel(model, true, maLoai);
            return "admin/form-loai-phong";
        }
    }

    @PostMapping("/{maLoai}/xoa")
    public String delete(@PathVariable Integer maLoai, RedirectAttributes redirectAttributes) {
        try {
            loaiPhongService.delete(maLoai);
            redirectAttributes.addFlashAttribute("success", "Xóa loại phòng thành công.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/loai-phong";
    }

    private void populateFormModel(Model model, boolean isEdit, Integer maLoai) {
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("maLoai", maLoai);
        model.addAttribute("formAction", isEdit ? "/admin/loai-phong/" + maLoai + "/sua" : "/admin/loai-phong/them");
    }

    private Map<String, String> toFieldErrors(BindingResult bindingResult) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return errors;
    }

    private String firstGlobalError(BindingResult bindingResult) {
        if (bindingResult.getGlobalErrors().isEmpty()) {
            return null;
        }
        return bindingResult.getGlobalErrors().get(0).getDefaultMessage();
    }
}
