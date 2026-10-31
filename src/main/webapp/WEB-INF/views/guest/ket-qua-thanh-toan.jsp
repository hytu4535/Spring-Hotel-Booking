<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Kết quả thanh toán VNPay"/>
</jsp:include>

<div class="container mt-5 mb-5 text-center">
    <div class="card shadow border-0 mx-auto" style="max-width: 700px;">
        <div class="card-body p-5">
            <c:choose>
                <c:when test="${status == 'SUCCESS'}">
                    <i class="bi bi-check-circle-fill text-success" style="font-size: 5rem;"></i>
                    <h2 class="mt-3 fw-bold text-success">Thanh toán đặt cọc thành công!</h2>
                    <p class="text-muted">${message}</p>
                    
                    <div class="alert alert-success mt-4">
                        Mã đơn đặt phòng: <strong class="fs-4 text-dark">${maTraCuu}</strong>
                    </div>
                </c:when>
                <c:when test="${status == 'INVALID_SIGNATURE'}">
                    <i class="bi bi-shield-exclamation text-danger" style="font-size: 5rem;"></i>
                    <h2 class="mt-3 fw-bold text-danger">Giao dịch đang chờ xác minh!</h2>
                    <p class="text-muted">${message}</p>
                    <div class="alert alert-warning">
                        Chữ ký bảo mật không khớp hoặc giao dịch chưa được cổng thanh toán xác nhận chính thức. Vui lòng liên hệ bộ phận hỗ trợ.
                    </div>
                </c:when>
                <c:otherwise>
                    <i class="bi bi-x-circle-fill text-danger" style="font-size: 5rem;"></i>
                    <h2 class="mt-3 fw-bold text-danger">Thanh toán không thành công!</h2>
                    <p class="text-muted">${message}</p>
                </c:otherwise>
            </c:choose>

            <!-- Lịch sử thanh toán -->
            <c:if test="${not empty paymentHistory}">
                <hr class="my-4">
                <h5 class="fw-bold text-start mb-3"><i class="bi bi-clock-history"></i> Lịch sử thanh toán của đơn</h5>
                <div class="table-responsive text-start">
                    <table class="table table-striped table-bordered align-middle">
                        <thead class="table-dark">
                            <tr>
                                <th>Mã GD</th>
                                <th>Loại</th>
                                <th>Phương thức</th>
                                <th>Số tiền</th>
                                <th>Thời gian</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="pay" items="${paymentHistory}">
                                <tr>
                                    <td><code>${pay.maGiaoDich}</code></td>
                                    <td><span class="badge bg-info text-dark">${pay.loaiThanhToan}</span></td>
                                    <td>${pay.phuongThuc}</td>
                                    <td class="fw-bold text-success"><fmt:formatNumber value="${pay.soTien}" type="currency" currencySymbol="VND"/></td>
                                    <td>${pay.ngayThanhToan}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>

            <div class="mt-4">
                <c:if test="${not empty maTraCuu}">
                    <a href="${pageContext.request.contextPath}/thanh-toan/dat-coc?maTraCuu=${maTraCuu}" class="btn btn-primary me-2">Quay lại trang thanh toán</a>
                </c:if>
                <a href="${pageContext.request.contextPath}/tra-cuu-don" class="btn btn-warning me-2">Tra cứu đơn</a>
                <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">Về trang chủ</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
