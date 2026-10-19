<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${isEdit ? 'Sửa loại phòng' : 'Thêm loại phòng'}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css">
    <style>
        :root {
            --primary: #0d6efd;
            --light: #f8f9fa;
            --dark: #212529;
            --sidebar-width: 250px;
        }
        body { background-color: var(--light); }
        .sidebar {
            background-color: var(--dark);
            color: white;
            height: 100vh;
            position: fixed;
            top: 0;
            left: 0;
            width: var(--sidebar-width);
            display: flex;
            flex-direction: column;
            z-index: 1000;
        }
        .sidebar .header {
            padding: 20px;
            text-align: center;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        .sidebar-menu { flex: 1; overflow-y: auto; }
        .sidebar a {
            color: white;
            text-decoration: none;
            padding: 12px 20px;
            display: block;
        }
        .sidebar a:hover, .sidebar a.active { background-color: var(--primary); }
        .sidebar-footer { padding: 15px 20px; border-top: 1px solid rgba(255,255,255,0.1); }
        .main-content {
            margin-left: var(--sidebar-width);
            padding: 20px;
            min-height: 100vh;
        }
        @media (max-width: 768px) {
            .sidebar { width: 100%; height: auto; position: relative; }
            .main-content { margin-left: 0; }
            .sidebar .header { display: none; }
        }
    </style>
</head>
<body class="bg-light">
<jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp">
    <jsp:param name="active" value="loaiphong" />
</jsp:include>

<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0"><i class="fas fa-layer-group me-2"></i>${isEdit ? 'Sửa loại phòng' : 'Thêm loại phòng'}</h2>
        <a href="${pageContext.request.contextPath}/admin/loai-phong" class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left me-1"></i> Danh sách
        </a>
    </div>

    <c:if test="${not empty globalError}">
        <div class="alert alert-danger">${globalError}</div>
    </c:if>

    <div class="card shadow-sm">
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}${formAction}">
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">Tên loại phòng <span class="text-danger">*</span></label>
                        <input type="text" name="tenLoai" class="form-control ${not empty fieldErrors['tenLoai'] ? 'is-invalid' : ''}" value="${formData.tenLoai}" required maxlength="100">
                        <c:if test="${not empty fieldErrors['tenLoai']}">
                            <div class="invalid-feedback d-block">${fieldErrors['tenLoai']}</div>
                        </c:if>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Hình ảnh</label>
                        <input type="text" name="hinhAnh" class="form-control ${not empty fieldErrors['hinhAnh'] ? 'is-invalid' : ''}" value="${formData.hinhAnh}" maxlength="255">
                        <c:if test="${not empty fieldErrors['hinhAnh']}">
                            <div class="invalid-feedback d-block">${fieldErrors['hinhAnh']}</div>
                        </c:if>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Giá mặc định <span class="text-danger">*</span></label>
                        <input type="number" name="giaMacDinh" class="form-control ${not empty fieldErrors['giaMacDinh'] ? 'is-invalid' : ''}" value="${formData.giaMacDinh}" min="1" step="0.01" required>
                        <c:if test="${not empty fieldErrors['giaMacDinh']}">
                            <div class="invalid-feedback d-block">${fieldErrors['giaMacDinh']}</div>
                        </c:if>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Sức chứa <span class="text-danger">*</span></label>
                        <input type="number" name="sucChua" class="form-control ${not empty fieldErrors['sucChua'] ? 'is-invalid' : ''}" value="${formData.sucChua}" min="1" required>
                        <c:if test="${not empty fieldErrors['sucChua']}">
                            <div class="invalid-feedback d-block">${fieldErrors['sucChua']}</div>
                        </c:if>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Diện tích (m²)</label>
                        <input type="number" name="dienTich" class="form-control ${not empty fieldErrors['dienTich'] ? 'is-invalid' : ''}" value="${formData.dienTich}" min="0.01" step="0.01">
                        <c:if test="${not empty fieldErrors['dienTich']}">
                            <div class="invalid-feedback d-block">${fieldErrors['dienTich']}</div>
                        </c:if>
                    </div>
                    <div class="col-12">
                        <label class="form-label">Mô tả</label>
                        <textarea name="moTa" class="form-control ${not empty fieldErrors['moTa'] ? 'is-invalid' : ''}" rows="4">${formData.moTa}</textarea>
                        <c:if test="${not empty fieldErrors['moTa']}">
                            <div class="invalid-feedback d-block">${fieldErrors['moTa']}</div>
                        </c:if>
                    </div>
                </div>

                <div class="mt-4 d-flex justify-content-end gap-2">
                    <a href="${pageContext.request.contextPath}/admin/loai-phong" class="btn btn-outline-secondary">Hủy</a>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-1"></i>${isEdit ? 'Cập nhật' : 'Tạo mới'}
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
