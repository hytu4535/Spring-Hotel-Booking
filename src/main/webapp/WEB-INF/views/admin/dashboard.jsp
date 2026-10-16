<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bảng điều khiển Admin - HotelBooking</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
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
        .sidebar .header {
            padding: 20px;
            text-align: center;
            border-bottom: 1px solid rgba(255,255,255,0.1);
            margin-bottom: 20px;
        }
        .sidebar .header h3 {
            margin: 0;
            font-size: 1.5rem;
        }
        .main-content {
            margin-left: 250px;
            padding: 20px;
            min-height: 100vh;
            background-color: var(--light);
        }
        .stats-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .stats-card h3 {
            margin: 0 0 15px 0;
            color: var(--dark);
            font-size: 1.2rem;
        }
        .stats-card .value {
            font-size: 2.5rem;
            font-weight: bold;
            margin: 10px 0;
        }
        .stats-card .label {
            color: var(--secondary);
            font-size: 0.9rem;
        }
        .stats-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .btn-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            margin-right: 10px;
        }
        .btn-primary { background-color: var(--primary); border-color: var(--primary); }
        .btn-secondary { background-color: var(--secondary); border-color: var(--secondary); }
        .btn-success { background-color: var(--success); border-color: var(--success); }
        .btn-info { background-color: var(--info); border-color: var(--info); }
        .btn-warning { background-color: var(--warning); border-color: var(--warning); color: #212529; }
        .btn-danger { background-color: var(--danger); border-color: var(--danger); }
        .table-responsive {
            margin-top: 20px;
        }
        .welcome-message {
            background: linear-gradient(135deg, var(--primary), var(--secondary));
            color: white;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 30px;
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
            .sidebar .header {
                display: none;
            }
        }
    </style>
</head>
<body>
    <div class="sidebar">
        <div class="header">
            <h3><i class="fas fa-hotel"></i> HotelBooking</h3>
            <p>${sessionScope.currentUser.hoTen} - ${sessionScope.currentUser.vaiTro == 'ADMIN' ? 'Quản trị' : 'Nhân viên'}</p>
        </div>

        <!-- Admin menus (only for ADMIN) -->
        <c:if test="${sessionScope.currentUser.vaiTro == 'ADMIN'}">
            <a class="${pageContext.request.servletPath == '/admin/dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-tachometer-alt me-2"></i> Bảng điều khiển</a>
            <a class="${pageContext.request.servletPath == '/admin/tai-khoan' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/tai-khoan"><i class="fas fa-user-cog me-2"></i> Quản lý tài khoản</a>
            <a class="${pageContext.request.servletPath == '/admin/phong' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/phong"><i class="fas fa-bed me-2"></i> Quản lý phòng</a>
            <a class="${pageContext.request.servletPath == '/admin/dich-vu' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dich-vu"><i class="fas fa-hammer me-2"></i> Quản lý dịch vụ</a>
            <a class="${pageContext.request.servletPath == '/admin/dat-phong' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dat-phong"><i class="fas fa-clipboard-list me-2"></i> Quản lý đặt phòng / đơn</a>
            <a class="${pageContext.request.servletPath == '/admin/doanh-thu' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/doanh-thu"><i class="fas fa-file-invoice-dollar me-2"></i> Doanh thu</a>
            <a class="${pageContext.request.servletPath == '/admin/thong-ke' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/thong-ke"><i class="fas fa-chart-bar me-2"></i> Thống kê</a>
        </c:if>

        <!-- Staff menus (for both ADMIN and NHANVIEN) -->
        <c:if test="${sessionScope.currentUser.vaiTro == 'ADMIN' || sessionScope.currentUser.vaiTro == 'NHANVIEN'}">
            <a class="${pageContext.request.servletPath == '/staff/nhan-tra-phong' ? 'active' : ''}" href="${pageContext.request.contextPath}/staff/nhan-tra-phong"><i class="fas fa-box-open me-2"></i> Nhận / Trả phòng</a>
            <a class="${pageContext.request.servletPath == '/staff/dich-vu-thanh-toan' ? 'active' : ''}" href="${pageContext.request.contextPath}/staff/dich-vu-thanh-toan"><i class="fas fa-coins me-2"></i> Dịch vụ & Thanh toán</a>
            <a class="${pageContext.request.servletPath == '/staff/profile' ? 'active' : ''}" href="${pageContext.request.contextPath}/staff/profile"><i class="fas fa-user me-2"></i> Hồ sơ cá nhân</a>
            <a class="${pageContext.request.servletPath == '/staff/doi-mat-khau' ? 'active' : ''}" href="${pageContext.request.contextPath}/staff/doi-mat-khau"><i class="fas fa-key me-2"></i> Đổi mật khẩu</a>
        </c:if>

        <hr class="sidebar-divider">
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-icon w-100 mt-3">
            <i class="fas fa-sign-out-alt"></i> Đăng xuất
        </a>
    </div>

    <div class="main-content">
        <div class="welcome-message">
            <h2>Chào mừng, Admin!</h2>
            <p>Bạn đang xem bảng điều khiển quản lý hệ thống khách sạn HotelBooking</p>
        </div>

        <!-- Thống kê tổng quan -->
        <div class="stats-row">
            <div class="stats-card">
                <h3>Tổng số phòng</h3>
                <div class="value">128</div>
                <div class="label">Phòng trong khách sạn</div>
            </div>
            <div class="stats-card">
                <h3>Phòng đang có khách</h3>
                <div class="value" style="color: var(--danger);">45</div>
                <div class="label">Đã được đặt</div>
            </div>
            <div class="stats-card">
                <h3>Phòng trống</h3>
                <div class="value" style="color: var(--success);">83</div>
                <div class="label">Sẵn sàng cho đặt</div>
            </div>
            <div class="stats-card">
                <h3>Doanh thu hôm nay</h3>
                <div class="value" style="color: var(--info);">12,500,000đ</div>
                <div class="label">Đặt phòng & dịch vụ</div>
            </div>
        </div>

        <!-- Hoạt động gần đây -->
        <div class="card">
            <div class="card-header">
                <h5><i class="fas fa-clock"></i> Hoạt động gần đây</h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Thời gian</th>
                                <th>Hành động</th>
                                <th>Khách hàng</th>
                                <th>Phòng</th>
                                <th>Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>10:30</td>
                                <td><i class="fas fa-check-circle text-success"></i> Đặt phòng</td>
                                <td>Nguyễn Văn A</td>
                                <td>Phòng 305</td>
                                <td><span class="badge bg-success">Xác nhận</span></td>
                            </tr>
                            <tr>
                                <td>09:15</td>
                                <td><i class="fas fa-exchange-alt text-warning"></i> Đổi phòng</td>
                                <td>Trần Thị B</td>
                                <td>Phòng 210 → 215</td>
                                <td><span class="badge bg-warning">Chờ</span></td>
                            </tr>
                            <tr>
                                <td>08:45</td>
                                <td><i class="fas fa-user-times text-danger"></i> Hủy đặt</td>
                                <td>Lê Văn C</td>
                                <td>Phòng 108</td>
                                <td><span class="badge bg-danger">Hủy</span></td>
                            </tr>
                            <tr>
                                <td>08:30</td>
                                <td><i class="fas fa-bed text-info"></i> Nhận phòng</td