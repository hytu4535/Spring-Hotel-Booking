<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="activeMenu" value="${empty param.active ? '' : param.active}" />

<div class="sidebar">
    <div class="header">
        <h3><i class="fas fa-hotel"></i> HotelBooking</h3>
        <p class="mb-0 small">
            ${sessionScope.currentUser.hoTen} -
            ${sessionScope.currentUser.vaiTro == 'ADMIN' ? 'Quản trị' : 'Nhân viên'}
        </p>
    </div>

    <div class="sidebar-menu">
        <c:if test="${sessionScope.currentUser.vaiTro == 'ADMIN'}">
            <details class="menu-group" ${activeMenu == 'dashboard' || activeMenu == 'taikhoan' ? 'open' : ''}>
                <summary class="menu-group-toggle">
                    <span><i class="fas fa-sliders-h me-2"></i> Tổng quan hệ thống</span>
                    <i class="fas fa-chevron-right caret"></i>
                </summary>
                <div class="menu-group-items">
                    <a class="${activeMenu == 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">
                        <i class="fas fa-tachometer-alt me-2"></i> Bảng điều khiển
                    </a>
                    <a class="${activeMenu == 'taikhoan' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/tai-khoan">
                        <i class="fas fa-user-cog me-2"></i> Quản lý tài khoản
                    </a>
                </div>
            </details>

            <details class="menu-group" ${activeMenu == 'loaiphong' || activeMenu == 'phong' || activeMenu == 'dichvu' || activeMenu == 'datphong' || activeMenu == 'nhantraphong' || activeMenu == 'doanhthu' || activeMenu == 'thongke' ? 'open' : ''}>
                <summary class="menu-group-toggle">
                    <span><i class="fas fa-layer-group me-2"></i> Vận hành khách sạn</span>
                    <i class="fas fa-chevron-right caret"></i>
                </summary>
                <div class="menu-group-items">
                    <a class="${activeMenu == 'loaiphong' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/loai-phong">
                        <i class="fas fa-tags me-2"></i> Quản lý loại phòng
                    </a>
                    <a class="${activeMenu == 'phong' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/phong">
                        <i class="fas fa-bed me-2"></i> Quản lý phòng
                    </a>
                    <a class="${activeMenu == 'dichvu' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dich-vu">
                        <i class="fas fa-concierge-bell me-2"></i> Quản lý dịch vụ
                    </a>
                    <a class="${activeMenu == 'datphong' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dat-phong">
                        <i class="fas fa-clipboard-list me-2"></i> Quản lý đặt phòng
                    </a>
                    <a class="${activeMenu == 'nhantraphong' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/nhan-tra-phong">
                        <i class="fas fa-concierge-bell me-2"></i> Nhận / Trả phòng
                    </a>
                    <a class="${activeMenu == 'doanhthu' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/doanh-thu">
                        <i class="fas fa-file-invoice-dollar me-2"></i> Doanh thu
                    </a>
                    <a class="${activeMenu == 'thongke' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/thong-ke">
                        <i class="fas fa-chart-bar me-2"></i> Thống kê
                    </a>
                </div>
            </details>

            <details class="menu-group" ${activeMenu == 'profile' || activeMenu == 'password' ? 'open' : ''}>
                <summary class="menu-group-toggle">
                    <span><i class="fas fa-user-circle me-2"></i> Tài khoản cá nhân</span>
                    <i class="fas fa-chevron-right caret"></i>
                </summary>
                <div class="menu-group-items">
                    <a class="${activeMenu == 'profile' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/profile">
                        <i class="fas fa-id-card me-2"></i> Hồ sơ cá nhân
                    </a>
                    <a class="${activeMenu == 'password' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/doi-mat-khau">
                        <i class="fas fa-key me-2"></i> Đổi mật khẩu
                    </a>
                </div>
            </details>
        </c:if>

    </div>

    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm w-100">
            <i class="fas fa-sign-out-alt"></i> Đăng xuất
        </a>
    </div>
</div>
