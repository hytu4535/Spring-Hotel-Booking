<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="activeMenu" value="${empty param.active ? '' : param.active}" />

<aside class="staff-sidebar">
    <div class="staff-sidebar-header">
        <a href="${pageContext.request.contextPath}/staff/quan-ly-don" class="staff-brand">
            <i class="bi bi-building-check"></i>
            <span>HotelBooking</span>
        </a>
        <div class="staff-user">
            <i class="bi bi-person-circle"></i>
            <span>${sessionScope.currentUser.hoTen}</span>
        </div>
        <span class="staff-role">LỄ TÂN / NHÂN VIÊN</span>
    </div>

    <nav class="staff-sidebar-menu" aria-label="Điều hướng nghiệp vụ">
        <div class="staff-section-label">Vận hành hằng ngày</div>
        <a class="${activeMenu == 'overview' ? 'active' : ''}" href="${pageContext.request.contextPath}/staff/quan-ly-don">
            <i class="bi bi-house"></i><span>Tổng quan</span>
        </a>
        <a class="${activeMenu == 'reservations' ? 'active' : ''}" href="${pageContext.request.contextPath}/staff/don">
            <i class="bi bi-calendar-check"></i><span>Đặt phòng & đơn đặt</span>
        </a>
        <a class="${activeMenu == 'frontdesk' ? 'active' : ''}" href="${pageContext.request.contextPath}/staff/nhan-tra-phong">
            <i class="bi bi-person-workspace"></i><span>Quầy lễ tân</span>
        </a>

        <div class="staff-section-label staff-section-label-spaced">Tài khoản</div>
        <a class="${activeMenu == 'profile' ? 'active' : ''}" href="${pageContext.request.contextPath}/staff/profile">
            <i class="bi bi-person"></i><span>Hồ sơ cá nhân</span>
        </a>
        <a class="${activeMenu == 'password' ? 'active' : ''}" href="${pageContext.request.contextPath}/staff/doi-mat-khau">
            <i class="bi bi-key"></i><span>Đổi mật khẩu</span>
        </a>
    </nav>

    <div class="staff-sidebar-footer">
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger w-100">
            <i class="bi bi-box-arrow-right me-1"></i> Đăng xuất
        </a>
    </div>
</aside>
