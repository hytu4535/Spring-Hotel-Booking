<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Sơ Đồ Phòng Lễ Tân</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff-sidebar.css">
    <style>
        .room-box { border-radius: 8px; padding: 15px; min-height: 150px; color: white; box-shadow: 0 4px 6px rgba(0,0,0,0.1); display:flex; flex-direction: column; justify-content: space-between;}
        .room-TRONG { background: #198754; } 
        .room-DA_DAT { background: #ffc107; color: #000; } 
        .room-DANG_O { background: #dc3545; } 
        .room-BAO_TRI { background: #6c757d; }
    </style>
</head>
<body class="admin-operation-page">
<div class="admin-wrapper d-flex">
    <c:choose>
        <c:when test="${isAdminView}"><jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp"><jsp:param name="active" value="nhantraphong"/></jsp:include></c:when>
        <c:otherwise><jsp:include page="/WEB-INF/views/common/sidebar-staff.jsp"><jsp:param name="active" value="frontdesk"/></jsp:include></c:otherwise>
    </c:choose>

    <div class="main-content w-100 p-4">
        <h4 class="mb-3 fw-bold text-primary"><i class="bi bi-grid-3x3-gap"></i> Sơ Đồ Khách Sạn</h4>
        
        <!-- Thanh công cụ (Bộ lọc trạng thái & tầng) -->
        <div class="card shadow-sm mb-4 border-0">
            <div class="card-body d-flex justify-content-between align-items-center flex-wrap gap-2">
                <form class="d-flex gap-2" method="get">
                    <input type="text" class="form-control" name="soPhong" placeholder="Tìm số phòng...">
                    <select name="tang" class="form-select"><option value="">Tất cả các tầng</option><option value="1">Tầng 1</option><option value="2">Tầng 2</option></select>
                    <button class="btn btn-primary"><i class="bi bi-search"></i> Lọc</button>
                </form>
                <div>
                    <span class="badge bg-success p-2">Trống</span>
                    <span class="badge bg-warning text-dark p-2">Sắp Check-in</span>
                    <span class="badge bg-danger p-2">Đang Ở</span>
                    <span class="badge bg-secondary p-2">Bảo Trì</span>
                </div>
            </div>
        </div>

        <c:if test="${not empty successMsg}"><div class="alert alert-success fw-bold">${successMsg}</div></c:if>

        <div class="row g-4">
            <c:forEach var="p" items="${danhSachPhong}">
                <div class="col-xl-2 col-lg-3 col-md-4 col-sm-6">
                    <div class="room-box room-${p.trangThai}">
                        <div class="d-flex justify-content-between">
                            <span class="fs-4 fw-bold">${p.soPhong}</span>
                            <span class="badge bg-light text-dark align-self-start">T${p.tang}</span>
                        </div>
                        <div class="small opacity-75">${p.tenLoaiPhong}</div>

                        <c:if test="${not empty p.tenKhachHang}">
                            <div class="mt-2 fw-bold small"><i class="bi bi-person-fill"></i> ${p.tenKhachHang}</div>
                        </c:if>

                        <div class="mt-3">
                            <c:if test="${p.trangThai == 'DA_DAT' && not empty p.maDpHienTai}">
                                <form action="${pageContext.request.contextPath}${roomBasePath}" method="post">
                                    <input type="hidden" name="action" value="check_in"><input type="hidden" name="maDp" value="${p.maDpHienTai}">
                                    <button type="submit" class="btn btn-sm btn-dark w-100 fw-bold shadow-sm" onclick="return confirm('Tiến hành Check-in phòng ${p.soPhong}?');"><i class="bi bi-box-arrow-in-right"></i> Nhận phòng</button>
                                </form>
                            </c:if>
                            
                            <c:if test="${p.trangThai == 'DANG_O' && not empty p.maDpHienTai}">
                                <!-- Task #24: Check-out và chuyển qua màn hình thanh toán -->
                                <form action="${pageContext.request.contextPath}${roomBasePath}" method="post">
                                    <input type="hidden" name="action" value="check_out"><input type="hidden" name="maDp" value="${p.maDpHienTai}">
                                    <button type="submit" class="btn btn-sm btn-light text-danger w-100 fw-bold shadow-sm" onclick="return confirm('Chốt đơn và tiến hành Trả phòng ${p.soPhong}?');"><i class="bi bi-box-arrow-right"></i> Trả phòng</button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>
</body></html>