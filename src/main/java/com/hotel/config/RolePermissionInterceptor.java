package com.hotel.config;

import com.hotel.model.TaiKhoan;
import com.hotel.service.RolePermissionService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RolePermissionInterceptor implements HandlerInterceptor {
    private final RolePermissionService service;
    public RolePermissionInterceptor(RolePermissionService service) { this.service = service; }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();
        if (!(path.startsWith("/admin/") || path.startsWith("/staff/") || path.startsWith("/customer/"))) return true;
        if ("/admin/login".equals(path)) return true;
        TaiKhoan user = request.getSession(false) == null ? null
                : (TaiKhoan) request.getSession(false).getAttribute("currentUser");
        if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return false; }
        if (!service.canAccess(user.getVaiTro(), path)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập chức năng này.");
            return false;
        }
        return true;
    }
}
