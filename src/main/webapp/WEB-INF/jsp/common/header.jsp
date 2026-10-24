<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title != null ? param.title : 'Hotel Booking'} - Hệ thống đặt phòng</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary sticky-top">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
            <i class="bi bi-building"></i> Hotel Booking
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <c:if test="${sessionScope.currentUser.vaiTro == 'CUSTOMER'}">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                            <i class="bi bi-person-circle"></i> ${sessionScope.currentUser.hoTen}
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/customer/profile">Hồ sơ của tôi</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/khach-hang/lich-su-don">Lịch sử đơn phòng</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">Đăng xuất</a></li>
                        </ul>
                    </li>
                </c:if>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/">Trang chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/tim-phong">Tìm phòng</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/tra-cuu-don">Tra cứu đơn</a>
                </li>
            </ul>
            <ul class="navbar-nav">
                <c:if test="${empty sessionScope.currentUser}">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/login">
                            <i class="bi bi-person"></i> Đăng nhập</a></li>
                </c:if>
            </ul>
        </div>
    </div>
</nav>
<main class="container my-4">