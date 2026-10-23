<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tổng Quan Lễ Tân</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff-sidebar.css">
</head>
<body class="admin-operation-page">
<div class="admin-wrapper d-flex">
    <jsp:include page="/WEB-INF/views/common/sidebar-staff.jsp"><jsp:param name="active" value="overview"/></jsp:include>

    <div class="main-content w-100 p-4">
        <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
            <h3 class="mb-0 fw-bold"><i class="bi bi-speedometer2"></i> Việc Cần Làm Hôm Nay</h3>
            <span class="text-muted"><script>document.write(new Date().toLocaleDateString('vi-VN'));</script></span>
        </div>

        <div class="row g-4 mb-4">
            <!-- Đơn chờ xử lý -->
            <div class="col-md-4">
                <div class="card shadow-sm border-0 border-start border-warning border-4 h-100">
                    <div class="card-body">
                        <h6 class="text-warning fw-bold text-uppercase mb-1">Đơn Chờ Xác Nhận</h6>
                        <div class="h2 mb-0 fw-bold text-dark">5</div> <!-- Dữ liệu giả định B -->
                    </div>
                    <div class="card-footer bg-white border-0"><a href="${pageContext.request.contextPath}/staff/don" class="text-decoration-none text-muted small">Xem danh sách <i class="bi bi-arrow-right"></i></a></div>
                </div>
            </div>
            
            <!-- Cần Check-in -->
            <div class="col-md-4">
                <div class="card shadow-sm border-0 border-start border-success border-4 h-100">
                    <div class="card-body">
                        <h6 class="text-success fw-bold text-uppercase mb-1">Khách đến Check-in</h6>
                        <div class="h2 mb-0 fw-bold text-dark">12</div>
                    </div>
                    <div class="card-footer bg-white border-0"><a href="${pageContext.request.contextPath}/staff/nhan-tra-phong" class="text-decoration-none text-muted small">Đến sơ đồ phòng <i class="bi bi-arrow-right"></i></a></div>
                </div>
            </div>

            <!-- Cần Check-out -->
            <div class="col-md-4">
                <div class="card shadow-sm border-0 border-start border-danger border-4 h-100">
                    <div class="card-body">
                        <h6 class="text-danger fw-bold text-uppercase mb-1">Khách sắp Check-out</h6>
                        <div class="h2 mb-0 fw-bold text-dark">8</div>
                    </div>
                    <div class="card-footer bg-white border-0"><a href="${pageContext.request.contextPath}/staff/nhan-tra-phong" class="text-decoration-none text-muted small">Kiểm tra ngay <i class="bi bi-arrow-right"></i></a></div>
                </div>
            </div>
        </div>

        <div class="alert alert-info border-0 shadow-sm">
            <h5><i class="bi bi-megaphone-fill"></i> Thông báo nội bộ</h5>
            <ul class="mb-0">
                <li>Ưu tiên dọn dẹp các phòng Tầng 3 vì chiều nay có đoàn khách VIP (15:00).</li>
                <li>Nhớ ghi nhận mã nhân viên xử lý khi thực hiện xác nhận hoặc hủy đơn.</li>
            </ul>
        </div>
    </div>
</div>
</body></html>