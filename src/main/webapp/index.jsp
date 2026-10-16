<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Trang chủ"/>
</jsp:include>

<section class="hero-section text-center">
    <div class="container">
        <h1 class="display-5 fw-bold mb-3">Đặt phòng khách sạn dễ dàng</h1>
        <p class="lead mb-4">Tìm phòng trống nhanh chóng – Không cần đăng ký tài khoản</p>

        <form action="${pageContext.request.contextPath}/tim-phong" method="get"
              class="row g-3 justify-content-center">
            <div class="col-md-3">
                <label class="form-label text-white">Ngày nhận phòng</label>
                <input type="date" name="ngayNhan" class="form-control form-control-lg" required>
            </div>
            <div class="col-md-3">
                <label class="form-label text-white">Ngày trả phòng</label>
                <input type="date" name="ngayTra" class="form-control form-control-lg" required>
            </div>
            <div class="col-md-2 d-flex align-items-end">
                <button type="submit" class="btn btn-warning btn-lg w-100">
                    <i class="bi bi-search"></i> Tìm phòng
                </button>
            </div>
        </form>
    </div>
</section>

<div class="row text-center g-4 mb-5">
    <div class="col-md-4">
        <i class="bi bi-calendar-check display-4 text-primary"></i>
        <h5 class="mt-3">Đặt phòng nhanh</h5>
        <p class="text-muted">Chỉ cần chọn ngày và điền thông tin</p>
    </div>
    <div class="col-md-4">
        <i class="bi bi-shield-check display-4 text-primary"></i>
        <h5 class="mt-3">An toàn & Bảo mật</h5>
        <p class="text-muted">Thông tin của bạn được bảo vệ</p>
    </div>
    <div class="col-md-4">
        <i class="bi bi-search display-4 text-primary"></i>
        <h5 class="mt-3">Tra cứu đơn dễ dàng</h5>
        <p class="text-muted">Dùng mã tra cứu + email</p>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>