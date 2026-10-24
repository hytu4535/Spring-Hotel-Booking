package com.hotel.controller.admin;
import com.hotel.model.DichVu;
import com.hotel.service.DichVuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping({"/admin/dich-vu","/admin/dichvu","/admin/quan-ly-dich-vu"})
public class DichVuAdminController {
 private final DichVuService service;
 public DichVuAdminController(DichVuService service){this.service=service;}
 @GetMapping public String list(@RequestParam(required=false) String tenDv,Model m){m.addAttribute("tenDv",tenDv==null?"":tenDv);m.addAttribute("dichVuList",service.search(tenDv));return "quan-ly-dich-vu";}
 @GetMapping({"/them","/moi"}) public String form(Model m){m.addAttribute("dichVu",new DichVu());m.addAttribute("formAction","/admin/dich-vu/them");return "form-dich-vu";}
 @PostMapping({"/them","/moi"}) public String create(@ModelAttribute DichVu v,RedirectAttributes a){service.save(v);a.addFlashAttribute("success","Lưu dịch vụ thành công.");return "redirect:/admin/dich-vu";}
 @GetMapping("/{id}/sua") public String edit(@PathVariable int id,Model m){m.addAttribute("dichVu",service.get(id));m.addAttribute("formAction","/admin/dich-vu/"+id+"/sua");return "form-dich-vu";}
 @PostMapping("/{id}/sua") public String update(@PathVariable int id,@ModelAttribute DichVu v,RedirectAttributes a){v.setMaDv(id);service.save(v);a.addFlashAttribute("success","Cập nhật dịch vụ thành công.");return "redirect:/admin/dich-vu";}
 @PostMapping("/{id}/xoa") public String delete(@PathVariable int id,RedirectAttributes a){service.delete(id);a.addFlashAttribute("success","Xóa dịch vụ thành công.");return "redirect:/admin/dich-vu";}
}
