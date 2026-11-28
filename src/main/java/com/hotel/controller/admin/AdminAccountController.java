package com.hotel.controller.admin;

import com.hotel.dto.TaiKhoanAdminForm;
import com.hotel.model.TaiKhoan;
import com.hotel.service.TaiKhoanService;
import com.hotel.util.PasswordUtil;
import com.hotel.service.RolePermissionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataAccessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping({"/admin/tai-khoan", "/admin/nhan-vien", "/staff/tai-khoan"})
public class AdminAccountController {
    private final TaiKhoanService service;
    private final RolePermissionService rolePermissionService;

    public AdminAccountController(TaiKhoanService service, RolePermissionService rolePermissionService) {
        this.service = service;
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String tenDangNhap,
                       @RequestParam(required = false) String hoTen,
                       @RequestParam(required = false) String vaiTro,
                       @RequestParam(required = false) Integer trangThai,
                       @RequestParam(required = false, defaultValue = "date-desc") String sort,
                       @RequestParam(required = false, defaultValue = "active") String xoaFilter,
                       HttpServletRequest request, Model model) {
        if (!canManage(request, "ACCOUNT_MANAGE")) return "redirect:/login";
        model.addAttribute("taiKhoanList", service.search(tenDangNhap, hoTen, vaiTro, trangThai, sort, xoaFilter));
        model.addAttribute("filterTenDangNhap", tenDangNhap == null ? "" : tenDangNhap);
        model.addAttribute("filterHoTen", hoTen == null ? "" : hoTen);
        model.addAttribute("filterVaiTro", vaiTro == null ? "" : vaiTro);
        model.addAttribute("filterTrangThai", trangThai);
        model.addAttribute("filterSort", sort);
        model.addAttribute("filterXoa", xoaFilter);
        model.addAttribute("accountForm", new TaiKhoanAdminForm());
        model.addAttribute("staffView", request.getServletPath().startsWith("/staff/"));
        model.addAttribute("accountBasePath", request.getServletPath().startsWith("/staff/")
                ? "/staff/tai-khoan" : "/admin/tai-khoan");
        return "admin/taiKhoanManage";
    }

    @GetMapping("/{maTk}/sua")
    public String editForm(@PathVariable int maTk, HttpServletRequest request, Model model) {
        if (!canManage(request, "ACCOUNT_MANAGE_EDIT")) return "redirect:/login";
        TaiKhoan account = service.findById(maTk);
        if (account == null) {
            return "redirect:" + request.getRequestURI().replaceFirst("/\\d+/sua$", "");
        }
        model.addAttribute("account", account);
        model.addAttribute("accountForm", toForm(account));
        model.addAttribute("staffView", request.getServletPath().startsWith("/staff/"));
        model.addAttribute("accountBasePath", request.getServletPath().startsWith("/staff/")
                ? "/staff/tai-khoan" : "/admin/tai-khoan");
        return "admin/form-tai-khoan";
    }

    @GetMapping("/tao")
    public String createForm(HttpServletRequest request) {
        return "redirect:" + basePath(request);
    }

    @PostMapping
    public String action(@RequestParam String action, @RequestParam(required = false) Integer maTk,
                         HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!canManage(request, "ACCOUNT_MANAGE")) return "redirect:/login";
        if (maTk != null && maTk == currentUser(request).getMaTk()
                && ("lock".equals(action) || "role".equals(action))) {
            redirectAttributes.addFlashAttribute("error", "Không thể khóa hoặc đổi quyền tài khoản đang đăng nhập.");
            return "redirect:" + request.getRequestURI();
        }

