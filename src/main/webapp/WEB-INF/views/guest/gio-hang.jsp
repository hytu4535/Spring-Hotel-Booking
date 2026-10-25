<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Giỏ Đặt Phòng"/>
</jsp:include>

<div class="container mt-4 mb-5">
    <h2 class="fw-bold text-primary mb-4"><i class="bi bi-cart-check"></i> Các phòng bạn đã chọn</h2>

    <c:if test="${empty sessionScope.cart || empty sessionScope.cart.items}">
        <div class="alert alert-warning text-center p-5">
            <h4>Giỏ hàng của bạn đang trống!</h4>
            <a href="${pageContext.request.contextPath}/tim-phong" class="btn btn-primary mt-3">Tìm phòng ngay</a>
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.cart && not empty sessionScope.cart.items}">
        <div class="row">
            <div class="col-md-8">
                <div class="card shadow-sm border-0 mb-3">
                    <div class="card-header bg-white">
                        <h5 class="mb-0">Thời gian lưu trú: 
                            <span class="text-success">${sessionScope.cart.ngayNhan}</span> đến 
                            <span class="text-danger">${sessionScope.cart.ngayTra}</span> 
                            (${sessionScope.cart.soDem} đêm)
                        </h5>
                    </div>
                    <div class="card-body p-0">
                        <table class="table mb-0 align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th>Phòng</th>
                                    <th>Loại phòng</th>
                                    <th>Giá / Đêm</th>
                                    <th>Thành tiền</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${sessionScope.cart.items}">
                                    <tr>
                                        <td><strong>P.${item.soPhong}</strong></td>
                                        <td>${item.tenLoai}</td>
                                        <td><fmt:formatNumber value="${item.giaPhong}" type="currency" currencySymbol="VND"/></td>
                                        <td class="text-primary fw-bold"><fmt:formatNumber value="${item.thanhTien}" type="currency" currencySymbol="VND"/></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/gio-hang?action=remove&maPhong=${item.maPhong}" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/tim-phong" class="btn btn-outline-primary"><i class="bi bi-plus-circle"></i> Chọn thêm phòng khác</a>
            </div>

            <!-- Tổng kết tiền -->
            <div class="col-md-4">
                <div class="card shadow-sm border-primary">
                    <div class="card-body bg-light">
                        <h5 class="card-title fw-bold">Tổng Thanh Toán</h5>
                        <hr>
                        <div class="d-flex justify-content-between mb-3">
                            <span>Tiền phòng:</span>
                            <span class="fs-5 fw-bold text-danger"><fmt:formatNumber value="${sessionScope.cart.tongTien}" type="currency" currencySymbol="VND"/></span>
                        </div>
                        <a href="${pageContext.request.contextPath}/dat-phong" class="btn btn-primary w-100 btn-lg fw-bold">Tiến hành Đặt Phòng</a>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>