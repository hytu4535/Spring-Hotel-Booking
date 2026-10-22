<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Lịch sử đặt phòng của tôi"/>
</jsp:include>

<div class="container mt-5 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold text-primary"><i class="bi bi-journal-text"></i> Lịch sử đơn đặt phòng của bạn</h2>
        <a href="${pageContext.request.contextPath}/tim-phong" class="btn btn-warning fw-bold">Đặt phòng mới</a>
    </div>

    <c:if test="${empty danhSachDon}">
        <div class="alert alert-info text-center py-4">
            <i class="bi bi-info-circle fs-3"></i>
            <p class="mt-2 mb-0">Bạn chưa có đơn đặt phòng nào.</p>
        </div>
    </c:if>

    <c:if test="${not empty danhSachDon}">
        <div class="accordion" id="accordionBookings">
            <c:forEach var="dp" items="${danhSachDon}" varStatus="status">
                <div class="card shadow-sm mb-3 border-0">
                    <div class="card-header bg-light d-flex justify-content-between align-items-center py-3" id="heading${dp.maDp}">
                        <div>
                            <h5 class="mb-0 text-primary fw-bold">
                                Mã đơn: #${dp.maTraCuu}
                            </h5>
                            <small class="text-muted">Ngày đặt: ${dp.ngayDat}</small>
                        </div>
                        <div>
                            <span class="me-3">
                                Trạng thái: 
                                <c:choose>
                                    <c:when test="${dp.trangThai == 'CHO_XAC_NHAN'}"><span class="badge bg-warning text-dark">Chờ xác nhận</span></c:when>
                                    <c:when test="${dp.trangThai == 'DA_XAC_NHAN'}"><span class="badge bg-primary">Đã xác nhận</span></c:when>
                                    <c:when test="${dp.trangThai == 'DA_NHAN_PHONG'}"><span class="badge bg-success">Đang ở</span></c:when>
                                    <c:when test="${dp.trangThai == 'DA_TRA_PHONG'}"><span class="badge bg-secondary">Đã trả phòng</span></c:when>
                                    <c:when test="${dp.trangThai == 'DA_HUY'}"><span class="badge bg-danger">Đã hủy</span></c:when>
                                </c:choose>
                            </span>
                            <a href="${pageContext.request.contextPath}/thanh-toan/dat-coc?maTraCuu=${dp.maTraCuu}" class="btn btn-sm btn-success">
                                <i class="bi bi-credit-card"></i> Thanh toán cọc
                            </a>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>Nhận phòng:</strong> ${dp.ngayNhanPhong}</p>
                                <p><strong>Trả phòng:</strong> ${dp.ngayTraPhong}</p>
                                <p><strong>Tổng tiền:</strong> <fmt:formatNumber value="${dp.tongTien}" type="currency" currencySymbol="VND"/></p>
                            </div>
                            <div class="col-md-6">
                                <h6 class="fw-bold"><i class="bi bi-clock-history"></i> Lịch sử thanh toán đơn này:</h6>
                                <c:set var="payments" value="${paymentMap[dp.maDp]}"/>
                                <c:if test="${empty payments}">
                                    <p class="text-muted small">Chưa có giao dịch thanh toán.</p>
                                </c:if>
                                <c:if test="${not empty payments}">
                                    <ul class="list-group list-group-flush small">
                                        <c:forEach var="pay" items="${payments}">
                                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                                <span>
                                                    <span class="badge bg-info text-dark">${pay.loaiThanhToan}</span> 
                                                    (${pay.phuongThuc}) - <code>${pay.maGiaoDich}</code>
                                                </span>
                                                <strong class="text-success"><fmt:formatNumber value="${pay.soTien}" type="currency" currencySymbol="VND"/></strong>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
