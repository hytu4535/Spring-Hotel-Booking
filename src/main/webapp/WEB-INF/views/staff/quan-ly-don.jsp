<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <title>Quản lý Đơn Đặt Phòng & Dịch Vụ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff-sidebar.css">
    <style>
        .admin-operation-page .admin-wrapper {
            display: block !important;
            position: relative;
        }
        .admin-operation-page .main-content {
            box-sizing: border-box;
            margin-left: 280px !important;
            width: calc(100% - 280px) !important;
            background-color: #f4f6f9;
            min-height: 100vh;
            padding: 24px;
        }
        @media (max-width: 768px) {
            .admin-operation-page .main-content {
                margin-left: 0 !important;
                width: 100% !important;
            }
        }
        .table th {
            background-color: #343a40;
            color: white;
            font-weight: 600;
            white-space: nowrap;
        }
        .table td {
            vertical-align: middle;
        }
        .modal-header {
            background: linear-gradient(135deg, #0d6efd, #0dcaf0);
            color: white;
        }
        .action-btns {
            display: flex;
            gap: 6px;
            justify-content: center;
            align-items: center;
        }
    </style>
</head>
<body class="admin-operation-page">
<div class="admin-wrapper">
    <c:choose>
        <c:when test="${isAdminView}">
            <jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp">
                <jsp:param name="active" value="datphong"/>
            </jsp:include>
        </c:when>
        <c:otherwise>
            <jsp:include page="/WEB-INF/views/common/sidebar-staff.jsp">
                <jsp:param name="active" value="reservations"/>
            </jsp:include>
        </c:otherwise>
    </c:choose>

    <div class="main-content">
        <!-- Top Navbar Header -->
        <div class="top-navbar d-flex justify-content-between align-items-center mb-4 bg-white p-3 rounded-3 shadow-sm">
            <h4 class="mb-0 fw-bold text-primary"><i class="bi bi-journal-check"></i> Quản lý Đơn Đặt Phòng & Dịch Vụ</h4>
            <span class="text-muted small"><i class="bi bi-clock"></i> Cập nhật thời gian thực</span>
        </div>

        <!-- Alerts -->
        <c:if test="${not empty sessionScope.successMsg}">
            <div class="alert alert-success alert-dismissible fade show fw-bold shadow-sm" role="alert">
                <i class="bi bi-check-circle-fill"></i> ${sessionScope.successMsg}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="successMsg" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.errorMsg}">
            <div class="alert alert-danger alert-dismissible fade show fw-bold shadow-sm" role="alert">
                <i class="bi bi-exclamation-triangle-fill"></i> ${sessionScope.errorMsg}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="errorMsg" scope="session"/>
        </c:if>

        <!-- Main Orders Table Card -->
        <div class="card shadow-sm border-0 rounded-3 mb-5">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead>
                            <tr>
                                <th>Mã Tra Cứu</th>
                                <th>Khách Hàng & SĐT</th>
                                <th>Ngày Nhận / Trả</th>
                                <th>Số Phòng</th>
                                <th>Tiền Phòng</th>
                                <th>Tiền DV</th>
                                <th>Tổng Tiền</th>
                                <th>Trạng Thái</th>
                                <th class="text-center">Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="dp" items="${danhSachDon}">
                                <tr>
                                    <td><span class="badge bg-secondary font-monospace">#${dp.maTraCuu}</span></td>
                                    <td>
                                        <div class="fw-bold">${dp.tenKhachHang}</div>
                                        <div class="text-muted small"><i class="bi bi-telephone"></i> ${dp.sdtKhachHang}</div>
                                    </td>
                                    <td>
                                        <div class="text-success fw-bold small"><i class="bi bi-box-arrow-in-right"></i> ${dp.ngayNhanPhong}</div>
                                        <div class="text-danger fw-bold small"><i class="bi bi-box-arrow-right"></i> ${dp.ngayTraPhong}</div>
                                    </td>
                                    <td>
                                        <div class="d-flex flex-wrap gap-1">
                                            <c:forEach var="ct" items="${dp.danhSachChiTiet}">
                                                <span class="badge bg-info text-dark">P.${ct.phong.soPhong}</span>
                                            </c:forEach>
                                        </div>
                                    </td>
                                    <td><fmt:formatNumber value="${dp.tienPhong}" type="currency" currencySymbol="VND"/></td>
                                    <td class="text-success fw-bold"><fmt:formatNumber value="${dp.tienDichVu}" type="currency" currencySymbol="VND"/></td>
                                    <td class="text-danger fw-bold"><fmt:formatNumber value="${dp.tongTien}" type="currency" currencySymbol="VND"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${dp.trangThai == 'CHO_XAC_NHAN'}"><span class="badge bg-warning text-dark px-2 py-1">Chờ XN</span></c:when>
                                            <c:when test="${dp.trangThai == 'DA_XAC_NHAN'}"><span class="badge bg-primary px-2 py-1">Đã Duyệt</span></c:when>
                                            <c:when test="${dp.trangThai == 'DA_NHAN_PHONG'}"><span class="badge bg-success px-2 py-1">Đang Ở</span></c:when>
                                            <c:when test="${dp.trangThai == 'DA_TRA_PHONG'}"><span class="badge bg-secondary px-2 py-1">Đã Trả</span></c:when>
                                            <c:when test="${dp.trangThai == 'DA_HUY'}"><span class="badge bg-danger px-2 py-1">Đã Hủy</span></c:when>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <div class="action-btns">
                                            <!-- Nút Xem Chi Tiết (Icon Con Mắt) -->
                                            <button type="button" class="btn btn-sm btn-info text-white shadow-sm" data-bs-toggle="modal" data-bs-target="#detailModal${dp.maDp}" title="Xem chi tiết đơn">
                                                <i class="bi bi-eye"></i>
                                            </button>

                                            <!-- Các Nút Thay Đổi Trạng Thái -->
                                            <c:choose>
                                                <c:when test="${dp.trangThai == 'CHO_XAC_NHAN'}">
                                                    <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-inline">
                                                        <input type="hidden" name="action" value="update_status">
                                                        <input type="hidden" name="maDp" value="${dp.maDp}">
                                                        <input type="hidden" name="trangThaiMoi" value="DA_XAC_NHAN">
                                                        <button type="submit" class="btn btn-sm btn-primary shadow-sm" title="Xác nhận đơn"><i class="bi bi-check-lg"></i></button>
                                                    </form>
                                                    <c:set var="hasPayment" value="false"/>
                                                    <c:set var="totalPaid" value="0"/>
                                                    <c:forEach var="tt" items="${dp.danhSachThanhToan}">
                                                        <c:if test="${tt.loaiThanhToan != 'HOAN_TIEN'}">
                                                            <c:set var="hasPayment" value="true"/>
                                                            <c:set var="totalPaid" value="${totalPaid + tt.soTien}"/>
                                                        </c:if>
                                                    </c:forEach>
                                                    <c:choose>
                                                        <c:when test="${hasPayment}">
                                                            <button type="button" class="btn btn-sm btn-outline-danger shadow-sm" data-bs-toggle="modal" data-bs-target="#refundModal${dp.maDp}" title="Hủy đơn & Hoàn tiền"><i class="bi bi-x-lg"></i></button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-inline">
                                                                <input type="hidden" name="action" value="update_status">
                                                                <input type="hidden" name="maDp" value="${dp.maDp}">
                                                                <input type="hidden" name="trangThaiMoi" value="DA_HUY">
                                                                <button type="submit" class="btn btn-sm btn-outline-danger shadow-sm" title="Hủy đơn" onclick="return confirm('Bạn có chắc chắn muốn hủy đơn này?');"><i class="bi bi-x-lg"></i></button>
                                                            </form>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:when test="${dp.trangThai == 'DA_XAC_NHAN'}">
                                                    <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-inline">
                                                        <input type="hidden" name="action" value="update_status">
                                                        <input type="hidden" name="maDp" value="${dp.maDp}">
                                                        <input type="hidden" name="trangThaiMoi" value="DA_NHAN_PHONG">
                                                        <button type="submit" class="btn btn-sm btn-success shadow-sm" title="Check-in nhận phòng"><i class="bi bi-box-arrow-in-right"></i></button>
                                                    </form>
                                                    <c:set var="hasPayment" value="false"/>
                                                    <c:set var="totalPaid" value="0"/>
                                                    <c:forEach var="tt" items="${dp.danhSachThanhToan}">
                                                        <c:if test="${tt.loaiThanhToan != 'HOAN_TIEN'}">
                                                            <c:set var="hasPayment" value="true"/>
                                                            <c:set var="totalPaid" value="${totalPaid + tt.soTien}"/>
                                                        </c:if>
                                                    </c:forEach>
                                                    <c:choose>
                                                        <c:when test="${hasPayment}">
                                                            <button type="button" class="btn btn-sm btn-outline-danger shadow-sm" data-bs-toggle="modal" data-bs-target="#refundModal${dp.maDp}" title="Hủy đơn & Hoàn tiền"><i class="bi bi-x-lg"></i></button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-inline">
                                                                <input type="hidden" name="action" value="update_status">
                                                                <input type="hidden" name="maDp" value="${dp.maDp}">
                                                                <input type="hidden" name="trangThaiMoi" value="DA_HUY">
                                                                <button type="submit" class="btn btn-sm btn-outline-danger shadow-sm" title="Hủy đơn" onclick="return confirm('Bạn có chắc chắn muốn hủy đơn này?');"><i class="bi bi-x-lg"></i></button>
                                                            </form>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:when test="${dp.trangThai == 'DA_NHAN_PHONG'}">
                                                    <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-inline">
                                                        <input type="hidden" name="action" value="update_status">
                                                        <input type="hidden" name="maDp" value="${dp.maDp}">
                                                        <input type="hidden" name="trangThaiMoi" value="DA_TRA_PHONG">
                                                        <button type="submit" class="btn btn-sm btn-warning shadow-sm" title="Check-out trả phòng"><i class="bi bi-box-arrow-right"></i></button>
                                                    </form>
                                                </c:when>
                                            </c:choose>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty danhSachDon}">
                                <tr><td colspan="9" class="text-center p-5 text-muted">Chưa có đơn đặt phòng nào trong hệ thống.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- ========================================== -->
