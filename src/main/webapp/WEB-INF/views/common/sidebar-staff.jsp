<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="sidebar d-flex flex-column p-3">
    <!-- Logo & Tên khách sạn -->
    <a href="${pageContext.request.contextPath}/staff/don" class="d-flex align-items-center mb-3 mb-md-0 me-md-auto text-white text-decoration-none px-2">
        <i class="bi bi-building-check fs-3 me-2 text-primary"></i>
        <span class="fs-5 fw-bold">Hotel Admin</span>
    </a>
    <hr>
    
    <ul class="nav nav-pills flex-column mb-auto">
        
        <small class="text-muted text-uppercase px-3 mb-2 d-block" style="font-size: 12px;">Nghiệp vụ</small>
        
        <!-- Menu chung cho Nhân viên và Admin -->
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/staff/don" class="nav-link ${param.active == 'don' ? 'active' : ''}">
                <i class="bi bi-card-list"></i> Quản lý đơn đặt
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/staff/so-do" class="nav-link ${param.active == 'sodo' ? 'active' : ''}">
                <i class="bi bi-grid-3x3-gap"></i> Sơ đồ phòng
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/staff/dich-vu" class="nav-link ${param.active == 'dichvu' ? 'active' : ''}">
                <i class="bi bi-cart-plus"></i> Dịch vụ & Thanh toán
            </a>
        </li>

        <!-- Menu CHỈ dành cho Admin -->
        <c:if test="${sessionScope.currentUser.vaiTro == 'ADMIN'}">
            <hr>
            <small class="text-muted text-uppercase px-3 mb-2 d-block" style="font-size: 12px;">Quản trị hệ thống</small>
            <li>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link ${param.active == 'dashboard' ? 'active' : ''}">
                    <i class="bi bi-bar-chart-line"></i> Dashboard
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/phong" class="nav-link ${param.active == 'phong' ? 'active' : ''}">
                    <i class="bi bi-door-open"></i> Quản lý phòng
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/loai-phong" class="nav-link ${param.active == 'loaiphong' ? 'active' : ''}">
                    <i class="bi bi-tags"></i> Loại phòng
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/dich-vu" class="nav-link ${param.active == 'qlydichvu' ? 'active' : ''}">
                    <i class="bi bi-cup-hot"></i> Danh mục dịch vụ
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/nhan-vien" class="nav-link ${param.active == 'nhanvien' ? 'active' : ''}">
                    <i class="bi bi-people"></i> Quản lý nhân viên
                </a>
            </li>
        </c:if>
    </ul>
    
    <hr>
    
    <!-- User Profile Dropdown -->
    <div class="dropdown">
        <a href="#" class="d-flex align-items-center text-white text-decoration-none dropdown-toggle" id="dropdownUser" data-bs-toggle="dropdown" aria-expanded="false">
            <i class="bi bi-person-circle fs-4 me-2"></i>
            <strong>${sessionScope.currentUser.hoTen}</strong>
        </a>
        <ul class="dropdown-menu dropdown-menu-dark text-small shadow" aria-labelledby="dropdownUser">
            <li><span class="dropdown-item-text text-warning">Vai trò: ${sessionScope.currentUser.vaiTro}</span></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right"></i> Đăng xuất</a></li>
        </ul>
    </div>
</nav>