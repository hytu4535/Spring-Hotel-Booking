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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css">
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
<jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp">
    <jsp:param name="active" value="loaiphong" />
</jsp:include>

<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0"><i class="fas fa-layer-group me-2"></i>Quản lý loại phòng</h2>
    </div>

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="card shadow-sm mb-3">
        <div class="card-header bg-white">
            <i class="fas fa-filter text-primary me-2"></i><strong>Bộ lọc</strong>
        </div>
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/admin/loai-phong" class="row g-2">
                <div class="col-md-5">
                    <input name="tenLoai" value="${tenLoai}" class="form-control" placeholder="Tên loại phòng">
                </div>
                <div class="col-auto">
                    <button type="submit" class="btn btn-primary"><i class="fas fa-search me-1"></i>Tra cứu</button>
                </div>
                <div class="col-auto">
                    <a href="${pageContext.request.contextPath}/admin/loai-phong" class="btn btn-outline-secondary">
                        <i class="fas fa-rotate-left me-1"></i>Xóa lọc
                    </a>
                </div>
                <div class="col-auto">
                    <a href="${pageContext.request.contextPath}/admin/loai-phong/them" class="btn btn-success">
                        <i class="fas fa-plus me-1"></i>Thêm loại phòng
                    </a>
                </div>
            </form>
        </div>
    </div>

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