<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hồ sơ khách hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="/WEB-INF/jsp/common/header.jsp"><jsp:param name="title" value="Hồ sơ khách hàng"/></jsp:include>
<div class="container my-4">
    <div class="row justify-content-center"><div class="col-lg-8">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h3 class="mb-4">Hồ sơ khách hàng</h3>
                <c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
                <c:if test="${not empty globalError}"><div class="alert alert-danger">${globalError}</div></c:if>
                <form:form method="post" modelAttribute="profile">
                    <div class="mb-3"><label class="form-label">Họ và tên</label><form:input path="hoTen" cssClass="form-control"/><form:errors path="hoTen" cssClass="text-danger small"/></div>
                    <div class="mb-3"><label class="form-label">Email</label><form:input path="email" type="email" cssClass="form-control"/><form:errors path="email" cssClass="text-danger small"/></div>
                    <div class="mb-3"><label class="form-label">Số điện thoại</label><form:input path="soDienThoai" cssClass="form-control"/><form:errors path="soDienThoai" cssClass="text-danger small"/></div>
                    <div class="mb-3"><label class="form-label">CCCD</label><form:input path="cccd" cssClass="form-control"/><form:errors path="cccd" cssClass="text-danger small"/></div>
                    <div class="mb-3"><label class="form-label">Địa chỉ</label><form:textarea path="diaChi" cssClass="form-control" rows="3"/><form:errors path="diaChi" cssClass="text-danger small"/></div>
                    <button class="btn btn-primary" type="submit">Lưu thay đổi</button>
                </form:form>
            </div>
        </div>
    </div></div>
</div>
</body>
</html>
