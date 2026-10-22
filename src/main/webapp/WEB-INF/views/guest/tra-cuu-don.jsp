<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Tra cứu đơn đặt phòng"/>
</jsp:include>

<div class="row mt-5 mb-5">
    <div class="col-md-5">
        <div class="card shadow-sm">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0"><i class="bi bi-search"></i> Tra cứu thông tin</h5>
            </div>
            <div class="card-body">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/tra-cuu-don" method="post">
                    <input type="hidden" name="action" value="search">
                    <div class="mb-3">
                        <label class="fw-bold">Mã tra cứu</label>
                        <input type="text" name="maTraCuu" class="form-control text-uppercase" required value="${maTraCuuInput}" placeholder="VD: HTL2610A7X2">
                    </div>
                    <div class="mb-3">
                        <label class="fw-bold">Email đặt phòng</label>
                        <input type="email" name="email" class="form-control" required value="${emailInput}" placeholder="Email bạn đã dùng để đặt">
                    </div>
                    <button type="submit" class="btn btn-warning w-100 fw-bold">Tra cứu ngay</button>
                </form>
            </div>
        </div>
    </div>

    <!-- HIỂN THỊ KẾT QUẢ TÌM KIẾM -->
    <div class="col-md-7">
        <c:if test="${not empty donDatPhong}">
            <div class="card shadow border-info">
                <div class="card-body">
                    <h4 class="text-info fw-bold">Đơn hàng: #${donDatPhong.maTraCuu}</h4>
                    <hr>
                    <p><strong>Khách hàng:</strong> ${khachHang.hoTen}</p>
                    <p><strong>SĐT:</strong> ${khachHang.soDienThoai}</p>
                    <p><strong>Ngày nhận:</strong> ${donDatPhong.ngayNhanPhong}</p>
                    <p><strong>Ngày trả:</strong> ${donDatPhong.ngayTraPhong}</p>
                    <p><strong>Tổng tiền:</strong> <fmt:formatNumber value="${donDatPhong.tienPhong}" type="currency" currencySymbol="VND"/></p>
                    <p><strong>Trạng thái:</strong> 
                        <c:choose>
                            <c:when test="${donDatPhong.trangThai == 'CHO_XAC_NHAN'}"><span class="badge bg-warning text-dark">Chờ xác nhận</span></c:when>
                            <c:when test="${donDatPhong.trangThai == 'DA_XAC_NHAN'}"><span class="badge bg-primary">Đã xác nhận</span></c:when>
                            <c:when test="${donDatPhong.trangThai == 'DA_NHAN_PHONG'}"><span class="badge bg-success">Đang ở</span></c:when>
                            <c:when test="${donDatPhong.trangThai == 'DA_TRA_PHONG'}"><span class="badge bg-secondary">Đã trả phòng</span></c:when>
                            <c:when test="${donDatPhong.trangThai == 'DA_HUY'}"><span class="badge bg-danger">Đã hủy</span></c:when>
                        </c:choose>
                    </p>

                    <!-- Nút Thanh toán cọc VNPay -->
                    <div class="mt-3">
                        <a href="${pageContext.request.contextPath}/thanh-toan/dat-coc?maTraCuu=${donDatPhong.maTraCuu}" class="btn btn-success me-2">
                            <i class="bi bi-credit-card"></i> Thanh toán cọc VNPay
                        </a>
                    </div>

                    <!-- Lịch sử thanh toán -->
                    <c:if test="${not empty paymentHistory}">
                        <hr class="my-4">
                        <h5 class="fw-bold mb-3"><i class="bi bi-clock-history"></i> Lịch sử thanh toán</h5>
                        <div class="table-responsive">
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
                                            <td><code>${pay.maGiaoDich != null ? pay.maGiaoDich : 'N/A'}</code></td>
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

                    <!-- Nút Hủy đơn chỉ hiện khi trạng thái là CHỜ XÁC NHẬN -->
                    <c:if test="${donDatPhong.trangThai == 'CHO_XAC_NHAN'}">
                        <hr>
                        <form action="${pageContext.request.contextPath}/tra-cuu-don" method="post" onsubmit="return confirm('Bạn có chắc chắn muốn hủy đơn này không?');">
                            <input type="hidden" name="action" value="cancel">
                            <input type="hidden" name="maDp" value="${donDatPhong.maDp}">
                            <input type="hidden" name="maTraCuu" value="${donDatPhong.maTraCuu}">
                            <input type="hidden" name="email" value="${khachHang.email}">
                            <button type="submit" class="btn btn-danger"><i class="bi bi-x-circle"></i> Hủy đặt phòng</button>
                        </form>
                    </c:if>
                </div>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
