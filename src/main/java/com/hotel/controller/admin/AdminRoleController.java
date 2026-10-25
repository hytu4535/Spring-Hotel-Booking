package com.hotel.controller.admin;

import com.hotel.model.SystemPermission;
import com.hotel.model.SystemRole;
import com.hotel.service.RolePermissionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Set;

@Controller
@RequestMapping("/admin/vai-tro")
public class AdminRoleController {
    private final RolePermissionService service;
    public AdminRoleController(RolePermissionService service) { this.service = service; }

    @GetMapping
    public String list(@RequestParam(required = false) Integer roleId, Model model) {
        java.util.List<SystemRole> roles = service.findRoles();
        SystemRole selected = roles.stream().filter(r -> roleId != null && r.getId().equals(roleId)).findFirst()
                .orElseGet(() -> roles.stream().filter(r -> "NHANVIEN".equals(r.getCode())).findFirst().orElse(roles.get(0)));
        model.addAttribute("roles", roles);
        model.addAttribute("permissions", service.findPermissions());
        model.addAttribute("selectedRole", selected);
        model.addAttribute("assignedCodes", service.permissionCodes(selected.getCode()));
        return "admin/vai-tro";
    }

    @PostMapping("/{roleId}")
    public String update(@PathVariable int roleId,
                         @RequestParam(required = false, defaultValue = "") String description,
                         @RequestParam(required = false, defaultValue = "false") boolean active,
                         @RequestParam(required = false) Set<String> permissions,
                         RedirectAttributes redirectAttributes) {
        try {
            service.updateRole(roleId, description, active, permissions == null ? Collections.emptySet() : permissions);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật mô tả, trạng thái và quyền truy cập của vai trò.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/vai-tro?roleId=" + roleId;
    }
}
