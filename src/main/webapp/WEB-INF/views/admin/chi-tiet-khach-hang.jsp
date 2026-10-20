<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>Chi tiết khách hàng</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css"></head>
<body>
<jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp"><jsp:param name="active" value="khachhang"/></jsp:include>
<main class="main-content">
    <a href="${pageContext.request.contextPath}/admin/khach-hang" class="btn btn-link px-0">← Danh sách khách hàng</a>
    <div class="card shadow-sm mb-4"><div class="card-body"><h3>${customer.hoTen}</h3>
        <div class="row"><div class="col-md-6">Email: ${customer.email}<br>Điện thoại: ${customer.soDienThoai}<br>CCCD: ${customer.cccd}</div><div class="col-md-6">Địa chỉ: ${customer.diaChi}<br>Mã tài khoản: ${customer.maTk}</div></div>
    </div></div>
    <div class="card shadow-sm"><div class="card-header fw-bold">Lịch sử đặt phòng</div><div class="table-responsive"><table class="table mb-0">
        <thead><tr><th>Mã tra cứu</th><th>Ngày đặt</th><th>Nhận phòng</th><th>Trả phòng</th><th>Trạng thái</th><th>Tổng tiền</th></tr></thead>
        <tbody><c:forEach var="item" items="${bookingHistory}"><tr><td>${item.maTraCuu}</td><td><fmt:formatDate value="${item.ngayDat}" pattern="dd/MM/yyyy HH:mm"/></td><td>${item.ngayNhanPhong}</td><td>${item.ngayTraPhong}</td><td>${item.trangThai}</td><td><fmt:formatNumber value="${item.tongTien}" type="number"/> đ</td></tr></c:forEach>
        <c:if test="${empty bookingHistory}"><tr><td colspan="6" class="text-center py-3">Chưa có lịch sử đặt phòng.</td></tr></c:if></tbody>
    </table></div></div>
    <div class="card shadow-sm mt-4"><div class="card-header">Gộp hồ sơ trùng</div><div class="card-body"><p class="text-muted">Nhập mã hồ sơ chính để chuyển lịch sử đặt phòng sang hồ sơ đó rồi xóa hồ sơ này.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/khach-hang/${customer.maKh}/gop" class="row g-2" onsubmit="return confirm('Thao tác này không thể hoàn tác. Tiếp tục?');">
            <div class="col-auto"><input type="number" name="primaryId" required min="1" class="form-control" placeholder="Mã hồ sơ chính"></div><div class="col-auto"><button class="btn btn-warning">Gộp vào hồ sơ chính</button></div>
        </form>
    </div></div>
</main></body></html>
