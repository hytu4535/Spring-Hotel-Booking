<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Tìm phòng"/>
</jsp:include>

<div class="container mt-4 mb-5">
    <h2 class="fw-bold text-primary mb-4"><i class="bi bi-search"></i> Tìm phòng trống</h2>

    <form method="get" action="${pageContext.request.contextPath}/tim-phong" class="row g-3 mb-4">
        <div class="col-md-4">
            <label class="form-label">Ngày nhận phòng</label>
            <input type="date" name="ngayNhan" class="form-control" value="${ngayNhan}" required>
        </div>
        <div class="col-md-4">
            <label class="form-label">Ngày trả phòng</label>
            <input type="date" name="ngayTra" class="form-control" value="${ngayTra}" required>
        </div>
        <div class="col-md-4 d-flex align-items-end">
            <button type="submit" class="btn btn-primary w-100">Tìm phòng</button>
        </div>
    </form>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <c:if test="${not empty ngayNhan and not empty ngayTra}">
        <p class="text-muted">Khoảng lưu trú: <strong>${ngayNhan}</strong> đến <strong>${ngayTra}</strong> (${soDem} đêm)</p>
    </c:if>

    <c:if test="${not empty danhSachPhong}">
        <div class="row g-3">
            <c:forEach var="phong" items="${danhSachPhong}">
                <div class="col-md-6 col-lg-4">
                    <div class="card h-100 shadow-sm">
                        <div class="card-body">
                            <h5 class="card-title mb-1">Phòng ${phong.soPhong}</h5>
                            <p class="text-muted mb-2">${phong.tenLoai} - Tầng ${phong.tang}</p>
                            <p class="mb-1">Sức chứa: <strong>${phong.sucChua}</strong> người</p>
                            <p class="mb-2">Diện tích: <strong>${phong.dienTich}</strong> m²</p>
                            <p class="fw-bold text-danger mb-3">
                                <fmt:formatNumber value="${phong.giaPhong}" type="currency" currencySymbol="VND"/> / đêm
                            </p>
                            <div class="d-flex gap-2">
                                <a class="btn btn-outline-secondary btn-sm"
                                   href="${pageContext.request.contextPath}/chi-tiet-phong?maPhong=${phong.maPhong}&ngayNhan=${ngayNhan}&ngayTra=${ngayTra}">
                                    Chi tiết
                                </a>
                                <form action="${pageContext.request.contextPath}/gio-hang" method="post" class="ms-auto">
                                    <input type="hidden" name="maPhong" value="${phong.maPhong}">
                                    <input type="hidden" name="soPhong" value="${phong.soPhong}">
                                    <input type="hidden" name="tenLoai" value="${phong.tenLoai}">
                                    <input type="hidden" name="giaPhong" value="${phong.giaPhong}">
                                    <input type="hidden" name="ngayNhan" value="${ngayNhan}">
                                    <input type="hidden" name="ngayTra" value="${ngayTra}">
                                    <button type="submit" class="btn btn-primary btn-sm">Thêm vào giỏ</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>

    <c:if test="${empty danhSachPhong and not empty ngayNhan and not empty ngayTra and empty error}">
        <div class="alert alert-warning mt-3">Không có phòng trống trong khoảng thời gian đã chọn.</div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>