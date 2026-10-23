<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch sử đặt phòng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="/WEB-INF/jsp/common/header.jsp"><jsp:param name="title" value="Lịch sử đặt phòng"/></jsp:include>

<div class="container my-5">
    <div class="row">
        <!-- Sidebar Menu Khách Hàng (Tùy chọn, hoặc để Full Width) -->
        <div class="col-md-3 mb-4">
            <div class="list-group shadow-sm">
                <a href="${pageContext.request.contextPath}/customer/profile" class="list-group-item list-group-item-action"><i class="bi bi-person me-2"></i> Hồ sơ cá nhân</a>
                <a href="${pageContext.request.contextPath}/customer/lich-su-dat-phong" class="list-group-item list-group-item-action active"><i class="bi bi-clock-history me-2"></i> Lịch sử đặt phòng</a>
            </div>
        </div>

        <div class="col-md-9">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white py-3">
                    <h4 class="mb-0 text-primary"><i class="bi bi-list-check"></i> Lịch sử các đơn phòng của bạn</h4>
                </div>
                <div class="card-body p-0">
                    <c:if test="${not empty success}"><div class="alert alert-success m-3">${success}</div></c:if>
                    
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Mã Đơn</th>
                                    <th>Ngày Đặt</th>
                                    <th>Nhận / Trả</th>
                                    <th>Tổng Tiền</th>
                                    <th>Trạng Thái</th>
                                    <th>Hành Động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="dp" items="${historyList}">
                                    <tr>
                                        <td><span class="badge bg-secondary">#${dp.maTraCuu}</span></td>
                                        <td><fmt:formatDate value="${dp.ngayDat}" pattern="dd/MM/yyyy HH:mm"/></td>
                                        <td>
                                            <span class="text-success fw-bold">${dp.ngayNhanPhong}</span><br>
                                            <span class="text-danger fw-bold">${dp.ngayTraPhong}</span>
                                        </td>
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
                                        <td>
                                            <a href="${pageContext.request.contextPath}/tra-cuu-don?action=search&maTraCuu=${dp.maTraCuu}&email=${sessionScope.currentUser.email}" class="btn btn-sm btn-outline-info">Chi tiết</a>
                                            <!-- Khách chỉ được hủy khi đơn đang ở trạng thái CHỜ XÁC NHẬN -->
                                            <c:if test="${dp.trangThai == 'CHO_XAC_NHAN'}">
                                                <form action="${pageContext.request.contextPath}/customer/lich-su-dat-phong/huy" method="post" class="d-inline" onsubmit="return confirm('Bạn chắc chắn muốn hủy đơn này?');">
                                                    <input type="hidden" name="maDp" value="${dp.maDp}">
                                                    <button type="submit" class="btn btn-sm btn-danger"><i class="bi bi-x-circle"></i> Hủy</button>
                                                </form>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty historyList}">
                                    <tr><td colspan="6" class="text-center py-4 text-muted">Bạn chưa có lịch sử đặt phòng nào.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
</body></html>