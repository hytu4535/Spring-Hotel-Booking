<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Báo cáo Thống kê - HotelBooking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css">
    <style>
        .main-content {
            margin-left: 250px;
            padding: 20px;
            min-height: 100vh;
            background-color: #f8f9fa;
        }
        .stats-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        }
        @media (max-width: 768px) {
            .main-content { margin-left: 0; }
        }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp">
    <jsp:param name="active" value="baocao" />
</jsp:include>

<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2><i class="fas fa-chart-bar me-2"></i>Báo cáo & Thống kê Tổng hợp</h2>
        <button class="btn btn-outline-primary" onclick="window.print()">
            <i class="fas fa-print me-1"></i> In Báo Cáo
        </button>
    </div>

    <!-- Tổng quan -->
    <div class="row mb-4">
        <div class="col-md-4 mb-3">
            <div class="stats-card">
                <h6 class="text-secondary">Tổng doanh thu kỳ báo cáo</h6>
                <h3 class="text-success fw-bold">
                    <fmt:formatNumber value="${tongDoanhThu}" type="number" groupingUsed="true"/> đ
                </h3>
                <small class="text-muted">Bao gồm tiền phòng và dịch vụ</small>
            </div>
        </div>
        <div class="col-md-4 mb-3">
            <div class="stats-card">
                <h6 class="text-secondary">Tổng số đơn đặt phòng</h6>
                <h3 class="text-primary fw-bold">${tongDonDat} đơn</h3>
                <small class="text-muted">Đã thanh toán / Hoàn tất</small>
            </div>
        </div>
        <div class="col-md-4 mb-3">
            <div class="stats-card">
                <h6 class="text-secondary">Tỷ lệ lấp đầy phòng</h6>
                <h3 class="text-info fw-bold">${tyLeLapDay}%</h3>
                <small class="text-muted">Hiệu suất khai thác trung bình</small>
            </div>
        </div>
    </div>

    <!-- Doanh thu theo tháng -->
    <div class="card shadow-sm mb-4">
        <div class="card-header bg-light">
            <h5 class="mb-0"><i class="fas fa-file-invoice-dollar me-2 text-success"></i>Báo cáo Doanh thu theo thời gian</h5>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                    <tr>
                        <th>Thời gian</th>
                        <th>Số đơn đặt</th>
                        <th>Doanh thu</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="m" items="${monthlyRevenue}">
                        <tr>
                            <td><strong>${m.thang}</strong></td>
                            <td>${m.soDon} đơn</td>
                            <td class="text-success fw-bold">
                                <fmt:formatNumber value="${m.doanhThu}" type="number" groupingUsed="true"/> đ
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="row">
        <!-- Dịch vụ sử dụng nhiều -->
        <div class="col-md-6 mb-4">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-light">
                    <h5 class="mb-0"><i class="fas fa-concierge-bell me-2 text-warning"></i>Dịch vụ được sử dụng nhiều</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead>
                            <tr>
                                <th>Tên dịch vụ</th>
                                <th>Số lượng</th>
                                <th>Doanh thu</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="s" items="${topServices}">
                                <tr>
                                    <td><strong>${s.tenDv}</strong></td>
                                    <td><span class="badge bg-secondary">${s.soLuong} lần</span></td>
                                    <td class="text-success fw-bold">
                                        <fmt:formatNumber value="${s.doanhThu}" type="number" groupingUsed="true"/> đ
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Phòng được đặt nhiều -->
        <div class="col-md-6 mb-4">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-light">
                    <h5 class="mb-0"><i class="fas fa-bed me-2 text-primary"></i>Phòng được đặt nhiều nhất</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead>
                            <tr>
                                <th>Số phòng</th>
                                <th>Loại phòng</th>
                                <th>Số lượt</th>
                                <th>Doanh thu</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="r" items="${topRooms}">
                                <tr>
                                    <td><strong>${r.soPhong}</strong></td>
                                    <td>${r.tenLoai}</td>
                                    <td><span class="badge bg-primary">${r.soLuotDat} lượt</span></td>
                                    <td class="text-success fw-bold">
                                        <fmt:formatNumber value="${r.doanhThu}" type="number" groupingUsed="true"/> đ
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
