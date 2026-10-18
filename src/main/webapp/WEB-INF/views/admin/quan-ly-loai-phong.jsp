<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý loại phòng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary: #0d6efd;
            --light: #f8f9fa;
            --dark: #212529;
            --sidebar-width: 250px;
        }
        body { background-color: var(--light); }
        .sidebar {
            background-color: var(--dark);
            color: white;
            height: 100vh;
            position: fixed;
            top: 0;
            left: 0;
            width: var(--sidebar-width);
            display: flex;
            flex-direction: column;
            z-index: 1000;
        }
        .sidebar .header {
            padding: 20px;
            text-align: center;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        .sidebar-menu { flex: 1; overflow-y: auto; }
        .sidebar a {
            color: white;
            text-decoration: none;
            padding: 12px 20px;
            display: block;
        }
        .sidebar a:hover, .sidebar a.active { background-color: var(--primary); }
        .sidebar-footer { padding: 15px 20px; border-top: 1px solid rgba(255,255,255,0.1); }
        .main-content {
            margin-left: var(--sidebar-width);
            padding: 20px;
            min-height: 100vh;
        }
        @media (max-width: 768px) {
            .sidebar { width: 100%; height: auto; position: relative; }
            .main-content { margin-left: 0; }
            .sidebar .header { display: none; }
        }
    </style>
</head>
<body class="bg-light">
<div class="sidebar">
    <div class="header">
        <h3><i class="fas fa-hotel"></i> HotelBooking</h3>
        <p class="mb-0 small">${sessionScope.currentUser.hoTen} - Quản trị</p>
    </div>
    <div class="sidebar-menu">
        <a href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-tachometer-alt me-2"></i>Bảng điều khiển</a>
        <a href="${pageContext.request.contextPath}/admin/tai-khoan"><i class="fas fa-user-cog me-2"></i>Quản lý tài khoản</a>
        <a class="active" href="${pageContext.request.contextPath}/admin/loai-phong"><i class="fas fa-layer-group me-2"></i>Quản lý loại phòng</a>
        <a href="${pageContext.request.contextPath}/admin/phong"><i class="fas fa-bed me-2"></i>Quản lý phòng</a>
        <a href="${pageContext.request.contextPath}/admin/dich-vu"><i class="fas fa-concierge-bell me-2"></i>Quản lý dịch vụ</a>
    </div>
    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm w-100">
            <i class="fas fa-sign-out-alt"></i> Đăng xuất
        </a>
    </div>
</div>

<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0"><i class="fas fa-layer-group me-2"></i>Quản lý loại phòng</h2>
        <a href="${pageContext.request.contextPath}/admin/loai-phong/them" class="btn btn-primary">
            <i class="fas fa-plus me-1"></i> Thêm loại phòng
        </a>
    </div>

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="card shadow-sm">
        <div class="card-header bg-white fw-semibold">
            Danh sách loại phòng
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-striped table-hover align-middle mb-0">
                    <thead class="table-dark">
                    <tr>
                        <th>Mã</th>
                        <th>Tên loại</th>
                        <th>Giá mặc định</th>
                        <th>Sức chứa</th>
                        <th>Diện tích (m²)</th>
                        <th>Số phòng đang dùng</th>
                        <th>Hình ảnh</th>
                        <th style="width: 140px;">Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="item" items="${loaiPhongList}">
                        <tr>
                            <td>${item.maLoai}</td>
                            <td>
                                <div class="fw-semibold">${item.tenLoai}</div>
                                <div class="text-muted small">${item.moTa}</div>
                            </td>
                            <td><fmt:formatNumber value="${item.giaMacDinh}" type="number"/> VND</td>
                            <td>${item.sucChua}</td>
                            <td>${item.dienTich}</td>
                            <td>
                                <span class="badge ${item.soLuongPhong > 0 ? 'bg-warning text-dark' : 'bg-success'}">
                                        ${item.soLuongPhong}
                                </span>
                            </td>
                            <td>${item.hinhAnh}</td>
                            <td>
                                <div class="d-flex gap-1">
                                    <a class="btn btn-sm btn-outline-primary"
                                       href="${pageContext.request.contextPath}/admin/loai-phong/${item.maLoai}/sua">
                                        <i class="fas fa-pen"></i>
                                    </a>
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/admin/loai-phong/${item.maLoai}/xoa"
                                          onsubmit="return confirm('Bạn có chắc muốn xóa loại phòng này?');">
                                        <button type="submit" class="btn btn-sm btn-outline-danger">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty loaiPhongList}">
                        <tr>
                            <td colspan="8" class="text-center text-muted py-4">Chưa có loại phòng nào.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>