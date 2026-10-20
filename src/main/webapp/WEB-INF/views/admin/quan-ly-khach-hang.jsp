<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>Quản lý khách hàng</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css"></head>
<body>
<jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp"><jsp:param name="active" value="khachhang"/></jsp:include>
<main class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4"><h2><i class="fas fa-users me-2"></i>Quản lý khách hàng</h2><a href="${pageContext.request.contextPath}/admin/khach-hang/moi" class="btn btn-success"><i class="fas fa-plus"></i> Thêm khách hàng</a></div>
    <c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
    <form class="row g-2 mb-3" method="get">
        <div class="col-md-8"><input name="keyword" value="${keyword}" class="form-control" placeholder="Tìm theo họ tên, email, số điện thoại hoặc CCCD"></div>
        <div class="col-auto"><button class="btn btn-primary"><i class="fas fa-search"></i> Tra cứu</button></div>
        <div class="col-auto"><a href="${pageContext.request.contextPath}/admin/khach-hang" class="btn btn-outline-secondary">Xóa lọc</a></div>
    </form>
    <div class="card shadow-sm"><div class="table-responsive"><table class="table table-hover align-middle mb-0">
        <thead class="table-dark"><tr><th>Mã</th><th>Khách hàng</th><th>Email</th><th>Điện thoại</th><th>CCCD</th><th>Số đơn</th><th></th></tr></thead>
        <tbody><c:forEach var="item" items="${khachHangList}"><tr>
            <td>${item.maKh}</td><td><strong>${item.hoTen}</strong><br><small class="text-muted">${item.diaChi}</small></td>
            <td>${item.email}</td><td>${item.soDienThoai}</td><td>${item.cccd}</td><td>${item.soDonDatPhong}</td>
            <td><a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/admin/khach-hang/${item.maKh}">Chi tiết</a>
                <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/admin/khach-hang/${item.maKh}/sua">Sửa</a>
                <form method="post" action="${pageContext.request.contextPath}/admin/khach-hang/${item.maKh}/xoa" class="d-inline" onsubmit="return confirm('Chỉ xóa hồ sơ không có lịch sử đặt phòng. Tiếp tục?');"><button class="btn btn-sm btn-outline-danger">Xóa</button></form>
            </td>
        </tr></c:forEach><c:if test="${empty khachHangList}"><tr><td colspan="7" class="text-center py-4">Không tìm thấy khách hàng.</td></tr></c:if></tbody>
    </table></div></div>
</main>
</body></html>
