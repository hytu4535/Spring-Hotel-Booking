<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Dịch vụ - HotelBooking</title>
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
    <jsp:param name="active" value="dich-vu" />
</jsp:include>

<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2><i class="fas fa-concierge-bell me-2"></i>Quản lý Dịch vụ Khách sạn</h2>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addModal">
            <i class="fas fa-plus me-1"></i> Thêm Dịch Vụ Mới
        </button>
    </div>

    <c:if test="${param.success != null}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <c:choose>
                <c:when test="${param.success == 'them'}">Thêm dịch vụ thành công!</c:when>
                <c:when test="${param.success == 'sua'}">Cập nhật dịch vụ thành công!</c:when>
                <c:when test="${param.success == 'xoa'}">Xóa dịch vụ thành công!</c:when>
            </c:choose>
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
                        <th>Tên dịch vụ</th>
                        <th>Đơn giá</th>
                        <th>Đơn vị tính</th>
                        <th>Mô tả</th>
                        <th>Trạng thái</th>
                        <th class="text-end">Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="dv" items="${dichVuList}">
                        <tr>
                            <td>${dv.maDv}</td>
                            <td><strong>${dv.tenDv}</strong></td>
                            <td class="text-success fw-bold">
                                <fmt:formatNumber value="${dv.gia}" type="number" groupingUsed="true"/> đ
                            </td>
                            <td>${dv.donViTinh}</td>
                            <td>${dv.moTa}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${dv.trangThai == 1}">
                                        <span class="badge bg-success">Hoạt động</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">Ngừng hoạt động</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-end">
                                <button class="btn btn-sm btn-outline-primary me-1" 
                                        onclick="openEditModal('${dv.maDv}', '${dv.tenDv}', '${dv.gia}', '${dv.donViTinh}', '${dv.moTa}', '${dv.trangThai}')">
                                    <i class="fas fa-edit"></i> Sửa
                                </button>
                                <form action="${pageContext.request.contextPath}/admin/dich-vu/xoa" method="post" class="d-inline" onsubmit="return confirm('Bạn có chắc chắn muốn xóa dịch vụ này không?');">
                                    <input type="hidden" name="maDv" value="${dv.maDv}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">
                                        <i class="fas fa-trash"></i> Xóa
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty dichVuList}">
                        <tr>
                            <td colspan="7" class="text-center text-muted py-4">Chưa có dịch vụ nào trong hệ thống.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Modal Thêm Dịch Vụ -->
<div class="modal fade" id="addModal" tabindex="-1">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/dich-vu/them" method="post" class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-plus-circle me-2"></i>Thêm Dịch Vụ Mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label class="form-label">Tên dịch vụ <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" name="tenDv" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Đơn giá (VNĐ) <span class="text-danger">*</span></label>
                    <input type="number" step="1000" class="form-control" name="gia" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Đơn vị tính <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" name="donViTinh" value="lần" required placeholder="VD: lần, suất, kg, giờ">
                </div>
                <div class="mb-3">
                    <label class="form-label">Mô tả</label>
                    <textarea class="form-control" name="moTa" rows="3"></textarea>
                </div>
                <div class="mb-3">
                    <label class="form-label">Trạng thái hoạt động</label>
                    <select class="form-select" name="trangThai">
                        <option value="1">Hoạt động</option>
                        <option value="0">Ngừng hoạt động</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-primary">Lưu dịch vụ</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal Sửa Dịch Vụ -->
<div class="modal fade" id="editModal" tabindex="-1">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/dich-vu/sua" method="post" class="modal-content">
            <input type="hidden" id="editMaDv" name="maDv">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-edit me-2"></i>Cập nhật Dịch Vụ</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label class="form-label">Tên dịch vụ <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="editTenDv" name="tenDv" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Đơn giá (VNĐ) <span class="text-danger">*</span></label>
                    <input type="number" step="1000" class="form-control" id="editGia" name="gia" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Đơn vị tính <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="editDonViTinh" name="donViTinh" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Mô tả</label>
                    <textarea class="form-control" id="editMoTa" name="moTa" rows="3"></textarea>
                </div>
                <div class="mb-3">
                    <label class="form-label">Trạng thái hoạt động</label>
                    <select class="form-select" id="editTrangThai" name="trangThai">
                        <option value="1">Hoạt động</option>
                        <option value="0">Ngừng hoạt động</option>
                    </select>
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
    function openEditModal(maDv, tenDv, gia, donViTinh, moTa, trangThai) {
        document.getElementById('editMaDv').value = maDv;
        document.getElementById('editTenDv').value = tenDv;
        document.getElementById('editGia').value = gia;
        document.getElementById('editDonViTinh').value = donViTinh;
        document.getElementById('editMoTa').value = moTa || '';
        document.getElementById('editTrangThai').value = trangThai;

        var editModal = new bootstrap.Modal(document.getElementById('editModal'));
        editModal.show();
    }
</script>
</body>
</html>
