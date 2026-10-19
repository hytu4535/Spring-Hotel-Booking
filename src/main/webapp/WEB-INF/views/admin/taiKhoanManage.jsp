<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý tài khoản - HotelBooking Admin</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-sidebar.css">

    <style>
        :root {
            --primary: #0d6efd;
            --secondary: #6c757d;
            --success: #198754;
            --info: #0dcaf0;
            --warning: #ffc107;
            --danger: #dc3545;
            --light: #f8f9fa;
            --dark: #212529;
            --sidebar-width: 250px;
        }

        body {
            background-color: var(--light);
        }

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
            overflow: hidden;
        }

        .sidebar .header {
            padding: 20px;
            text-align: center;
            border-bottom: 1px solid rgba(255,255,255,0.1);
            flex-shrink: 0;
        }

        .sidebar .header h3 {
            margin: 0;
            font-size: 1.5rem;
        }

        .sidebar-menu {
            flex: 1;
            overflow-y: auto;
            overflow-x: hidden;
            padding-bottom: 10px;
        }

        .sidebar-menu::-webkit-scrollbar {
            width: 4px;
        }
        .sidebar-menu::-webkit-scrollbar-track {
            background: transparent;
        }
        .sidebar-menu::-webkit-scrollbar-thumb {
            background: rgba(255,255,255,0.2);
            border-radius: 4px;
        }

        .sidebar a {
            color: white;
            text-decoration: none;
            padding: 12px 20px;
            display: block;
        }

        .sidebar a:hover,
        .sidebar a.active {
            background-color: var(--primary);
        }

        .sidebar-footer {
            padding: 15px 20px;
            border-top: 1px solid rgba(255,255,255,0.1);
            flex-shrink: 0;
        }

        .main-content {
            margin-left: var(--sidebar-width);
            padding: 20px;
            min-height: 100vh;
            background-color: var(--light);
        }

        .status-badge {
            font-size: 0.8rem;
            padding: 0.35em 0.65em;
            border-radius: 0.25rem;
        }
        .status-active {
            background-color: #d4edda;
            color: #155724;
        }
        .status-locked {
            background-color: #f8d7da;
            color: #721c24;
        }

        .table thead th {
            white-space: nowrap;
        }

        @media (max-width: 768px) {
            .sidebar {
                width: 100%;
                height: auto;
                position: relative;
                overflow: visible;
            }
            .main-content {
                margin-left: 0;
            }
            .sidebar .header {
                display: none;
            }
        }
    </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/common/sidebar-admin.jsp">
    <jsp:param name="active" value="taikhoan" />
</jsp:include>

