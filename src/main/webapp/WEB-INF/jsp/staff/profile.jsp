<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ cá nhân - Hotel Booking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <style>
        :root {
            --primary: #0d6efd;
            --secondary: #6c757d;
            --success: #198754;
            --info: #0dcaf0;
            --warning: #ffc107;
            --danger: #dc3545;
            --light: #f8f9fa;
            --dark: #212529;
        }
        .sidebar {
            background-color: var(--dark);
            color: white;
            height: 100vh;
            position: fixed;
            top: 0;
            left: 0;
            width: 250px;
            overflow-y: auto;
            padding-top: 20px;
            z-index: 1000;
        }
        .sidebar a {
            color: white;
            text-decoration: none;
            padding: 12px 20px;
            display: block;
        }
        .sidebar a:hover {
            background-color: var(--primary);
        }
        .sidebar .active {
            background-color: var(--primary);
        }
        .main-content {
            margin-left: 250px;
            padding: 20px;
            min-height: 100vh;
            background-color: var(--light);
        }
        @media (max-width: 768px) {
            .sidebar {
                width: 100%;
                height: auto;
                position: relative;
            }
            .main-content {
                margin-left: 0;
            }
        }
        .profile-info {
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        .profile-info h3 {
            margin-top: 0;
            color: var(--dark);
            border-bottom: 2px solid var(--primary);
            padding-bottom: 10px;
        }
        .profile-item {
            display: flex;
            margin-bottom: 20px;
        }
        .profile-label {
            width: 150px;
            font-weight: bold;
            color: var(--dark);
        }
        .profile-value {
            flex: 1;
            color: var(--secondary);
        }
        .btn-profile {
            background-color: var(--primary);
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
        }
        .btn-profile:hover {
            background-color: #0b5ed7;
        }
        .btn-cancel {
            background-color: var(--secondary);
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            margin-left: 10px;
        }
        .btn-cancel:hover {
            background-color: #5c636a;
        }
        .form-section {
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .form-section h3 {
            margin-top: 0;
            color: var(--dark);
            border-bottom: 2px solid var(--primary);
            padding-bottom: 10px;
        }
    </style>
</head>
<body>

<!-- Sidebar -->
<div class="sidebar">
    <div class="position-sticky pt-4">
        <div class="header">
            <h3><i class="bi bi-hospital"></i> HotelBooking</h3>
            <p>${sessionScope.currentUser.hoTen} - ${sessionScope.currentUser.vaiTro == 'ADMIN' ? 'Quản trị' : 'Nhân viên'}</p>
        </div>

        <!-- Admin menus (only for ADMIN) -->
        <c:if test="${sessionScope.currentUser.vaiTro == 'ADMIN'}">
            <a class="${pageContext.request.servletPath == '/admin/dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard"><i class="bi bi-speedometer2"></i> Bảng điều khiển</a>
            <a class="${pageContext.request.servletPath == '/admin/tai-khoan' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/tai-khoan"><i class="bi bi-person-gear"></i> Quản lý tài khoản</a>
            <a class="${pageContext.request.servletPath == '/admin/phong' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/phong"><i class="bi bi-building"></i> Quản lý phòng</a>
            <a class="${pageContext.request.servletPath == '/admin/dich-vu' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dich-vu"><i class="bi bi-gear"></i> Quản lý dịch vụ</a>
            <a class="${pageContext.request.servletPath == '/admin/dat-phong' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dat-phong"><i class="bi bi-calendar-check"></i> Quản lý đặt phòng / đơn</a>
            <a class="${pageContext.request.servletPath == '/admin/doanh-thu' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/doanh-thu"><i class="bi bi-cash-stack"></i> Doanh thu</a>
            <a class="${pageContext.request.servletPath == '/admin/thong-ke' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/thong-ke"><i class="bi bi-bar-chart"></i> Thống kê</a>
        </c:if>

        <!-- Staff menus (for both ADMIN and NHANVIEN) -->
        <c:if test="${sessionScope.currentUser.vaiTro == 'ADMIN' || sessionScope.currentUser.vaiTro == 'NHANVIEN'}">
            <a class="${pageContext.request.servletPath == '/staff/nhan-tra-phong' ? 'active' : ''}" href="${pageContext.request.contextPath}/staff/nhan-tra-phong"><i class="bi bi-box-arrow-in-left"></i> Nhận / Trả phòng</a>
            <a class="${pageContext.request.servletPath == '/staff/dich-vu-thanh-toan' ? 'active' : ''}" href="${pageContext.request.contextPath}/staff/dich-vu-thanh-toan"><i class="bi bi-cash-coin"></i> Dịch vụ & Thanh toán</a>
            <a class="${pageContext.request.servletPath == '/profile' ? 'active' : ''}" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person"></i> Hồ sơ cá nhân</a>
            <a class="${pageContext.request.servletPath == '/doi-mat-khau' ? 'active' : ''}" href="${pageContext.request.contextPath}/doi-mat-khau"><i class="bi bi-key"></i> Đổi mật khẩu</a>
        </c:if>

        <hr class="dropdown-divider bg-light">
        <div class="mb-4 mt-auto p-4">
            <a class="btn btn-danger btn-block" href="${pageContext.request.contextPath}/logout">
                <i class="bi bi-box-arrow-left"></i> Đăng xuất
            </a>
        </div>
    </div>
</div>

<!-- Main content -->
<div class="main-content">
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h1 class="h2"><i class="bi bi-person"></i> Hồ sơ cá nhân</h1>
    </div>

    <!-- Thông báo -->
    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show">
            <i class="bi bi-check-circle me-2"></i>${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show">
            <i class="bi bi-exclamation-triangle me-2"></i>${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessages && !empty errorMessages}">
        <div class="alert alert-danger alert-dismissible fade show">
            <i class="bi bi-exclamation-triangle me-2"></i>Vui lòng sửa các lỗi được đánh dấu bên dưới.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="row">
        <div class="col-md-8">
            <div class="profile-info">
                <h3><i class="bi bi-person-circle"></i> Thông tin cá nhân</h3>
                <div class="profile-item">
                    <span class="profile-label">Họ tên:</span>
                    <span class="profile-value">${user.hoTen}</span>
                </div>
                <div class="profile-item">
                    <span class="profile-label">Tên đăng nhập:</span>
                    <span class="profile-value">${user.tenDangNhap}</span>
                </div>
                <div class="profile-item">
                    <span class="profile-label">Email:</span>
                    <span class="profile-value">${user.email}</span>
                </div>
                <div class="profile-item">
                    <span class="profile-label">Số điện thoại:</span>
                    <span class="profile-value"><c:if test="${not empty user.soDienThoai}">${user.soDienThoai}</c:if></span>
                </div>
                <div class="profile-item">
                    <span class="profile-label">Vai trò:</span>
                    <span class="profile-value">
                        <span class="badge bg-${user.vaiTro == 'ADMIN' ? 'primary' : 'success'}">
                            ${user.vaiTro == 'ADMIN' ? 'Quản trị' : 'Nhân viên'}
                        </span>
                    </span>
                </div>
                <div class="profile-item">
                    <span class="profile-label">Ngày tạo:</span>
                    <span class="profile-value"><fmt:formatDate value="${user.ngayTao}" pattern="dd/MM/yyyy HH:mm" /></span>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="form-section">
                <h3><i class="bi bi-pencil-square"></i> Cập nhật thông tin</h3>
                <form method="POST" action="${pageContext.request.contextPath}/staff/profile">
                    <div class="mb-3">
                        <label class="form-label">Họ tên</label>
                        <input type="text" name="hoTen"
                               class="form-control ${not empty errorHoTen ? 'is-invalid' : ''}"
                               value="${not empty hoTen ? hoTen : currentUser.hoTen}">
                        <c:if test="${not empty errorHoTen}">
                            <div class="invalid-feedback d-block">
                                ${errorHoTen}
                            </div>
                        </c:if>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Email</label>
                        <input type="email" name="email"
                               class="form-control ${not empty errorEmail ? 'is-invalid' : ''}"
                               value="${not empty email ? email : currentUser.email}">
                        <c:if test="${not empty errorEmail}">
                            <div class="invalid-feedback d-block">
                                ${errorEmail}
                            </div>
                        </c:if>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Số điện thoại</label>
                        <input type="tel" name="soDienThoai"
                               class="form-control ${not empty errorSoDienThoai ? 'is-invalid' : ''}"
                               value="${not empty soDienThoai ? soDienThoai : currentUser.soDienThoai}">
                        <c:if test="${not empty errorSoDienThoai}">
                            <div class="invalid-feedback d-block">
                                ${errorSoDienThoai}
                            </div>
                        </c:if>
                    </div>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-profile">Cập nhật</button>
                        <a href="${pageContext.request.contextPath}/staff/profile" class="btn btn-cancel">Hủy</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>