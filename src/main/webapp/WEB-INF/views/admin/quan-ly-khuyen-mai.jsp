<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Khuyến mãi - HotelBooking</title>
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
<jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp">
    <jsp:param name="active" value="khuyenmai" />
</jsp:include>

<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2><i class="fas fa-tags me-2"></i>Quản lý Mã Giảm Giá & Khuyến Mãi</h2>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addModal">
            <i class="fas fa-plus me-1"></i> Tạo Mã Khuyến Mãi
        </button>
    </div>

    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <div class="card shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Mã Code</th>
                        <th>Tên Chương Trình</th>
                        <th>Mức Giảm</th>
                        <th>Thời Gian Áp Dụng</th>
                        <th>Điều Kiện Sử Dụng</th>
                        <th>Trạng Thái</th>
                        <th class="text-end">Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="p" items="${promotions}">
                        <tr>
                            <td>${p.id}</td>
                            <td><span class="badge bg-primary fs-6">${p.maCode}</span></td>
                            <td><strong>${p.tenKhuyenMai}</strong></td>
                            <td class="text-success fw-bold">${p.mucGiam}</td>
                            <td>${p.ngayBatDau} → ${p.ngayKetThuc}</td>
                            <td>${p.dieuKien}</td>
                            <td><span class="badge bg-success">${p.trangThai}</span></td>
                            <td class="text-end">
                                <button class="btn btn-sm btn-outline-primary me-1" 
                                        onclick="openEditModal('${p.id}', '${p.maCode}', '${p.tenKhuyenMai}', '${p.mucGiam}', '${p.ngayBatDau}', '${p.ngayKetThuc}', '${p.dieuKien}')">
                                    <i class="fas fa-edit"></i> Sửa
                                </button>
                                <form action="${pageContext.request.contextPath}/admin/khuyen-mai/xoa" method="post" class="d-inline" onsubmit="return confirm('Bạn có chắc chắn muốn xóa mã này?');">
                                    <input type="hidden" name="id" value="${p.id}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">
                                        <i class="fas fa-trash"></i> Xóa
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty promotions}">
                        <tr>
                            <td colspan="8" class="text-center text-muted py-4">Chưa có mã khuyến mãi nào.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Modal Tạo Mã -->
<div class="modal fade" id="addModal" tabindex="-1">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/khuyen-mai/them" method="post" class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-plus-circle me-2"></i>Tạo Mã Khuyến Mãi Mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label class="form-label">Mã Code <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" name="maCode" required placeholder="VD: SUMMER2026">
                </div>
                <div class="mb-3">
                    <label class="form-label">Tên chương trình <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" name="tenKhuyenMai" required placeholder="VD: Giảm giá mùa hè">
                </div>
                <div class="mb-3">
                    <label class="form-label">Mức giảm <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" name="mucGiam" required placeholder="VD: 15% hoặc 100,000đ">
                </div>
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" name="ngayBatDau" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" name="ngayKetThuc" required>
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label">Điều kiện sử dụng</label>
                    <textarea class="form-control" name="dieuKien" rows="3" placeholder="VD: Áp dụng đơn từ 1,000,000đ"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-primary">Tạo mã</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal Sửa Mã -->
<div class="modal fade" id="editModal" tabindex="-1">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/khuyen-mai/sua" method="post" class="modal-content">
            <input type="hidden" id="editId" name="id">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-edit me-2"></i>Cập nhật Mã Khuyến Mãi</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label class="form-label">Mã Code <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="editMaCode" name="maCode" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Tên chương trình <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="editTenKhuyenMai" name="tenKhuyenMai" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Mức giảm <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="editMucGiam" name="mucGiam" required>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" id="editNgayBatDau" name="ngayBatDau" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" id="editNgayKetThuc" name="ngayKetThuc" required>
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label">Điều kiện sử dụng</label>
                    <textarea class="form-control" id="editDieuKien" name="dieuKien" rows="3"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-primary">Cập nhật</button>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function openEditModal(id, maCode, tenKhuyenMai, mucGiam, ngayBatDau, ngayKetThuc, dieuKien) {
        document.getElementById('editId').value = id;
        document.getElementById('editMaCode').value = maCode;
        document.getElementById('editTenKhuyenMai').value = tenKhuyenMai;
        document.getElementById('editMucGiam').value = mucGiam;
        document.getElementById('editNgayBatDau').value = ngayBatDau;
        document.getElementById('editNgayKetThuc').value = ngayKetThuc;
        document.getElementById('editDieuKien').value = dieuKien || '';

        var editModal = new bootstrap.Modal(document.getElementById('editModal'));
        editModal.show();
    }
</script>
</body>
</html>