<!-- MAIN CONTENT -->
<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0"><i class="fas fa-user-cog me-2"></i>Quản lý tài khoản</h2>
        <div class="d-flex gap-2">
            <button type="button" class="btn btn-outline-secondary btn-sm" onclick="location.reload()">
                <i class="fas fa-sync-alt"></i> Làm mới
            </button>
            <button type="button" class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#createAccountModal">
                <i class="fas fa-user-plus"></i> Tạo tài khoản
            </button>
        </div>
    </div>

    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show">
            <i class="fas fa-check-circle me-2"></i>${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show">
            <i class="fas fa-times-circle me-2"></i>${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="card shadow-sm">
        <div class="card-header bg-white fw-semibold">
            Danh sách tài khoản
            <span class="badge bg-secondary ms-2">${taiKhoanList.size()}</span>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover table-striped mb-0 align-middle">
                    <thead class="table-dark">
                        <tr>
                            <th style="width:50px">#</th>
                            <th>Tên đăng nhập</th>
                            <th>Họ tên</th>
                            <th>Email</th>
                            <th>Số điện thoại</th>
                            <th>Vai trò</th>
                            <th>Trạng thái</th>
                            <th>Ngày tạo</th>
                            <th class="text-center" style="width:120px">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="tk" items="${taiKhoanList}" varStatus="st">
                            <tr>
                                <td>${st.index + 1}</td>
                                <td class="fw-semibold">${tk.tenDangNhap}</td>
                                <td>${tk.hoTen}</td>
                                <td>${tk.email}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty tk.soDienThoai}">${tk.soDienThoai}</c:when>
                                        <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <span class="badge ${tk.vaiTro == 'ADMIN' ? 'bg-primary' : 'bg-success'}">
                                        ${tk.vaiTro == 'ADMIN' ? 'Quản trị' : 'Nhân viên'}
                                    </span>
                                </td>
                                <td>
                                    <span class="status-badge ${tk.trangThai == 1 ? 'status-active' : 'status-locked'}">
                                        ${tk.trangThai == 1 ? 'Hoạt động' : 'Bị khóa'}
                                    </span>
                                </td>
                                <td>
                                    <fmt:formatDate value="${tk.ngayTao}" pattern="dd/MM/yyyy HH:mm"/>
                                </td>
                                <td class="text-center">
                                    <div class="d-flex justify-content-center gap-1">
                                        <!-- Khóa / Mở khóa -->
                                        <form method="post" action="${pageContext.request.contextPath}/admin/tai-khoan" class="d-inline">
                                            <input type="hidden" name="action" value="${tk.trangThai == 1 ? 'lock' : 'unlock'}">
                                            <input type="hidden" name="maTk" value="${tk.maTk}">
                                            <button type="submit"
                                                    class="btn btn-sm ${tk.trangThai == 1 ? 'btn-outline-danger' : 'btn-outline-success'}"
                                                    title="${tk.trangThai == 1 ? 'Khóa' : 'Mở khóa'}"
                                                    onclick="return confirm('Bạn có chắc muốn ${tk.trangThai == 1 ? 'khóa' : 'mở khóa'} tài khoản này?');">
                                                <i class="fas fa-${tk.trangThai == 1 ? 'lock' : 'unlock'}"></i>
                                            </button>
                                        </form>

                                        <!-- Đổi vai trò -->
                                        <form method="post" action="${pageContext.request.contextPath}/admin/tai-khoan" class="d-inline">
                                            <input type="hidden" name="action" value="role">
                                            <input type="hidden" name="maTk" value="${tk.maTk}">
                                            <button type="submit"
                                                    class="btn btn-sm btn-outline-info"
                                                    title="Đổi vai trò"
                                                    onclick="return confirm('Bạn có chắc muốn đổi vai trò tài khoản này?');">
                                                <i class="fas fa-exchange-alt"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty taiKhoanList}">
                            <tr>
                                <td colspan="9" class="text-center text-muted py-4">
                                    <i class="fas fa-info-circle me-1"></i> Không có tài khoản nào trong hệ thống.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- MODAL TẠO TÀI KHOẢN -->
<div class="modal fade" id="createAccountModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="post" action="${pageContext.request.contextPath}/admin/tai-khoan">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="fas fa-user-plus me-2"></i>Tạo tài khoản nhân viên</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="action" value="create">

                    <div class="mb-3">
                        <label class="form-label">Tên đăng nhập <span class="text-danger">*</span></label>
                        <input type="text" name="tenDangNhap" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Mật khẩu <span class="text-danger">*</span></label>
                        <input type="password" name="matKhau" class="form-control" minlength="6" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Họ tên <span class="text-danger">*</span></label>
                        <input type="text" name="hoTen" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Email <span class="text-danger">*</span></label>
                        <input type="email" name="email" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Số điện thoại</label>
                        <input type="tel" name="soDienThoai" class="form-control">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Vai trò</label>
                        <select name="vaiTro" class="form-select" required>
                            <option value="NHANVIEN" selected>Nhân viên</option>
                        </select>
                        <div class="form-text">Chỉ được tạo tài khoản nhân viên</div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Tạo tài khoản</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>