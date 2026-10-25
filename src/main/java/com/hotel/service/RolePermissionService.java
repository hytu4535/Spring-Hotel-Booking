package com.hotel.service;

import com.hotel.model.RolePermission;
import com.hotel.model.SystemPermission;
import com.hotel.model.SystemRole;
import com.hotel.repository.RolePermissionRepository;
import com.hotel.repository.SystemPermissionRepository;
import com.hotel.repository.SystemRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service("rolePermissionService")
public class RolePermissionService {
    private static final List<String> FIXED_ROLES = Arrays.asList("ADMIN", "NHANVIEN", "CUSTOMER");
    private static final Set<String> BUSINESS_CODES = new LinkedHashSet<>(Arrays.asList(
            "DASHBOARD", "BOOKING_MANAGE", "CHECKIN_OUT", "SERVICE_PAYMENT", "ROOM_MANAGE",
            "ROOM_TYPE_MANAGE", "SERVICE_MANAGE", "CUSTOMER_MANAGE", "ACCOUNT_MANAGE", "ROLE_MANAGE"));

    private final SystemRoleRepository roleRepository;
    private final SystemPermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RolePermissionService(SystemRoleRepository roleRepository, SystemPermissionRepository permissionRepository,
                                 RolePermissionRepository rolePermissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @PostConstruct
    @Transactional
    public void seedSystemRoles() {
        migrateOldPermissionMatrix();
        ensureRole("ADMIN", "Quản trị viên", "Toàn quyền quản trị hệ thống", true);
        ensureRole("NHANVIEN", "Nhân viên", "Vận hành nghiệp vụ khách sạn", true);
        ensureRole("CUSTOMER", "Khách hàng", "Đặt phòng và quản lý thông tin cá nhân", true);
        ensurePermission("DASHBOARD", "Dashboard", "Xem tổng quan và dashboard nghiệp vụ");
        ensurePermission("BOOKING_MANAGE", "Quản lý đặt phòng", "Xem, xác nhận và cập nhật đơn đặt phòng");
        ensurePermission("CHECKIN_OUT", "Nhận / trả phòng", "Xem sơ đồ phòng và thực hiện check-in, check-out");
        ensurePermission("SERVICE_PAYMENT", "Dịch vụ & thanh toán", "Ghi nhận dịch vụ phát sinh và thanh toán");
        ensurePermission("ROOM_MANAGE", "Quản lý phòng", "Thêm, sửa, xóa và cập nhật trạng thái phòng");
        ensurePermission("ROOM_TYPE_MANAGE", "Quản lý loại phòng", "Quản lý danh mục loại phòng và giá");
        ensurePermission("SERVICE_MANAGE", "Quản lý dịch vụ", "Quản lý danh mục dịch vụ khách sạn");
        ensurePermission("CUSTOMER_MANAGE", "Quản lý khách hàng", "Quản lý hồ sơ và lịch sử khách hàng");
        ensurePermission("ACCOUNT_MANAGE", "Quản lý tài khoản", "Quản lý tài khoản nhân viên và trạng thái tài khoản");
        ensurePermission("ROLE_MANAGE", "Quản lý vai trò", "Thiết lập quyền truy cập chức năng");

        for (String code : BUSINESS_CODES) assignIfMissing("ADMIN", code);
        for (String code : Arrays.asList("DASHBOARD", "BOOKING_MANAGE", "CHECKIN_OUT", "SERVICE_PAYMENT")) assignIfMissing("NHANVIEN", code);
        rolePermissionRepository.deleteByRoleCode("CUSTOMER");
    }

    private void migrateOldPermissionMatrix() {
        Set<String> existing = permissionRepository.findAll().stream().map(SystemPermission::getCode).collect(Collectors.toSet());
        if (!existing.isEmpty() && !existing.equals(BUSINESS_CODES)) {
            rolePermissionRepository.deleteAll();
            permissionRepository.deleteAll();
        }
    }

    private void ensureRole(String code, String name, String description, boolean active) {
        SystemRole role = roleRepository.findByCode(code).orElseGet(SystemRole::new);
        role.setCode(code); role.setDisplayName(name); role.setDescription(description);
        if (role.getId() == null) role.setActive(active);
        roleRepository.save(role);
    }

    private void ensurePermission(String code, String name, String description) {
        SystemPermission permission = permissionRepository.findByCode(code).orElseGet(SystemPermission::new);
        permission.setCode(code); permission.setDisplayName(name); permission.setDescription(description);
        permission.setUrlPattern(code);
        permissionRepository.save(permission);
    }

    private void assignIfMissing(String roleCode, String permissionCode) {
        if (rolePermissionRepository.findByRoleCodeAndPermissionCode(roleCode, permissionCode).isPresent()) return;
        SystemRole role = roleRepository.findByCode(roleCode).orElseThrow(() -> new IllegalStateException("Thiếu role " + roleCode));
        SystemPermission permission = permissionRepository.findByCode(permissionCode).orElseThrow(() -> new IllegalStateException("Thiếu quyền " + permissionCode));
        RolePermission link = new RolePermission(); link.setRole(role); link.setPermission(permission); rolePermissionRepository.save(link);
    }

    @Transactional(readOnly = true) public List<SystemRole> findRoles() { return roleRepository.findAll(); }
    @Transactional(readOnly = true) public List<SystemPermission> findPermissions() { return permissionRepository.findAllByOrderByCodeAsc(); }
    @Transactional(readOnly = true) public Set<String> permissionCodes(String roleCode) {
        return rolePermissionRepository.findByRoleCode(roleCode).stream().map(x -> x.getPermission().getCode()).collect(Collectors.toSet());
    }
    @Transactional(readOnly = true) public boolean hasPermission(String roleCode, String code) {
        return "ADMIN".equals(roleCode) ? BUSINESS_CODES.contains(code) : permissionCodes(roleCode).contains(code);
    }
    public boolean hasPermission(String code) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpSession session = attributes == null ? null : attributes.getRequest().getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) return false;
        com.hotel.model.TaiKhoan user = (com.hotel.model.TaiKhoan) session.getAttribute("currentUser");
        return hasPermission(user.getVaiTro(), code);
    }

    @Transactional(readOnly = true)
    public boolean canAccess(String roleCode, String path) {
        SystemRole role = roleRepository.findByCode(roleCode).orElse(null);
        if (role == null || !role.isActive()) return false;
        if (path.endsWith("/profile") || path.endsWith("/doi-mat-khau")) return true;
        if ("CUSTOMER".equals(roleCode)) return path.startsWith("/customer/");
        String code = featureForPath(path);
        return code != null && hasPermission(roleCode, code);
    }

    private String featureForPath(String path) {
        if (path.equals("/admin/vai-tro") || path.startsWith("/admin/vai-tro/")) return "ROLE_MANAGE";
        if (path.startsWith("/admin/tai-khoan")) return "ACCOUNT_MANAGE";
        if (path.startsWith("/admin/khach-hang") || path.startsWith("/staff/khach-hang")) return "CUSTOMER_MANAGE";
        if (path.startsWith("/admin/loai-phong") || path.startsWith("/staff/loai-phong")) return "ROOM_TYPE_MANAGE";
        if (path.startsWith("/admin/phong") || path.startsWith("/staff/phong")) return "ROOM_MANAGE";
        if (path.startsWith("/staff/dich-vu-thanh-toan")) return "SERVICE_PAYMENT";
        if (path.startsWith("/admin/dich-vu") || path.startsWith("/staff/dich-vu")) return "SERVICE_MANAGE";
        if (path.startsWith("/admin/dat-phong") || path.startsWith("/staff/don")) return "BOOKING_MANAGE";
        if (path.startsWith("/admin/nhan-tra-phong") || path.startsWith("/staff/nhan-tra-phong")) return "CHECKIN_OUT";
        if (path.equals("/admin/dashboard") || path.equals("/staff/quan-ly-don")) return "DASHBOARD";
        return null;
    }

    @Transactional
    public void updateRole(int roleId, String description, boolean active, Set<String> selected) {
        SystemRole role = roleRepository.findById(roleId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò."));
        if (!FIXED_ROLES.contains(role.getCode())) throw new IllegalArgumentException("Vai trò không hợp lệ.");
        if ("CUSTOMER".equals(role.getCode())) throw new IllegalArgumentException("CUSTOMER là vai trò khách hàng, không cấu hình quyền quản trị.");
        role.setDescription(description == null ? "" : description.trim());
        role.setActive("ADMIN".equals(role.getCode()) || active);
        roleRepository.save(role);
        if ("ADMIN".equals(role.getCode())) return;
        rolePermissionRepository.deleteByRoleCode(role.getCode());
        if (selected != null) for (String code : selected) if (BUSINESS_CODES.contains(code)) assignIfMissing(role.getCode(), code);
    }
}
