<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bảng điều khiển Admin - HotelBooking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css">
    <style>
        :root {
            --primary: #0d6efd;
            --secondary: #6c757d;
            --success: #198754;
            --info: #0dcaf0;
            --warning: #ffc107;
            --danger: #dc3545;
            --light: #f8f9fa;
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
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        .stats-card h3 {
            margin: 0 0 15px 0;
            font-size: 1.1rem;
            color: var(--secondary);
        }

        .stats-card .value {
            font-size: 1.8rem;
            font-weight: bold;
            margin: 10px 0;
        }

        .stats-card .label {
            color: var(--secondary);
            font-size: 0.85rem;
        }

        .stats-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .welcome-message {
            background: linear-gradient(135deg, var(--primary), var(--secondary));
            color: white;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 30px;
        }

        @media (max-width: 768px) {
            .main-content {
                margin-left: 0;
            }
        }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp">
    <jsp:param name="active" value="dashboard" />
</jsp:include>

<div class="main-content">
    <div class="welcome-message">
        <h2>Chào mừng, Admin!</h2>
        <p>Thống kê tổng quan hệ thống khách sạn HotelBooking theo thời gian thực.</p>
    </div>

    <div class="stats-row">
        <div class="stats-card">
            <h3>Doanh thu hôm nay</h3>
            <div class="value text-success">
                <fmt:formatNumber value="${doanhThuHomNay}" type="number" groupingUsed="true"/> đ
            </div>
            <div class="label">Cập nhật trong ngày</div>
        </div>
        <div class="stats-card">
            <h3>Doanh thu tháng này</h3>
            <div class="value text-primary">
                <fmt:formatNumber value="${doanhThuThangNay}" type="number" groupingUsed="true"/> đ
            </div>
            <div class="label">Tổng doanh thu tháng</div>
        </div>
        <div class="stats-card">
            <h3>Tổng số đơn đặt</h3>
            <div class="value text-warning">${tongSoDon}</div>
            <div class="label">Đơn đặt phòng hợp lệ</div>
        </div>
        <div class="stats-card">
            <h3>Tỷ lệ lấp đầy</h3>
            <div class="value text-info">${thongKePhong.occupancyRate}%</div>
            <div class="label">Đang có khách / Tổng phòng (${thongKePhong.occupiedRooms}/${thongKePhong.totalRooms})</div>
        </div>
    </div>

    <div class="row mb-4">
        <div class="col-md-6 mb-3">
            <div class="stats-card h-100">
                <h3><i class="fas fa-door-open me-2 text-primary"></i>Tình trạng phòng</h3>
                <ul class="list-group list-group-flush mt-3">
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Tổng số phòng
                        <span class="badge bg-secondary rounded-pill">${thongKePhong.totalRooms}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Phòng đang có khách
                        <span class="badge bg-danger rounded-pill">${thongKePhong.occupiedRooms}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Phòng trống sẵn sàng
                        <span class="badge bg-success rounded-pill">${thongKePhong.availableRooms}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Phòng bảo trì
                        <span class="badge bg-warning text-dark rounded-pill">${thongKePhong.maintenanceRooms}</span>
                    </li>
                </ul>
            </div>
        </div>
        <div class="col-md-6 mb-3">
            <div class="stats-card h-100">
                <h3><i class="fas fa-chart-line me-2 text-success"></i>Chỉ số vận hành</h3>
                <div class="p-3">
                    <p class="mb-2"><strong>Hiệu suất khai thác phòng:</strong></p>
                    <div class="progress mb-4" style="height: 25px;">
                        <div class="progress-bar bg-success" role="progressbar" style="width: ${thongKePhong.occupancyRate}%;" aria-valuenow="${thongKePhong.occupancyRate}" aria-valuemin="0" aria-valuemax="100">${thongKePhong.occupancyRate}%</div>
                    </div>
                    <p class="text-muted small">Tỷ lệ lấp đầy tính dựa trên số phòng đang có khách ở thực tế so với tổng số phòng trong hệ thống khách sạn.</p>
                </div>
            </div>
        </div>
    </div>

    <div class="card">
        <div class="card-header">
            <h5 class="mb-0"><i class="fas fa-clock me-2"></i>Hoạt động gần đây</h5>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead>
                    <tr>
                        <th>Thời gian</th>
                        <th>Khách hàng</th>
                        <th>Phòng</th>
                        <th>Trạng thái đơn</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="hd" items="${hoatDongGganDay}">
                        <tr>
                            <td><fmt:formatDate value="${hd.ngayDat}" pattern="dd/MM/yyyy HH:mm" /></td>
                            <td><strong>${hd.hoTen}</strong></td>
                            <td>${hd.soPhong}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${hd.trangThai == 'DA_XAC_NHAN'}"><span class="badge bg-success">Đã xác nhận</span></c:when>
                                    <c:when test="${hd.trangThai == 'DA_NHAN_PHONG'}"><span class="badge bg-info text-dark">Đã nhận phòng</span></c:when>
                                    <c:when test="${hd.trangThai == 'DA_TRA_PHONG'}"><span class="badge bg-secondary">Đã trả phòng</span></c:when>
                                    <c:when test="${hd.trangThai == 'CHO_XAC_NHAN'}"><span class="badge bg-warning text-dark">Chờ xác nhận</span></c:when>
                                    <c:otherwise><span class="badge bg-danger">${hd.trangThai}</span></c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty hoatDongGganDay}">
                        <tr>
                            <td colspan="4" class="text-center text-muted">Chưa có hoạt động nào.</td>
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
