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
                            <button type="button" class="btn btn-sm btn-info text-white me-2 shadow-sm" data-bs-toggle="modal" data-bs-target="#detailModal${dp.maDp}" title="Xem chi tiết đơn">
                                <i class="bi bi-eye"></i> Chi tiết
                            </button>
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
                                <p><strong>Phòng đã đặt:</strong> 
                                    <c:forEach var="ct" items="${dp.chiTietDatPhongs}" varStatus="loop">
                                        <span class="badge bg-info text-dark">P.${ct.phong.soPhong} (${ct.phong.loaiPhong.tenLoai})</span>${!loop.last ? ', ' : ''}
                                    </c:forEach>
                                </p>
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

                        <!-- Đánh giá phòng (Chỉ hiển thị khi đơn đã trả phòng DA_TRA_PHONG) -->
                        <c:if test="${dp.trangThai == 'DA_TRA_PHONG'}">
                            <hr class="my-3">
                            <div class="p-3 bg-light rounded border">
                                <h6 class="fw-bold text-dark mb-2"><i class="bi bi-star-fill text-warning"></i> Đánh giá của bạn cho đơn phòng này</h6>
                                <c:set var="review" value="${reviewMap[dp.maDp]}"/>
                                <c:if test="${not empty review}">
                                    <div class="text-success fw-bold mb-1">
                                        <c:forEach begin="1" end="${review.soSao}">⭐</c:forEach> (${review.soSao}/5 sao)
                                    </div>
                                    <p class="mb-1 fst-italic">"${review.noiDung}"</p>
                                    <small class="text-muted">Đánh giá vào: ${review.ngayDanhGia}</small>
                                </c:if>
                                <c:if test="${empty review}">
                                    <form action="${pageContext.request.contextPath}/khach-hang/danh-gia" method="post" class="mt-2">
                                        <input type="hidden" name="maDp" value="${dp.maDp}">
                                        <div class="row g-2 align-items-center">
                                            <div class="col-md-3">
                                                <select name="soSao" class="form-select form-select-sm" required>
                                                    <option value="5">⭐⭐⭐⭐⭐ (5 sao)</option>
                                                    <option value="4">⭐⭐⭐⭐ (4 sao)</option>
                                                    <option value="3">⭐⭐⭐ (3 sao)</option>
                                                    <option value="2">⭐⭐ (2 sao)</option>
                                                    <option value="1">⭐ (1 sao)</option>
                                                </select>
                                            </div>
                                            <div class="col-md-7">
                                                <input type="text" name="noiDung" class="form-control form-control-sm" placeholder="Nhập nhận xét của bạn về phòng..." required>
                                            </div>
                                            <div class="col-md-2">
                                                <button type="submit" class="btn btn-primary btn-sm w-100 fw-bold">Gửi đánh giá</button>
                                            </div>
                                        </div>
                                    </form>
                                </c:if>
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- MODALS XEM CHI TIẾT ĐƠN -->
        <c:forEach var="dp" items="${danhSachDon}">
            <div class="modal fade" id="detailModal${dp.maDp}" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-lg modal-dialog-scrollable">
                    <div class="modal-content shadow-lg border-0">
                        <div class="modal-header bg-primary text-white">
                            <h5 class="modal-title fw-bold"><i class="bi bi-info-circle-fill"></i> Chi Tiết Đơn Đặt Phòng #${dp.maTraCuu}</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body bg-light p-4">
                            <div class="card shadow-sm border-0 mb-3 rounded-3">
                                <div class="card-body">
                                    <h6 class="text-primary fw-bold mb-3"><i class="bi bi-calendar-check"></i> Thông tin chung</h6>
                                    <p class="mb-1"><strong>Mã tra cứu:</strong> <span class="text-danger fw-bold">${dp.maTraCuu}</span></p>
                                    <p class="mb-1"><strong>Ngày đặt:</strong> ${dp.ngayDat}</p>
                                    <p class="mb-1"><strong>Ngày nhận phòng:</strong> <span class="text-success fw-bold">${dp.ngayNhanPhong}</span></p>
                                    <p class="mb-1"><strong>Ngày trả phòng:</strong> <span class="text-danger fw-bold">${dp.ngayTraPhong}</span></p>
                                    <p class="mb-0"><strong>Trạng thái:</strong> 
                                        <c:choose>
                                            <c:when test="${dp.trangThai == 'CHO_XAC_NHAN'}"><span class="badge bg-warning text-dark">Chờ xác nhận</span></c:when>
                                            <c:when test="${dp.trangThai == 'DA_XAC_NHAN'}"><span class="badge bg-primary">Đã xác nhận</span></c:when>
                                            <c:when test="${dp.trangThai == 'DA_NHAN_PHONG'}"><span class="badge bg-success">Đang ở</span></c:when>
                                            <c:when test="${dp.trangThai == 'DA_TRA_PHONG'}"><span class="badge bg-secondary">Đã trả phòng</span></c:when>
                                            <c:when test="${dp.trangThai == 'DA_HUY'}"><span class="badge bg-danger">Đã hủy</span></c:when>
                                        </c:choose>
                                    </p>
                                </div>
                            </div>

                            <div class="card shadow-sm border-0 mb-3 rounded-3">
                                <div class="card-body">
                                    <h6 class="text-primary fw-bold mb-3"><i class="bi bi-house-door-fill"></i> Danh sách phòng đã đặt</h6>
                                    <div class="table-responsive">
                                        <table class="table table-bordered align-middle mb-0">
                                            <thead class="table-light">
                                                <tr>
                                                    <th>Số phòng</th>
                                                    <th>Loại phòng</th>
                                                    <th>Giá / đêm</th>
                                                    <th>Số đêm</th>
                                                    <th>Thành tiền</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="ct" items="${dp.chiTietDatPhongs}">
                                                    <tr>
                                                        <td><strong>P.${ct.phong.soPhong}</strong></td>
                                                        <td>${ct.phong.loaiPhong.tenLoai}</td>
                                                        <td><fmt:formatNumber value="${ct.giaPhong}" type="currency" currencySymbol="VND"/></td>
                                                        <td>${ct.soDem}</td>
                                                        <td class="text-primary fw-bold"><fmt:formatNumber value="${ct.thanhTien}" type="currency" currencySymbol="VND"/></td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>

                            <div class="card shadow-sm border-0 mb-3 rounded-3">
                                <div class="card-body">
                                    <h6 class="text-success fw-bold mb-3"><i class="bi bi-bag-plus-fill"></i> Danh sách dịch vụ đã sử dụng</h6>
                                    <c:set var="services" value="${serviceMap[dp.maDp]}"/>
                                    <c:if test="${empty services}">
                                        <p class="text-muted small mb-0">Chưa có dịch vụ phát sinh nào.</p>
                                    </c:if>
                                    <c:if test="${not empty services}">
                                        <div class="table-responsive">
                                            <table class="table table-sm table-bordered mb-0 align-middle">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th>Tên dịch vụ</th>
                                                        <th>Số lượng</th>
                                                        <th>Đơn giá</th>
                                                        <th>Thành tiền</th>
                                                        <th>Ngày dùng</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="s" items="${services}">
                                                        <tr>
                                                            <td><strong>${s.tenDv}</strong></td>
                                                            <td>${s.soLuong}</td>
                                                            <td><fmt:formatNumber value="${s.giaTaiThoiDiem}" type="currency" currencySymbol="VND"/></td>
                                                            <td class="text-success fw-bold"><fmt:formatNumber value="${s.thanhTien}" type="currency" currencySymbol="VND"/></td>
                                                            <td>${s.ngaySuDung}</td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="card shadow-sm border-0 mb-3 rounded-3">
                                <div class="card-body">
                                    <h6 class="text-primary fw-bold mb-3"><i class="bi bi-clock-history"></i> Lịch sử thanh toán</h6>
                                    <c:set var="payments" value="${paymentMap[dp.maDp]}"/>
                                    <c:if test="${empty payments}">
                                        <p class="text-muted small mb-0">Chưa có giao dịch thanh toán nào.</p>
                                    </c:if>
                                    <c:if test="${not empty payments}">
                                        <div class="table-responsive">
                                            <table class="table table-sm table-bordered mb-0 align-middle">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th>Mã GD</th>
                                                        <th>Loại</th>
                                                        <th>Phương thức</th>
                                                        <th>Số tiền</th>
                                                        <th>Thời gian</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="pay" items="${payments}">
                                                        <tr>
                                                            <td><code>${pay.maGiaoDich}</code></td>
                                                            <td><span class="badge bg-info text-dark">${pay.loaiThanhToan}</span></td>
                                                            <td>${pay.phuongThuc}</td>
                                                            <td class="text-success fw-bold"><fmt:formatNumber value="${pay.soTien}" type="currency" currencySymbol="VND"/></td>
                                                            <td>${pay.ngayThanhToan}</td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="card shadow-sm border-0 rounded-3 bg-white">
                                <div class="card-body text-end">
                                    <p class="mb-1"><strong>Tiền phòng:</strong> <fmt:formatNumber value="${dp.tienPhong}" type="currency" currencySymbol="VND"/></p>
                                    <p class="mb-1"><strong>Tiền dịch vụ:</strong> <fmt:formatNumber value="${dp.tienDichVu}" type="currency" currencySymbol="VND"/></p>
                                    <h4 class="text-danger fw-bold mb-0">Tổng tiền: <fmt:formatNumber value="${dp.tongTien}" type="currency" currencySymbol="VND"/></h4>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </c:if>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
