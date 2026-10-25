<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý Đơn Đặt Phòng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff-sidebar.css">
    <style>
        .admin-operation-page .admin-wrapper {
            display: block !important;
            position: relative;
        }

        .admin-operation-page .main-content {
            box-sizing: border-box;
            margin-left: 280px !important;
            width: calc(100% - 280px) !important;
        }

        @media (max-width: 768px) {
            .admin-operation-page .main-content {
                margin-left: 0 !important;
                width: 100% !important;
            }
        }
    </style>
</head>
<body class="admin-operation-page">
<div class="admin-wrapper">
    <c:choose>
        <c:when test="${isAdminView}">
            <jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp">
                <jsp:param name="active" value="datphong"/>
            </jsp:include>
        </c:when>
        <c:otherwise>
            <jsp:include page="/WEB-INF/views/common/sidebar-staff.jsp">
                <jsp:param name="active" value="reservations"/>
            </jsp:include>
        </c:otherwise>
    </c:choose>

    <div class="main-content">
        <div class="top-navbar">
            <h4 class="mb-0 fw-bold text-primary"><i class="bi bi-card-list"></i> Danh sách Đơn Đặt Phòng</h4>
        </div>

        <c:if test="${not empty sessionScope.successMsg}">
            <div class="alert alert-success fw-bold"><i class="bi bi-check-circle"></i> ${sessionScope.successMsg}</div>
            <c:remove var="successMsg" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.errorMsg}">
            <div class="alert alert-danger fw-bold"><i class="bi bi-exclamation-triangle"></i> ${sessionScope.errorMsg}</div>
            <c:remove var="errorMsg" scope="session"/>
        </c:if>

        <div class="card shadow-sm border-0">
            <div class="card-body p-0">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>Mã Tra Cứu</th>
                            <th>Khách Hàng</th>
                            <th>Ngày Nhận / Trả</th>
                            <th>Tổng Tiền</th>
                            <th>Trạng Thái</th>
                            <th class="text-center">Thao tác duyệt</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="dp" items="${danhSachDon}">
                            <tr>
                                <td><span class="badge bg-secondary">#${dp.maTraCuu}</span></td>
                                <td>
                                    <strong>${dp.tenKhachHang}</strong><br>
                                    <small class="text-muted"><i class="bi bi-telephone"></i> ${dp.sdtKhachHang}</small>
                                </td>
                                <td>
                                    <span class="text-success fw-bold">${dp.ngayNhanPhong}</span> <br>
                                    <span class="text-danger fw-bold">${dp.ngayTraPhong}</span>
                                </td>
                                <td><fmt:formatNumber value="${dp.tongTien}" type="currency" currencySymbol="VND"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${dp.trangThai == 'CHO_XAC_NHAN'}"><span class="badge bg-warning text-dark">Chờ XN</span></c:when>
                                        <c:when test="${dp.trangThai == 'DA_XAC_NHAN'}"><span class="badge bg-primary">Đã Duyệt</span></c:when>
                                        <c:when test="${dp.trangThai == 'DA_NHAN_PHONG'}"><span class="badge bg-success">Đang Ở</span></c:when>
                                        <c:when test="${dp.trangThai == 'DA_TRA_PHONG'}"><span class="badge bg-secondary">Đã Trả</span></c:when>
                                        <c:when test="${dp.trangThai == 'DA_HUY'}"><span class="badge bg-danger">Đã Hủy</span></c:when>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <!-- Nút đổi trạng thái linh hoạt -->
                                    <c:if test="${dp.trangThai == 'CHO_XAC_NHAN'}">
                                        <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-inline">
                                            <input type="hidden" name="action" value="update_status">
                                            <input type="hidden" name="maDp" value="${dp.maDp}">
                                            <input type="hidden" name="trangThaiMoi" value="DA_XAC_NHAN">
                                            <button type="submit" class="btn btn-sm btn-primary">Xác nhận đơn</button>
                                        </form>
                                        <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-inline">
                                            <input type="hidden" name="action" value="update_status">
                                            <input type="hidden" name="maDp" value="${dp.maDp}">
                                            <input type="hidden" name="trangThaiMoi" value="DA_HUY">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Hủy đơn này?');">Hủy</button>
                                        </form>
                                    </c:if>

                                    <c:if test="${dp.trangThai == 'DA_XAC_NHAN'}">
                                        <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-inline">
                                            <input type="hidden" name="action" value="update_status">
                                            <input type="hidden" name="maDp" value="${dp.maDp}">
                                            <input type="hidden" name="trangThaiMoi" value="DA_NHAN_PHONG">
                                            <button type="submit" class="btn btn-sm btn-success">Check-in</button>
                                        </form>
                                    </c:if>

                                    <c:if test="${dp.trangThai == 'DA_NHAN_PHONG'}">
                                        <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-inline">
                                            <input type="hidden" name="action" value="update_status">
                                            <input type="hidden" name="maDp" value="${dp.maDp}">
                                            <input type="hidden" name="trangThaiMoi" value="DA_TRA_PHONG">
                                            <button type="submit" class="btn btn-sm btn-warning">Check-out</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty danhSachDon}">
                            <tr><td colspan="6" class="text-center p-4">Chưa có đơn đặt phòng nào.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>