<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Thanh toán & Đối soát - HotelBooking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css">
    <style>
        .main-content {
            margin-left: 250px;
            padding: 20px;
            min-height: 100vh;
            background-color: #f8f9fa;
        }
        @media (max-width: 768px) {
            .main-content { margin-left: 0; }
        }
    </style>
</head>
<body>
<c:choose>
    <c:when test="${isAdminView}">
        <jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp">
            <jsp:param name="active" value="thanh-toan" />
        </jsp:include>
    </c:when>
    <c:otherwise>
        <jsp:include page="/WEB-INF/views/common/sidebar-staff.jsp">
            <jsp:param name="active" value="payments" />
        </jsp:include>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff-sidebar.css">
    </c:otherwise>
</c:choose>

<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2><i class="fas fa-file-invoice-dollar me-2"></i>Quản lý Thanh toán & Đối soát</h2>
        <button type="button" class="btn btn-primary fw-bold shadow-sm" data-bs-toggle="modal" data-bs-target="#addPaymentModal">
            <i class="fas fa-plus-circle me-1"></i> Ghi Nhận Thanh Toán Thủ Công
        </button>
    </div>

    <!-- Alerts -->
    <c:if test="${not empty successMsg}">
        <div class="alert alert-success alert-dismissible fade show fw-bold shadow-sm" role="alert">
            <i class="fas fa-check-circle me-1"></i> ${successMsg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger alert-dismissible fade show fw-bold shadow-sm" role="alert">
            <i class="fas fa-exclamation-triangle me-1"></i> ${errorMsg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Tabs Navigation -->
    <ul class="nav nav-tabs mb-4" id="paymentTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" id="transactions-tab" data-bs-toggle="tab" data-bs-target="#transactions-pane" type="button" role="tab">
                <i class="fas fa-list me-1"></i> Tra cứu Giao dịch
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="reconciliation-tab" data-bs-toggle="tab" data-bs-target="#reconciliation-pane" type="button" role="tab">
                <i class="fas fa-balance-scale me-1"></i> Đối soát Giao dịch Online (VNPAY)
            </button>
        </li>
    </ul>

    <div class="tab-content" id="paymentTabsContent">
        <!-- Tab 1: Tra cứu Giao dịch -->
        <div class="tab-pane fade show active" id="transactions-pane" role="tabpanel">
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}${not empty paymentBasePath ? paymentBasePath : '/admin/thanh-toan'}" class="row g-3">
                        <div class="col-md-3">
                            <label class="form-label">Loại thanh toán</label>
                            <select name="loaiThanhToan" class="form-select">
                                <option value="">-- Tất cả loại --</option>
                                <option value="DAT_COC" ${loaiThanhToan == 'DAT_COC' ? 'selected' : ''}>Đặt cọc (DAT_COC)</option>
                                <option value="THANH_TOAN_DU" ${loaiThanhToan == 'THANH_TOAN_DU' ? 'selected' : ''}>Thanh toán đủ (THANH_TOAN_DU)</option>
                                <option value="HOAN_TIEN" ${loaiThanhToan == 'HOAN_TIEN' ? 'selected' : ''}>Hoàn tiền (HOAN_TIEN)</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Phương thức</label>
                            <select name="phuongThuc" class="form-select">
                                <option value="">-- Tất cả phương thức --</option>
                                <option value="VNPAY" ${phuongThuc == 'VNPAY' ? 'selected' : ''}>VNPAY</option>
                                <option value="TIEN_MAT" ${phuongThuc == 'TIEN_MAT' ? 'selected' : ''}>Tiền mặt</option>
                                <option value="CHUYEN_KHOAN" ${phuongThuc == 'CHUYEN_KHOAN' ? 'selected' : ''}>Chuyển khoản</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Từ khóa (Mã GD / Mã tra cứu)</label>
                            <input type="text" class="form-control" name="keyword" value="${keyword}" placeholder="VD: HTL2610A7X2 hoặc 142536">
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <button type="submit" class="btn btn-primary w-100">
                                <i class="fas fa-search me-1"></i> Lọc
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <div class="card shadow-sm">
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Mã đơn / Tra cứu</th>
                                <th>Loại thanh toán</th>
                                <th>Phương thức</th>
                                <th>Mã giao dịch</th>
                                <th>Số tiền</th>
                                <th>Thời gian</th>
                                <th>Ghi chú</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="t" items="${transactions}">
                                <tr>
                                    <td>${t.maTt}</td>
                                    <td>
                                        <c:if test="${t.datPhong != null}">
                                            <span class="badge bg-secondary">${t.datPhong.maTraCuu}</span>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.loaiThanhToan == 'DAT_COC'}"><span class="badge bg-warning text-dark">Đặt cọc</span></c:when>
                                            <c:when test="${t.loaiThanhToan == 'THANH_TOAN_DU'}"><span class="badge bg-success">Thanh toán đủ</span></c:when>
                                            <c:when test="${t.loaiThanhToan == 'HOAN_TIEN'}"><span class="badge bg-danger">Hoàn tiền</span></c:when>
                                            <c:otherwise><span class="badge bg-info">${t.loaiThanhToan}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><strong>${t.phuongThuc}</strong></td>
                                    <td>${t.maGiaoDich != null ? t.maGiaoDich : 'N/A'}</td>
                                    <td class="fw-bold ${t.loaiThanhToan == 'HOAN_TIEN' ? 'text-danger' : 'text-success'}">
                                        <fmt:formatNumber value="${t.soTien}" type="number" groupingUsed="true"/> đ
                                    </td>
                                    <td><fmt:formatDate value="${t.ngayThanhToan}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                                    <td>${t.ghiChu}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty transactions}">
                                <tr>
                                    <td colspan="8" class="text-center text-muted py-4">Không tìm thấy giao dịch nào.</td>
                                </tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tab 2: Đối soát Giao dịch Online (VNPAY) -->
        <div class="tab-pane fade" id="reconciliation-pane" role="tabpanel">
            <div class="row mb-4">
                <div class="col-md-4">
                    <div class="card stats-card shadow-sm p-3">
                        <h6 class="text-secondary">Tổng GD Online (VNPAY)</h6>
                        <h3 class="text-primary">${reconciliation.totalTransactions}</h3>
                        <small class="text-muted">Giao dịch thành công qua cổng</small>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card stats-card shadow-sm p-3">
                        <h6 class="text-secondary">Tổng doanh thu Online</h6>
                        <h3 class="text-success">
                            <fmt:formatNumber value="${reconciliation.totalOnlineAmount}" type="number" groupingUsed="true"/> đ
                        </h3>
                        <small class="text-muted">Đã ghi nhận vào hệ thống</small>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card stats-card shadow-sm p-3">
                        <h6 class="text-secondary">Trạng thái đối soát</h6>
                        <h3 class="text-info">${reconciliation.reconciliationStatus}</h3>
                        <small class="text-muted">Kiểm tra lúc: <fmt:formatDate value="${reconciliation.lastCheckedTime}" pattern="dd/MM/yyyy HH:mm:ss"/></small>
                    </div>
                </div>
            </div>

            <div class="card shadow-sm">
                <div class="card-header bg-light">
                    <h5 class="mb-0"><i class="fas fa-check-circle text-success me-2"></i>Bảng đối soát chi tiết giao dịch VNPAY Sandbox</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead class="table-light">
                            <tr>
                                <th>Mã GD Cổng (VNPAY)</th>
                                <th>Mã Tra Cứu Đơn</th>
                                <th>Loại</th>
                                <th>Số tiền</th>
                                <th>Thời gian</th>
                                <th>Trạng thái hệ thống</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="vnp" items="${reconciliation.vnpayTransactions}">
                                <tr>
                                    <td><strong>${vnp.maGiaoDich}</strong></td>
                                    <td>
                                        <c:if test="${vnp.datPhong != null}">
                                            <span class="badge bg-secondary">${vnp.datPhong.maTraCuu}</span>
                                        </c:if>
                                    </td>
                                    <td><span class="badge bg-warning text-dark">${vnp.loaiThanhToan}</span></td>
                                    <td class="text-success fw-bold">
                                        <fmt:formatNumber value="${vnp.soTien}" type="number" groupingUsed="true"/> đ
                                    </td>
                                    <td><fmt:formatDate value="${vnp.ngayThanhToan}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                                    <td><span class="badge bg-success">Đã khớp (Matched)</span></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty reconciliation.vnpayTransactions}">
                                <tr>
                                    <td colspan="6" class="text-center text-muted py-4">Chưa có giao dịch VNPAY nào để đối soát.</td>
                                </tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal Ghi Nhận Thanh Toán Thủ Công -->
