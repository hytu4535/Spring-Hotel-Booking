<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>${formTitle}</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css">
<style>
    :root { --sidebar-width: 250px; }
    body { background-color: #f8f9fa; overflow-x: hidden; }
    .main-content {
        margin-left: var(--sidebar-width);
        padding: 20px;
        min-height: 100vh;
        width: calc(100% - var(--sidebar-width));
        box-sizing: border-box;
    }
    @media (max-width: 768px) {
        .main-content { margin-left: 0; width: 100%; }
    }
</style></head>
<body>
<jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp"><jsp:param name="active" value="khachhang"/></jsp:include>
<main class="main-content">
    <a href="${pageContext.request.contextPath}/admin/khach-hang" class="btn btn-link px-0">← Danh sách khách hàng</a>
    <div class="card shadow-sm"><div class="card-body">
        <h3 class="mb-4">${formTitle}</h3>
        <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
        <form:form method="post" action="${pageContext.request.contextPath}${formAction}" modelAttribute="customerForm">
            <div class="row g-3">
                <div class="col-md-6"><form:label path="hoTen" cssClass="form-label">Họ tên *</form:label><form:input path="hoTen" cssClass="form-control"/><form:errors path="hoTen" cssClass="text-danger small"/></div>
                <div class="col-md-6"><form:label path="email" cssClass="form-label">Email *</form:label><form:input path="email" type="email" cssClass="form-control"/><form:errors path="email" cssClass="text-danger small"/></div>
                <div class="col-md-6"><form:label path="soDienThoai" cssClass="form-label">Số điện thoại</form:label><form:input path="soDienThoai" cssClass="form-control"/><form:errors path="soDienThoai" cssClass="text-danger small"/></div>
                <div class="col-md-6"><form:label path="cccd" cssClass="form-label">CCCD</form:label><form:input path="cccd" cssClass="form-control"/><form:errors path="cccd" cssClass="text-danger small"/></div>
                <div class="col-12"><form:label path="diaChi" cssClass="form-label">Địa chỉ</form:label><form:input path="diaChi" cssClass="form-control"/><form:errors path="diaChi" cssClass="text-danger small"/></div>
            </div>
            <div class="mt-4"><button class="btn btn-success"><i class="fas fa-plus"></i> ${formTitle == 'Thêm khách hàng' ? 'Thêm khách hàng' : 'Lưu thay đổi'}</button><a href="${pageContext.request.contextPath}/admin/khach-hang" class="btn btn-outline-secondary ms-2">Hủy</a></div>
        </form:form>
    </div></div>
</main></body></html>