        if ("lock".equals(action) && maTk != null) {
            redirectAttributes.addFlashAttribute("success",
                    service.updateTrangThai(maTk, 0) ? "Đã khóa tài khoản." : "Không thể khóa tài khoản.");
        }
        if ("unlock".equals(action) && maTk != null) {
            redirectAttributes.addFlashAttribute("success",
                    service.updateTrangThai(maTk, 1) ? "Đã mở khóa tài khoản." : "Không thể mở khóa tài khoản.");
        }
        if ("role".equals(action) && maTk != null) {
            TaiKhoan user = service.findById(maTk);
            if (user != null) {
                user.setVaiTro(nextRole(user.getVaiTro()));
                service.update(user);
                redirectAttributes.addFlashAttribute("success",
                        "Đã chuyển vai trò tài khoản sang " + user.getVaiTro() + ".");
            }
        }
        if ("delete".equals(action) && maTk != null) {
            if (maTk == currentUser(request).getMaTk()) {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa mềm tài khoản đang đăng nhập.");
            } else {
                boolean deleted = service.softDelete(maTk);
                redirectAttributes.addFlashAttribute(
                        deleted ? "success" : "error",
                        deleted ? "Đã xóa mềm tài khoản." : "Không tìm thấy tài khoản.");
            }
        }
        return "redirect:" + request.getRequestURI();
    }

    @PostMapping("/tao")
    public String create(@Valid @ModelAttribute("accountForm") TaiKhoanAdminForm form,
                         org.springframework.validation.BindingResult result,
                         HttpServletRequest request, Model model,
                         RedirectAttributes redirectAttributes) {
        if (!canManage(request, "ACCOUNT_MANAGE")) return "redirect:/login";
        if (service.existsTenDangNhap(form.getTenDangNhap(), null)) {
            result.rejectValue("tenDangNhap", "duplicate", "Tên đăng nhập đã tồn tại.");
        }
        if (form.getMatKhau() == null || form.getMatKhau().isBlank()) {
            result.rejectValue("matKhau", "required", "Mật khẩu là bắt buộc khi tạo tài khoản.");
        }
        if (form.getMatKhau() != null && !form.getMatKhau().isBlank()
                && !form.getMatKhau().equals(form.getXacNhanMatKhau())) {
            result.rejectValue("xacNhanMatKhau", "password.mismatch", "Mật khẩu xác nhận không khớp.");
        }
        if (service.existsEmail(form.getEmail(), null)) {
            result.rejectValue("email", "duplicate", "Email đã tồn tại.");
        }
        if (form.getVaiTro() == null || form.getVaiTro().isBlank()) {
            result.rejectValue("vaiTro", "required", "Vui lòng chọn vai trò.");
        }
        if (result.hasErrors()) {
            populateListModel(request, model);
            model.addAttribute("accountForm", form);
            model.addAttribute("openCreateModal", true);
            return "admin/taiKhoanManage";
        }
        TaiKhoan created = new TaiKhoan();
        created.setTenDangNhap(form.getTenDangNhap().trim());
        created.setMatKhau(PasswordUtil.hash(form.getMatKhau()));
        created.setHoTen(form.getHoTen().trim());
        created.setEmail(form.getEmail().trim());
        created.setSoDienThoai(form.getSoDienThoai());
        created.setVaiTro(form.getVaiTro().trim().toUpperCase());
        created.setProvider("LOCAL");
        created.setTrangThai(1);
        service.save(created);
        redirectAttributes.addFlashAttribute("success", "Tạo tài khoản thành công.");
        return "redirect:" + basePath(request);
    }

    @PostMapping("/{maTk}/sua")
    public String edit(@PathVariable int maTk,
                       @Valid @ModelAttribute("accountForm") TaiKhoanAdminForm form,
                       org.springframework.validation.BindingResult result,
                       Model model,
                       HttpServletRequest request,
                       RedirectAttributes redirectAttributes) {
        if (!canManage(request, "ACCOUNT_MANAGE_EDIT")) return "redirect:/login";
        TaiKhoan account = service.findById(maTk);
        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "Tài khoản không tồn tại.");
            return "redirect:" + request.getRequestURI().replaceFirst("/\\d+/sua$", "");
        }
        if (service.existsEmail(form.getEmail(), maTk)) {
            result.rejectValue("email", "duplicate", "Email đã tồn tại.");
        }
        if (form.getMatKhau() != null && !form.getMatKhau().isBlank()
                && !form.getMatKhau().equals(form.getXacNhanMatKhau())) {
            result.rejectValue("xacNhanMatKhau", "password.mismatch", "Mật khẩu xác nhận không khớp.");
        }
        if (result.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("accountForm", form);
            model.addAttribute("staffView", request.getServletPath().startsWith("/staff/"));
            model.addAttribute("accountBasePath", basePath(request));
            return "admin/form-tai-khoan";
        }
        account.setHoTen(form.getHoTen().trim());
        account.setEmail(form.getEmail().trim());
        account.setSoDienThoai(form.getSoDienThoai() == null ? null : form.getSoDienThoai().trim());
        if (form.getMatKhau() != null && !form.getMatKhau().isBlank()) {
            account.setMatKhau(PasswordUtil.hash(form.getMatKhau()));
        }
        account.setProvider(account.getProvider() == null ? "LOCAL" : account.getProvider());
        try {
            service.update(account);
        } catch (DataAccessException ex) {
            result.reject("save.failed", "Không thể lưu tài khoản. Vui lòng kiểm tra dữ liệu và cấu trúc database.");
            model.addAttribute("account", account);
            model.addAttribute("accountForm", form);
            model.addAttribute("staffView", request.getServletPath().startsWith("/staff/"));
            model.addAttribute("accountBasePath", basePath(request));
            return "admin/form-tai-khoan";
        }
        redirectAttributes.addFlashAttribute("success", "Cập nhật tài khoản thành công.");
        return "redirect:" + basePath(request);
    }

    private void populateListModel(HttpServletRequest request, Model model) {
        String basePath = basePath(request);
        model.addAttribute("taiKhoanList", service.search(
                request.getParameter("tenDangNhap"),
                request.getParameter("hoTen"),
                request.getParameter("vaiTro"),
                parseStatus(request.getParameter("trangThai")),
                request.getParameter("sort") == null ? "date-desc" : request.getParameter("sort"),
                request.getParameter("xoaFilter") == null ? "active" : request.getParameter("xoaFilter")));
        model.addAttribute("filterTenDangNhap", request.getParameter("tenDangNhap"));
        model.addAttribute("filterHoTen", request.getParameter("hoTen"));
        model.addAttribute("filterVaiTro", request.getParameter("vaiTro"));
        model.addAttribute("filterTrangThai", parseStatus(request.getParameter("trangThai")));
        model.addAttribute("filterSort", request.getParameter("sort"));
        model.addAttribute("filterXoa", request.getParameter("xoaFilter") == null
                ? "active" : request.getParameter("xoaFilter"));
        model.addAttribute("staffView", basePath.startsWith("/staff/"));
        model.addAttribute("accountBasePath", basePath);
    }

    private Integer parseStatus(String value) {
        return value == null || value.isBlank() ? null : Integer.valueOf(value);
    }

    private TaiKhoanAdminForm toForm(TaiKhoan account) {
        TaiKhoanAdminForm form = new TaiKhoanAdminForm();
        form.setTenDangNhap(account.getTenDangNhap());
        form.setHoTen(account.getHoTen());
        form.setEmail(account.getEmail());
        form.setSoDienThoai(account.getSoDienThoai());
        form.setVaiTro(account.getVaiTro());
        return form;
    }

    private String basePath(HttpServletRequest request) {
        return request.getServletPath().startsWith("/staff/")
                ? "/staff/tai-khoan" : "/admin/tai-khoan";
    }

    private TaiKhoan currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (TaiKhoan) session.getAttribute("currentUser");
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        TaiKhoan user = session == null ? null : (TaiKhoan) session.getAttribute("currentUser");
        return user != null && "ADMIN".equals(user.getVaiTro());
    }

    private boolean canManage(HttpServletRequest request, String permission) {
        HttpSession session = request.getSession(false);
        TaiKhoan user = session == null ? null : (TaiKhoan) session.getAttribute("currentUser");
        return user != null && rolePermissionService.hasPermission(user.getVaiTro(), permission);
    }

    private String nextRole(String currentRole) {
        if ("ADMIN".equalsIgnoreCase(currentRole)) return "NHANVIEN";
        if ("NHANVIEN".equalsIgnoreCase(currentRole)
                || "STAFF".equalsIgnoreCase(currentRole)
                || "EMPLOYEE".equalsIgnoreCase(currentRole)) {
            return "CUSTOMER";
        }
        return "ADMIN";
    }
}