<div class="modal fade" id="addPaymentModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content shadow-lg border-0">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="fas fa-cash-register me-2"></i>Ghi Nhận Thanh Toán Thủ Công</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body p-4">
                <form action="${pageContext.request.contextPath}${not empty paymentBasePath ? paymentBasePath : '/admin/thanh-toan'}/them" method="post">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Chọn đơn đặt phòng</label>
                        <select name="maDp" class="form-select" required>
                            <option value="">-- Chọn đơn đặt phòng --</option>
                            <c:forEach var="dp" items="${danhSachDon}">
                                <option value="${dp.maDp}">#${dp.maTraCuu} - ${dp.tenKhachHang} (${dp.sdtKhachHang}) - Tổng: <fmt:formatNumber value="${dp.tongTien}" type="number" groupingUsed="true"/>đ</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Số tiền thanh toán (VND)</label>
                        <input type="number" step="0.01" name="soTien" class="form-control" required placeholder="VD: 500000">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Loại thanh toán</label>
                        <select name="loaiThanhToan" class="form-select" required>
                            <option value="DAT_COC">Đặt cọc (DAT_COC)</option>
                            <option value="THANH_TOAN_DU" selected>Thanh toán đủ (THANH_TOAN_DU)</option>
                            <option value="HOAN_TIEN">Hoàn tiền (HOAN_TIEN)</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Phương thức thanh toán</label>
                        <select name="phuongThuc" class="form-select" required>
                            <option value="TIEN_MAT" selected>Tiền mặt (TIEN_MAT)</option>
                            <option value="CHUYEN_KHOAN">Chuyển khoản (CHUYEN_KHOAN)</option>
                            <option value="VNPAY">VNPAY</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Mã giao dịch / Biên lai (Tùy chọn)</label>
                        <input type="text" name="maGiaoDich" class="form-control" placeholder="VD: BL-98765">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Ghi chú</label>
                        <textarea name="ghiChu" class="form-control" rows="2" placeholder="Ghi chú giao dịch...">Thu tiền mặt tại quầy lễ tân</textarea>
                    </div>
                    <div class="text-end">
                        <button type="button" class="btn btn-secondary px-4 fw-bold" data-bs-dismiss="modal">Đóng</button>
                        <button type="submit" class="btn btn-primary px-4 fw-bold shadow-sm">Xác nhận Ghi Nhận</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
