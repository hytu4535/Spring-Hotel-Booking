<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang chính Nhân viên - Hotel Booking</title>
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
    </style>
</head>
<body>

    <!-- Sidebar -->
    <div class="sidebar">
        <div class="position-sticky pt-4">
            <ul class="nav flex-column mb-6">
                <li class="nav-item">
                    <a class="nav-link text-white active" href="${pageContext.request.contextPath}/staff/quan-ly-don">
                        <i class="bi bi-speedometer2"></i> Trang chính
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${pageContext.request.contextPath}/staff/don">
                        <i class="bi bi-list-task"></i> Quản lý đơn đặt phòng
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${pageContext.request.contextPath}/staff/nhan-tra-phong">
                        <i class="bi bi-box-arrow-in-left"></i> Nhận / Trả phòng
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${pageContext.request.contextPath}/staff/dich-vu-thanh-toan">
                        <i class="bi bi-cash-coin"></i> Dịch vụ & Thanh toán
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${pageContext.request.contextPath}/staff/profile">
                        <i class="bi bi-person"></i> Hồ sơ cá nhân
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${pageContext.request.contextPath}/staff/doi-mat-khau">
                        <i class="bi bi-key"></i> Đổi mật khẩu
                    </a>
                </li>
            </ul>
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
            <h1 class="h2"><i class="bi bi-speedometer2"></i> Trang chính Nhân viên</h1>
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

        <!-- Dashboard content -->
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Chào mừng bạn đến với hệ thống quản lý khách sạn</h5>
                        <p class="card-text">
                            Đây là trang chính của nhân viên. Từ đây bạn có thể truy cập vào các chức năng:
                        </p>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item">
                                <i class="bi bi-list-task me-2"></i> Quản lý đơn đặt phòng: xem danh sách, duyệt, hủy đơn
                            </li>
                            <li class="list-group-item">
                                <i class="bi bi-box-arrow-in-left me-2"></i> Nhận phòng / Trả phòng: check-in / check-out
                            </li>
                            <li class="list-group-item">
                                <i class="bi bi-cash-coin me-2"></i> Dịch vụ & Thanh toán: thêm dịch vụ phát sinh, ghi nhận thanh toán, in hóa đơn
                            </li>
                            <li class="list-group-item">
                                <i class="bi bi-person me-2"></i> Hồ sơ cá nhân: cập nhật thông tin, đổi mật khẩu
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>