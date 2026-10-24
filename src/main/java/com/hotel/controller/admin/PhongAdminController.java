package com.hotel.controller.admin;
import com.hotel.model.LoaiPhong;
import com.hotel.model.Phong;
import com.hotel.repository.LoaiPhongRepository;
import com.hotel.service.PhongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping({"/admin/phong","/admin/quan-ly-phong"})
public class PhongAdminController {
 private final PhongService service; private final LoaiPhongRepository loaiPhongRepository;
 public PhongAdminController(PhongService s,LoaiPhongRepository r){service=s;loaiPhongRepository=r;}
 @GetMapping public String list(Model m){m.addAttribute("phongList",service.findAll());return "quan-ly-phong";}
 @GetMapping({"/them","/moi"}) public String form(Model m){m.addAttribute("phong",new Phong());m.addAttribute("loaiPhongList",loaiPhongRepository.findAll());m.addAttribute("formAction","/admin/phong/them");return "form-phong";}
 @PostMapping({"/them","/moi"}) public String create(@RequestParam String soPhong,@RequestParam int maLoai,@RequestParam(required=false) Integer tang,@RequestParam(required=false) String trangThai,@RequestParam(required=false) String ghiChu,RedirectAttributes a){Phong p=new Phong();p.setSoPhong(soPhong);p.setLoaiPhong(loaiPhongRepository.findById(maLoai).orElseThrow());p.setTang(tang);p.setTrangThai(trangThai==null||trangThai.isEmpty()?"TRONG":trangThai);p.setGhiChu(ghiChu);service.save(p);a.addFlashAttribute("success","Lưu phòng thành công.");return "redirect:/admin/phong";}
 @GetMapping("/{id}/sua") public String edit(@PathVariable int id,Model m){m.addAttribute("phong",service.get(id));m.addAttribute("loaiPhongList",loaiPhongRepository.findAll());m.addAttribute("formAction","/admin/phong/"+id+"/sua");return "form-phong";}
 @PostMapping("/{id}/sua") public String update(@PathVariable int id,@RequestParam String soPhong,@RequestParam int maLoai,@RequestParam(required=false) Integer tang,@RequestParam String trangThai,@RequestParam(required=false) String ghiChu,RedirectAttributes a){Phong p=service.get(id);p.setSoPhong(soPhong);p.setLoaiPhong(loaiPhongRepository.findById(maLoai).orElseThrow());p.setTang(tang);p.setTrangThai(trangThai);p.setGhiChu(ghiChu);service.save(p);a.addFlashAttribute("success","Cập nhật phòng thành công.");return "redirect:/admin/phong";}
 @PostMapping("/{id}/xoa") public String delete(@PathVariable int id,RedirectAttributes a){service.delete(id);a.addFlashAttribute("success","Xóa phòng thành công.");return "redirect:/admin/phong";}
}
