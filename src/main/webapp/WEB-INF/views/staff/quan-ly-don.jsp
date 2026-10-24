<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Danh sách Đơn Đặt Phòng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff-sidebar.css">
    <style>.admin-operation-page .main-content { margin-left: 280px; width: calc(100% - 280px); }</style>
</head>
<body class="admin-operation-page bg-light">
<div class="admin-wrapper d-flex">
    <c:choose>
        <c:when test="${isAdminView}"><jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp"><jsp:param name="active" value="datphong"/></jsp:include></c:when>
        <c:otherwise><jsp:include page="/WEB-INF/views/common/sidebar-staff.jsp"><jsp:param name="active" value="reservations"/></jsp:include></c:otherwise>
    </c:choose>

    <div class="main-content p-4">
        <h4 class="mb-4 fw-bold text-primary"><i class="bi bi-card-list"></i> Quản Lý Đơn Đặt Phòng</h4>

        <!-- Form Lọc Đơn -->
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-body">
                <form method="get" class="row g-2 align-items-center">
                    <div class="col-md-3">
                        <input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="Mã đơn, Tên KH, SĐT...">
                    </div>
                    <div class="col-md-2">
                        <select name="trangThai" class="form-select">
                            <option value="">-- Tất cả trạng thái --</option>
                            <option value="CHO_XAC_NHAN" ${trangThai == 'CHO_XAC_NHAN' ? 'selected' : ''}>Chờ xác nhận</option>
                            <option value="DA_XAC_NHAN" ${trangThai == 'DA_XAC_NHAN' ? 'selected' : ''}>Đã duyệt</option>
                            <option value="DA_NHAN_PHONG" ${trangThai == 'DA_NHAN_PHONG' ? 'selected' : ''}>Đang ở</option>
                            <option value="DA_TRA_PHONG" ${trangThai == 'DA_TRA_PHONG' ? 'selected' : ''}>Đã trả phòng</option>
                            <option value="DA_HUY" ${trangThai == 'DA_HUY' ? 'selected' : ''}>Đã hủy</option>
                        </select>
                    </div>
                    <div class="col-md-2"><input type="date" name="tuNgay" value="${tuNgay}" class="form-control" title="Từ ngày"></div>
                    <div class="col-md-2"><input type="date" name="denNgay" value="${denNgay}" class="form-control" title="Đến ngày"></div>
                    <div class="col-md-3">
                        <button type="submit" class="btn btn-primary"><i class="bi bi-search"></i> Tìm kiếm</button>
                        <a href="${pageContext.request.contextPath}${bookingBasePath}" class="btn btn-outline-secondary">Xóa lọc</a>
                    </div>
                </form>
            </div>
        </div>

        <c:if test="${not empty sessionScope.successMsg}"><div class="alert alert-success">${sessionScope.successMsg}</div><c:remove var="successMsg" scope="session"/></c:if>

        <div class="card shadow-sm border-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>Mã Tra Cứu</th>
                            <th>Khách Hàng</th>
                            <th>Nhận / Trả</th>
                            <th>Tổng Tiền</th>
                            <th>Trạng Thái</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="dp" items="${danhSachDon}">
                            <tr>
                                <td><span class="badge bg-secondary">#${dp.maTraCuu}</span></td>
                                <td><strong>${dp.tenKhachHang}</strong><br><small><i class="bi bi-telephone"></i> ${dp.sdtKhachHang}</small></td>
                                <td><span class="text-success fw-bold">${dp.ngayNhanPhong}</span><br><span class="text-danger fw-bold">${dp.ngayTraPhong}</span></td>
                                <td class="fw-bold text-primary"><fmt:formatNumber value="${dp.tongTien}" type="currency" currencySymbol="VND"/></td>
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
                                    <a href="${pageContext.request.contextPath}${bookingBasePath}/${dp.maDp}" class="btn btn-sm btn-info text-white"><i class="bi bi-eye"></i> Chi tiết</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body></html>