<!-- ALL MODALS PLACED OUTSIDE THE TABLE & DOM -->
<!-- ========================================== -->
<c:forEach var="dp" items="${danhSachDon}">
    <div class="modal fade" id="detailModal${dp.maDp}" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-xl modal-dialog-scrollable">
            <div class="modal-content shadow-lg border-0">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold"><i class="bi bi-info-circle-fill"></i> Chi Tiết Đơn Đặt Phòng #${dp.maTraCuu}</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body bg-light p-4">
                    
                    <!-- SECTION 1: THÔNG TIN CHUNG & THANH TOÁN -->
                    <div class="card shadow-sm border-0 mb-4 rounded-3">
                        <div class="card-header bg-white fw-bold text-primary py-3">
                            <i class="bi bi-person-lines-fill"></i> 1. Thông Tin Khách Hàng & Tổng Quan Thanh Toán
                        </div>
                        <div class="card-body">
                            <div class="row g-4">
                                <div class="col-md-4">
                                    <h6 class="text-muted small text-uppercase fw-bold">Thông tin khách hàng</h6>
                                    <p class="mb-1"><strong>Họ tên:</strong> ${dp.tenKhachHang}</p>
                                    <p class="mb-1"><strong>Điện thoại:</strong> ${dp.sdtKhachHang}</p>
                                    <p class="mb-0"><strong>Email:</strong> ${dp.emailKhachHang}</p>
                                </div>
                                <div class="col-md-4 border-start">
                                    <h6 class="text-muted small text-uppercase fw-bold">Thời gian lưu trú</h6>
                                    <p class="mb-1"><strong>Ngày đặt:</strong> <fmt:formatDate value="${dp.ngayDat}" pattern="dd/MM/yyyy HH:mm"/></p>
                                    <p class="mb-1"><strong>Nhận phòng:</strong> <span class="text-success fw-bold">${dp.ngayNhanPhong}</span></p>
                                    <p class="mb-0"><strong>Trả phòng:</strong> <span class="text-danger fw-bold">${dp.ngayTraPhong}</span></p>
                                </div>
                                <div class="col-md-4 border-start bg-light p-3 rounded">
                                    <h6 class="text-muted small text-uppercase fw-bold">Hóa đơn & Nhân viên</h6>
                                    <p class="mb-1"><strong>NV xử lý:</strong> ${not empty dp.tenNhanVienXuLy ? dp.tenNhanVienXuLy : 'Chưa có'}</p>
                                    <p class="mb-1"><strong>Tiền phòng:</strong> <fmt:formatNumber value="${dp.tienPhong}" type="currency" currencySymbol="VND"/></p>
                                    <p class="mb-1"><strong>Tiền dịch vụ:</strong> <fmt:formatNumber value="${dp.tienDichVu}" type="currency" currencySymbol="VND"/></p>
                                    <p class="mb-0 fs-5 fw-bold text-danger">Tổng cộng: <fmt:formatNumber value="${dp.tongTien}" type="currency" currencySymbol="VND"/></p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- SECTION 2: DANH SÁCH PHÒNG ĐÃ ĐẶT -->
                    <div class="card shadow-sm border-0 mb-4 rounded-3">
                        <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
                            <span class="fw-bold text-primary"><i class="bi bi-house-door-fill"></i> 2. Danh Sách Phòng Đã Đặt</span>
                            <button type="button" class="btn btn-sm btn-primary px-3" data-bs-toggle="collapse" data-bs-target="#addRoomCollapse${dp.maDp}">
                                <i class="bi bi-plus-circle"></i> Thêm phòng
                            </button>
                        </div>
                        <div class="card-body p-0">
                            <!-- Form thêm phòng (collapse) -->
                            <div class="collapse p-3 bg-white border-bottom" id="addRoomCollapse${dp.maDp}">
                                <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post">
                                    <input type="hidden" name="action" value="add_phong">
                                    <input type="hidden" name="maDp" value="${dp.maDp}">
                                    <div class="row g-2 align-items-end">
                                        <div class="col-md-8">
                                            <label class="form-label fw-bold small text-muted">Chọn phòng bổ sung</label>
                                            <select name="maPhong" class="form-select" required>
                                                <option value="">-- Chọn phòng --</option>
                                                <c:forEach var="p" items="${danhSachPhong}">
                                                    <option value="${p.maPhong}">Phòng ${p.soPhong} (${p.tenLoai}) - Tầng ${p.tang} - <fmt:formatNumber value="${p.giaPhong}" type="currency" currencySymbol="VND"/>/đêm</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="col-md-4">
                                            <button type="submit" class="btn btn-primary w-100 fw-bold">Xác nhận thêm phòng</button>
                                        </div>
                                    </div>
                                </form>
                            </div>

                            <table class="table mb-0 align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>Số phòng</th>
                                        <th>Loại phòng</th>
                                        <th>Giá / Đêm</th>
                                        <th>Số đêm</th>
                                        <th>Thành tiền</th>
                                        <th class="text-center">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:set var="roomCount" value="0"/>
                                    <c:forEach var="ct" items="${dp.danhSachChiTiet}">
                                        <c:set var="roomCount" value="${roomCount + 1}"/>
                                        <tr>
                                            <td><strong>P.${ct.phong.soPhong}</strong></td>
                                            <td>${ct.phong.loaiPhong.tenLoai}</td>
                                            <td><fmt:formatNumber value="${ct.giaPhong}" type="currency" currencySymbol="VND"/></td>
                                            <td>${ct.soDem}</td>
                                            <td class="text-primary fw-bold"><fmt:formatNumber value="${ct.thanhTien}" type="currency" currencySymbol="VND"/></td>
                                            <td class="text-center">
                                                <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-inline" onsubmit="return validateDeleteRoom(${roomCount});">
                                                    <input type="hidden" name="action" value="delete_phong">
                                                    <input type="hidden" name="maCtdp" value="${ct.maCtdp}">
                                                    <input type="hidden" name="maDp" value="${dp.maDp}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger shadow-sm" title="Xóa phòng" data-bs-toggle="tooltip"><i class="bi bi-trash"></i></button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty dp.danhSachChiTiet}">
                                        <tr><td colspan="6" class="text-center text-muted p-4">Chưa có phòng nào trong đơn này.</td></tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <!-- SECTION 3: DANH SÁCH DỊCH VỤ PHÁT SINH -->
                    <div class="card shadow-sm border-0 rounded-3 mb-2">
                        <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
                            <span class="fw-bold text-success"><i class="bi bi-bag-plus-fill"></i> 3. Danh Sách Dịch Vụ Phát Sinh</span>
                            <button type="button" class="btn btn-sm btn-success px-3" data-bs-toggle="collapse" data-bs-target="#addServiceCollapse${dp.maDp}">
                                <i class="bi bi-plus-circle"></i> Thêm dịch vụ
                            </button>
                        </div>
                        <div class="card-body p-0">
                            <!-- Form thêm dịch vụ (collapse) -->
                            <div class="collapse p-3 bg-white border-bottom" id="addServiceCollapse${dp.maDp}">
                                <form class="add-service-form" action="${pageContext.request.contextPath}${bookingBasePath}" method="post">
                                    <input type="hidden" name="action" value="add_dich_vu">
                                    <input type="hidden" name="maDp" value="${dp.maDp}">
                                    <div class="row g-2 align-items-end">
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold small text-muted">Chọn dịch vụ phát sinh</label>
                                            <select name="maDv" class="form-select" required>
                                                <option value="">-- Chọn dịch vụ --</option>
                                                <c:forEach var="dv" items="${danhSachDichVu}">
                                                    <option value="${dv.maDv}">${dv.tenDv} (<fmt:formatNumber value="${dv.gia}" type="currency" currencySymbol="VND"/>/${dv.donViTinh})</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="col-md-2">
                                            <label class="form-label fw-bold small text-muted">Số lượng</label>
                                            <input type="number" name="soLuong" class="form-control" value="1" min="1" required>
                                        </div>
                                        <div class="col-md-4">
                                            <label class="form-label fw-bold small text-muted">Ngày sử dụng</label>
                                            <input type="date" name="ngaySuDung" class="form-control" required value="<%=new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())%>">
                                        </div>
                                        <div class="col-12 mt-2">
                                            <button type="submit" class="btn btn-success w-100 fw-bold">Xác nhận thêm dịch vụ</button>
                                        </div>
                                    </div>
                                </form>
                            </div>

                            <table class="table mb-0 align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>Tên dịch vụ</th>
                                        <th>Số lượng</th>
                                        <th>Đơn giá</th>
                                        <th>Thành tiền</th>
                                        <th>Nhân viên ghi nhận</th>
                                        <th class="text-center">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="s" items="${dp.danhSachDichVu}">
                                        <tr>
                                            <td><strong>${s.tenDv}</strong></td>
                                            <td>${s.soLuong}</td>
                                            <td><fmt:formatNumber value="${s.giaTaiThoiDiem}" type="currency" currencySymbol="VND"/></td>
                                            <td class="text-success fw-bold"><fmt:formatNumber value="${s.thanhTien}" type="currency" currencySymbol="VND"/></td>
                                            <td><small class="text-muted">${not empty s.tenNhanVien ? s.tenNhanVien : 'Hệ thống'}</small></td>
                                            <td class="text-center">
                                                <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-inline">
                                                    <input type="hidden" name="action" value="delete_dich_vu">
                                                    <input type="hidden" name="maDatDv" value="${s.maDatDv}">
                                                    <input type="hidden" name="maDp" value="${dp.maDp}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger shadow-sm" title="Xóa dịch vụ" onclick="return confirm('Bạn có chắc muốn xóa dịch vụ này?');"><i class="bi bi-trash"></i></button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty dp.danhSachDichVu}">
                                        <tr><td colspan="6" class="text-center text-muted p-4">Chưa có dịch vụ phát sinh nào trong đơn này.</td></tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <!-- SECTION 4: LỊCH SỬ THANH TOÁN & HOÀN TIỀN -->
                    <div class="card shadow-sm border-0 rounded-3 mb-2">
                        <div class="card-header bg-white fw-bold text-dark py-3">
                            <i class="bi bi-wallet2"></i> 4. Lịch Sử Thanh Toán & Hoàn Tiền
                        </div>
                        <div class="card-body p-0">
                            <table class="table mb-0 align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>Thời gian</th>
                                        <th>Loại</th>
                                        <th>Phương thức</th>
                                        <th>Mã GD</th>
                                        <th>Ghi chú</th>
                                        <th class="text-end">Số tiền</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="tt" items="${dp.danhSachThanhToan}">
                                        <tr>
                                            <td><fmt:formatDate value="${tt.ngayThanhToan}" pattern="dd/MM/yyyy HH:mm"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${tt.loaiThanhToan == 'DAT_COC'}"><span class="badge bg-warning text-dark">Đặt cọc</span></c:when>
                                                    <c:when test="${tt.loaiThanhToan == 'THANH_TOAN_DU'}"><span class="badge bg-success">Thanh toán đủ</span></c:when>
                                                    <c:when test="${tt.loaiThanhToan == 'HOAN_TIEN'}"><span class="badge bg-danger">Hoàn tiền</span></c:when>
                                                </c:choose>
                                            </td>
                                            <td>${tt.phuongThuc}</td>
                                            <td><code>${not empty tt.maGiaoDich ? tt.maGiaoDich : 'N/A'}</code></td>
                                            <td>${tt.ghiChu}</td>
                                            <td class="text-end fw-bold ${tt.loaiThanhToan == 'HOAN_TIEN' ? 'text-danger' : 'text-success'}">
                                                ${tt.loaiThanhToan == 'HOAN_TIEN' ? '-' : '+'}<fmt:formatNumber value="${tt.soTien}" type="currency" currencySymbol="VND"/>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty dp.danhSachThanhToan}">
                                        <tr><td colspan="6" class="text-center text-muted p-3">Chưa có giao dịch thanh toán nào.</td></tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>

                </div>
                <div class="modal-footer bg-white py-3 justify-content-between">
                    <a href="${pageContext.request.contextPath}${isAdminView ? '/admin' : '/staff'}/hoa-don?maDp=${dp.maDp}" target="_blank" class="btn btn-warning text-dark px-4 fw-bold shadow-sm">
                        <i class="bi bi-receipt"></i> Xem & In Hóa Đơn
                    </a>
                    <button type="button" class="btn btn-secondary px-4 fw-bold" data-bs-dismiss="modal">Đóng</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal Xác nhận Hoàn tiền & Hủy đơn -->
    <div class="modal fade" id="refundModal${dp.maDp}" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content shadow-lg border-0">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title fw-bold"><i class="bi bi-cash-coin"></i> Xác Nhận Hoàn Tiền & Hủy Đơn #${dp.maTraCuu}</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <form id="refundForm${dp.maDp}" class="refund-form" data-map="${dp.maDp}">
                        <c:set var="totalPaid" value="0"/>
                        <c:forEach var="tt" items="${dp.danhSachThanhToan}">
                            <c:if test="${tt.loaiThanhToan != 'HOAN_TIEN'}">
                                <c:set var="totalPaid" value="${totalPaid + tt.soTien}"/>
                            </c:if>
                        </c:forEach>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Số tiền hoàn lại (VND)</label>
                            <input type="number" step="0.01" name="soTienHoan" class="form-control" required value="${totalPaid}" />
                            <div class="form-text text-muted">Tổng số tiền khách đã thanh toán: <fmt:formatNumber value="${totalPaid}" type="currency" currencySymbol="VND"/></div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Phương thức hoàn tiền</label>
                            <select name="phuongThuc" class="form-select" required>
                                <option value="TIEN_MAT">Tiền mặt</option>
                                <option value="CHUYEN_KHOAN">Chuyển khoản</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Lý do hoàn tiền / Hủy đơn</label>
                            <textarea name="ghiChu" class="form-control" rows="3" placeholder="Nhập lý do hủy đơn và hoàn tiền..." required>Hủy đơn đặt phòng và hoàn tiền cọc</textarea>
                        </div>
                        <div class="text-end">
                            <button type="button" class="btn btn-secondary px-4 fw-bold" data-bs-dismiss="modal">Đóng</button>
                            <button type="submit" class="btn btn-danger px-4 fw-bold shadow-sm">Xác nhận Hoàn Tiền & Hủy</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</c:forEach>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // CSRF Token setup if present
    const csrfToken = $('meta[name="_csrf"]').attr('content');
    const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
    if (csrfToken && csrfHeader) {
        $.ajaxSetup({
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        });
    }

    $(document).on('submit', '.add-service-form', function(e) {
        e.preventDefault();
        const form = $(this);
        const maDp = form.find('input[name="maDp"]').val();
        const maDv = form.find('select[name="maDv"]').val();
        const soLuong = form.find('input[name="soLuong"]').val();
        const ngaySuDung = form.find('input[name="ngaySuDung"]').val();

        $.ajax({
            url: '${pageContext.request.contextPath}' + (window.location.pathname.startsWith('/admin') ? '/admin' : '/staff') + '/api/them-dich-vu',
            type: 'POST',
            data: {
                maDp: maDp,
                maDv: maDv,
                soLuong: soLuong,
                ngaySuDung: ngaySuDung
            },
            success: function(response) {
                if (response.success) {
                    alert(response.message || 'Thêm dịch vụ thành công!');
                    location.reload();
                } else {
                    alert('Lỗi: ' + (response.message || 'Không thể thêm dịch vụ'));
                }
            },
            error: function(xhr, status, error) {
                console.log("AJAX Error:", error, xhr.responseText);
                let errMessage = "Lỗi khi thêm dịch vụ phát sinh!";
                try {
                    let res = JSON.parse(xhr.responseText);
                    if (res.message) errMessage = res.message;
                } catch (e) {
                    if (xhr.responseText) errMessage = xhr.responseText;
                }
                alert(errMessage);
            }
        });
    });

    function validateDeleteRoom(roomCount) {
        if (roomCount <= 1) {
            alert('Không thể xóa! Đơn đặt phòng phải có ít nhất 1 phòng.');
            return false;
        }
        return confirm('Bạn có chắc muốn xóa phòng này khỏi đơn?');
    }

    $(document).on('submit', '.refund-form', function(e) {
        e.preventDefault();
        const form = $(this);
        const maDp = form.data('map');
        const soTienHoan = form.find('input[name="soTienHoan"]').val();
        const phuongThuc = form.find('select[name="phuongThuc"]').val();
        const ghiChu = form.find('textarea[name="ghiChu"]').val();

        if (!confirm('Bạn có chắc chắn muốn thực hiện hoàn tiền và hủy đơn này?')) return;

        $.ajax({
            url: '${pageContext.request.contextPath}' + (window.location.pathname.startsWith('/admin') ? '/admin' : '/staff') + '/api/hoan-tien',
            type: 'POST',
            data: {
                maDp: maDp,
                soTienHoan: soTienHoan,
                phuongThuc: phuongThuc,
                ghiChu: ghiChu
            },
            success: function(response) {
                if (response.success) {
                    alert(response.message || 'Hoàn tiền và hủy đơn thành công!');
                    location.reload();
                } else {
                    alert('Lỗi: ' + (response.message || 'Không thể hoàn tiền'));
                }
            },
            error: function(xhr) {
                let err = "Lỗi khi thực hiện hoàn tiền!";
                try {
                    let res = JSON.parse(xhr.responseText);
                    if (res.message) err = res.message;
                } catch (e) {
                    if (xhr.responseText) err = xhr.responseText;
                }
                alert(err);
            }
        });
    });
</script>
</body>
</html>
