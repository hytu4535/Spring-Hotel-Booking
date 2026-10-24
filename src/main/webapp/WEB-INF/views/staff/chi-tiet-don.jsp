<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Chi Tiết Đơn Đặt Phòng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff-sidebar.css">
    <style>.admin-operation-page .main-content { margin-left: 280px; width: calc(100% - 280px); }</style>
</head>
<body class="admin-operation-page bg-light">
<div class="admin-wrapper d-flex">
    <c:choose>
        <c:when test="${isAdminView}"><jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp"><jsp:param name="active" value="datphong"/></jsp:include></c:when>
        <c:otherwise><jsp:include page="/WEB-INF/views/common/sidebar-staff.jsp"><jsp:param name="active" value="reservations"/></jsp:include></c:otherwise>
    </c:choose>

    <div class="main-content p-4">
        <a href="${pageContext.request.contextPath}${bookingBasePath}" class="btn btn-link text-decoration-none px-0 mb-3"><i class="bi bi-arrow-left"></i> Quay lại danh sách</a>
        
        <div class="row">
            <!-- Thông tin khách hàng & Đơn -->
            <div class="col-md-4">
                <div class="card shadow-sm border-0 mb-4">
                    <div class="card-header bg-dark text-white fw-bold">Thông tin Đơn #${donDatPhong.maTraCuu}</div>
                    <div class="card-body">
                        <p><strong>Khách hàng:</strong> ${donDatPhong.khachHang.hoTen}</p>
                        <p><strong>SĐT:</strong> ${donDatPhong.khachHang.soDienThoai}</p>
                        <p><strong>Email:</strong> ${donDatPhong.khachHang.email}</p>
                        <hr>
                        <p><strong>Ngày nhận:</strong> <span class="text-success">${donDatPhong.ngayNhanPhong}</span></p>
                        <p><strong>Ngày trả:</strong> <span class="text-danger">${donDatPhong.ngayTraPhong}</span></p>
                        <p class="fs-5 mt-3">Tổng thu: <strong class="text-primary"><fmt:formatNumber value="${donDatPhong.tongTien}" type="currency" currencySymbol="VND"/></strong></p>
                        
                        <div class="mt-4">
                            <!-- NÚT XỬ LÝ (Task #21) -->
                            <c:if test="${donDatPhong.trangThai == 'CHO_XAC_NHAN'}">
                                <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-grid gap-2">
                                    <input type="hidden" name="action" value="update_status"><input type="hidden" name="maDp" value="${donDatPhong.maDp}">
                                    <button type="submit" name="trangThaiMoi" value="DA_XAC_NHAN" class="btn btn-primary"><i class="bi bi-check-circle"></i> Duyệt Đơn</button>
                                    <button type="submit" name="trangThaiMoi" value="DA_HUY" class="btn btn-outline-danger" onclick="return confirm('Chắc chắn hủy?');">Hủy Đơn</button>
                                </form>
                            </c:if>
                            <c:if test="${donDatPhong.trangThai == 'DA_XAC_NHAN'}">
                                <form action="${pageContext.request.contextPath}${bookingBasePath}" method="post" class="d-grid">
                                    <input type="hidden" name="action" value="update_status"><input type="hidden" name="maDp" value="${donDatPhong.maDp}"><input type="hidden" name="trangThaiMoi" value="DA_NHAN_PHONG">
                                    <button type="submit" class="btn btn-success"><i class="bi bi-box-arrow-in-right"></i> Khách Check-in</button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Danh sách phòng đã đặt -->
            <div class="col-md-8">
                <div class="card shadow-sm border-0">
                    <div class="card-header bg-white fw-bold"><i class="bi bi-house-door"></i> Danh sách phòng</div>
                    <div class="table-responsive">
                        <table class="table mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Phòng</th>
                                    <th>Loại Phòng</th>
                                    <th>Giá/Đêm</th>
                                    <th>Đêm</th>
                                    <th>Thành Tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="ct" items="${chiTietList}">
                                    <tr>
                                        <td class="fw-bold">P.${ct.phong.soPhong}</td>
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
        </div>
    </div>
</div>
</body></html>