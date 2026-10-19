<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đổi mật khẩu - Hotel Booking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff-sidebar.css">
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
        .change-password-form {
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            max-width: 400px;
            margin: 0 auto;
        }
        .change-password-form h3 {
            margin-top: 0;
            color: var(--dark);
            text-align: center;
            border-bottom: 2px solid var(--primary);
            padding-bottom: 10px;
        }
        .form-label {
            font-weight: bold;
            color: var(--dark);
        }
        .btn-change-password {
            background-color: var(--primary);
            color: white;
            border: none;
            padding: 12px;
            width: 100%;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
        }
        .btn-change-password:hover {
            background-color: #0b5ed7;
        }
        .btn-back {
            background-color: var(--secondary);
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
        }
        .btn-back:hover {
            background-color: #5c636a;
        }
    </style>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css">
</head>
<body>

<c:choose>
    <c:when test="${isAdminView}">
        <jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp">
            <jsp:param name="active" value="password"/>
        </jsp:include>
    </c:when>
    <c:otherwise>
        <jsp:include page="/WEB-INF/views/common/sidebar-staff.jsp">
            <jsp:param name="active" value="password"/>
        </jsp:include>
    </c:otherwise>
</c:choose>

<!-- Main content -->
<div class="main-content">
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h1 class="h2"><i class="bi bi-key"></i> Đổi mật khẩu</h1>
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

    <div class="change-password-form">
        <h3>Đổi mật khẩu</h3>
        <form method="POST" action="${pageContext.request.contextPath}${passwordBasePath}">
            <div class="mb-3">
                <label class="form-label">Mật khẩu cũ</label>
                <input type="password" class="form-control ${not empty errorMessages && errorMessages.containsKey('matKhauCu') ? 'is-invalid' : ''}" name="matKhauCu" value="${not empty param.matKhauCu ? param.matKhauCu : ''}" required>
                <div class="invalid-feedback">
                    ${errorMessages['matKhauCu']}
                </div>
            </div>
            <div class="mb-3">
                <label class="form-label">Mật khẩu mới</label>
                <input type="password" class="form-control ${not empty errorMessages && errorMessages.containsKey('matKhauMoi') ? 'is-invalid' : ''}" name="matKhauMoi" value="${not empty param.matKhauMoi ? param.matKhauMoi : ''}" required>
                <div class="invalid-feedback">
                    ${errorMessages['matKhauMoi']}
                </div>
            </div>
            <div class="mb-3">
                <label class="form-label">Xác nhận mật khẩu mới</label>
                <input type="password" class="form-control ${not empty errorMessages && errorMessages.containsKey('xacNhanMatKhauMoi') ? 'is-invalid' : ''}" name="xacNhanMatKhauMoi" value="${not empty param.xacNhanMatKhauMoi ? param.xacNhanMatKhauMoi : ''}" required>
                <div class="invalid-feedback">
                    ${errorMessages['xacNhanMatKhauMoi']}
                </div>
            </div>
            <div class="d-grid">
                <button type="submit" class="btn btn-change-password">Đổi mật khẩu</button>
            </div>
            <div class="mt-3 text-center">
                <button type="button" class="btn btn-back" onclick="window.history.back()">Quay lại</button>
            </div>
        </form>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>