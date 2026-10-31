<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Thanh toán đặt cọc VNPay"/>
</jsp:include>

<div class="container mt-5 mb-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow border-0">
                <div class="card-header bg-primary text-white py-3">
                    <h4 class="mb-0"><i class="bi bi-credit-card-2-front"></i> Thanh toán đặt cọc đơn phòng #${datPhong.maTraCuu}</h4>
                </div>
                <div class="card-body p-4">
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle"></i> Quý khách vui lòng thanh toán khoản cọc <strong>30% tổng tiền</strong> để xác nhận giữ phòng. Đơn đặt phòng sẽ được xử lý sau khi cọc thành công.
                    </div>

                    <div class="table-responsive mb-4">
                        <table class="table table-bordered">
                            <tr>
                                <th class="w-50">Mã tra cứu đơn:</th>
                                <td class="fw-bold text-danger">${datPhong.maTraCuu}</td>
                            </tr>
                            <tr>
                                <th>Ngày nhận phòng:</th>
                                <td>${datPhong.ngayNhanPhong}</td>
                            </tr>
                            <tr>
                                <th>Ngày trả phòng:</th>
                                <td>${datPhong.ngayTraPhong}</td>
                            </tr>
                            <tr>
                                <th>Tổng giá trị đơn phòng:</th>
                                <td><fmt:formatNumber value="${datPhong.tongTien}" type="currency" currencySymbol="VND"/></td>
                            </tr>
                            <tr class="table-warning">
                                <th class="fw-bold">Số tiền cần cọc (30%):</th>
                                <td class="fw-bold text-danger fs-4"><fmt:formatNumber value="${depositAmount}" type="currency" currencySymbol="VND"/></td>
                            </tr>
                        </table>
                    </div>

                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/thanh-toan/vnpay?maTraCuu=${datPhong.maTraCuu}" class="btn btn-success btn-lg px-4 py-3 fw-bold shadow">
                            <i class="bi bi-shield-lock-fill"></i> Thanh toán VNPay Sandbox
                        </a>
                    </div>

                    <!-- Lịch sử thanh toán của đơn này -->
                    <c:if test="${not empty paymentHistory}">
                        <hr class="my-4">
                        <h5 class="fw-bold mb-3"><i class="bi bi-clock-history"></i> Lịch sử thanh toán</h5>
                        <div class="table-responsive">
                            <table class="table table-striped table-bordered align-middle">
                                <thead class="table-dark">
                                    <tr>
                                        <th>Mã GD</th>
                                        <th>Loại thanh toán</th>
                                        <th>Phương thức</th>
                                        <th>Số tiền</th>
                                        <th>Thời gian</th>
                                        <th>Ghi chú</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="pay" items="${paymentHistory}">
                                        <tr>
                                            <td><code>${pay.maGiaoDich != null ? pay.maGiaoDich : 'N/A'}</code></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${pay.loaiThanhToan == 'DAT_COC'}"><span class="badge bg-warning text-dark">Đặt cọc</span></c:when>
                                                    <c:when test="${pay.loaiThanhToan == 'THANH_TOAN_DU'}"><span class="badge bg-success">Thanh toán đủ</span></c:when>
                                                    <c:otherwise><span class="badge bg-secondary">${pay.loaiThanhToan}</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${pay.phuongThuc}</td>
                                            <td class="fw-bold text-success"><fmt:formatNumber value="${pay.soTien}" type="currency" currencySymbol="VND"/></td>
                                            <td>${pay.ngayThanhToan}</td>
                                            <td>${pay.ghiChu}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
