<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hóa Đơn Thanh Toán - #${don.maTraCuu}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: #333;
        }
        .invoice-container {
            max-width: 850px;
            margin: 40px auto;
            background: #fff;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 0 20px rgba(0,0,0,0.08);
        }
        .invoice-header {
            border-bottom: 2px solid #0d6efd;
            padding-bottom: 20px;
            margin-bottom: 30px;
        }
        .hotel-title {
            font-size: 24px;
            font-weight: 700;
            color: #0d6efd;
        }
        .invoice-title {
            font-size: 28px;
            font-weight: 700;
            text-transform: uppercase;
            color: #343a40;
        }
        .table th {
            background-color: #f1f3f5 !important;
            color: #495057;
            font-weight: 600;
        }
        .total-section {
            background-color: #f8f9fa;
            border-radius: 6px;
            padding: 20px;
        }
        @media print {
            body {
                background-color: #fff;
            }
            .invoice-container {
                box-shadow: none;
                margin: 0;
                padding: 10px;
                width: 100%;
                max-width: 100%;
            }
            .no-print {
                display: none !important;
            }
            .btn {
                display: none !important;
            }
        }
    </style>
</head>
<body>

<div class="container">
    <div class="no-print text-end mb-3 mt-4">
        <button onclick="window.print()" class="btn btn-primary fw-bold px-4 shadow-sm">
            <i class="bi bi-printer-fill"></i> In Hóa Đơn / Lưu PDF
        </button>
        <a href="javascript:window.close();" class="btn btn-secondary fw-bold px-4 shadow-sm ms-2">
            <i class="bi bi-x-circle"></i> Đóng
        </a>
    </div>

    <div class="invoice-container">
        <!-- Header -->
        <div class="invoice-header d-flex justify-content-between align-items-center">
            <div>
                <div class="hotel-title"><i class="bi bi-building"></i> HOTEL BOOKING SYSTEM</div>
                <p class="text-muted small mb-0">Địa chỉ: 123 Đường Hồ Chí Minh, TP.HCM</p>
                <p class="text-muted small mb-0">Hotline: 1900 6868 | Email: support@hotelbooking.com</p>
            </div>
            <div class="text-end">
                <div class="invoice-title">HÓA ĐƠN THANH TOÁN</div>
                <p class="text-muted small mb-0">Mã tra cứu: <strong class="text-dark">#${don.maTraCuu}</strong></p>
                <p class="text-muted small mb-0">Ngày tạo: <fmt:formatDate value="${don.ngayDat}" pattern="dd/MM/yyyy HH:mm"/></p>
            </div>
        </div>

        <!-- Customer & Stay Info -->
        <div class="row mb-4">
            <div class="col-6">
                <h6 class="text-uppercase text-muted fw-bold small">Thông tin khách hàng:</h6>
                <p class="mb-1"><strong>Họ tên:</strong> ${don.tenKhachHang}</p>
                <p class="mb-1"><strong>Số điện thoại:</strong> ${don.sdtKhachHang}</p>
                <p class="mb-0"><strong>Email:</strong> ${don.emailKhachHang}</p>
            </div>
            <div class="col-6 text-end">
                <h6 class="text-uppercase text-muted fw-bold small">Thời gian lưu trú:</h6>
                <p class="mb-1"><strong>Nhận phòng:</strong> ${don.ngayNhanPhong}</p>
                <p class="mb-1"><strong>Trả phòng:</strong> ${don.ngayTraPhong}</p>
                <p class="mb-0"><strong>Trạng thái đơn:</strong> 
                    <span class="badge bg-secondary">${don.trangThai}</span>
                </p>
            </div>
        </div>

        <!-- Room Details Table -->
        <h5 class="fw-bold text-primary mb-3"><i class="bi bi-house-door"></i> Chi tiết tiền phòng</h5>
        <div class="table-responsive mb-4">
            <table class="table table-bordered align-middle">
                <thead>
                    <tr>
                        <th>Số phòng</th>
                        <th>Loại phòng</th>
                        <th>Đơn giá / Đêm</th>
                        <th>Số đêm</th>
                        <th class="text-end">Thành tiền</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="ct" items="${don.danhSachChiTiet}">
                        <tr>
                            <td><strong>P.${ct.phong.soPhong}</strong></td>
                            <td>${ct.phong.loaiPhong.tenLoai}</td>
                            <td><fmt:formatNumber value="${ct.giaPhong}" type="currency" currencySymbol="VND"/></td>
                            <td>${ct.soDem}</td>
                            <td class="text-end fw-bold"><fmt:formatNumber value="${ct.thanhTien}" type="currency" currencySymbol="VND"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty don.danhSachChiTiet}">
                        <tr><td colspan="5" class="text-center text-muted">Không có phòng trong đơn.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <!-- Service Details Table -->
        <h5 class="fw-bold text-success mb-3"><i class="bi bi-bag-plus"></i> Chi tiết dịch vụ phát sinh</h5>
        <div class="table-responsive mb-4">
            <table class="table table-bordered align-middle">
                <thead>
                    <tr>
                        <th>Tên dịch vụ</th>
                        <th>Số lượng</th>
                        <th>Đơn giá</th>
                        <th class="text-end">Thành tiền</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${don.danhSachDichVu}">
                        <tr>
                            <td><strong>${s.tenDv}</strong></td>
                            <td>${s.soLuong}</td>
                            <td><fmt:formatNumber value="${s.giaTaiThoiDiem}" type="currency" currencySymbol="VND"/></td>
                            <td class="text-end fw-bold"><fmt:formatNumber value="${s.thanhTien}" type="currency" currencySymbol="VND"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty don.danhSachDichVu}">
                        <tr><td colspan="4" class="text-center text-muted">Không có dịch vụ phát sinh.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <!-- Payment & Refund History -->
        <h5 class="fw-bold text-dark mb-3"><i class="bi bi-wallet2"></i> Lịch sử thanh toán & Hoàn tiền</h5>
        <div class="table-responsive mb-4">
            <table class="table table-bordered align-middle">
                <thead>
                    <tr>
                        <th>Thời gian</th>
                        <th>Loại giao dịch</th>
                        <th>Phương thức</th>
                        <th>Mã giao dịch</th>
                        <th>Ghi chú</th>
                        <th class="text-end">Số tiền</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="tt" items="${danhSachThanhToan}">
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
                    <c:if test="${empty danhSachThanhToan}">
                        <tr><td colspan="6" class="text-center text-muted">Chưa có giao dịch thanh toán nào.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <!-- Summary Totals -->
        <div class="total-section">
            <div class="row">
                <div class="col-md-6 offset-md-6">
                    <div class="d-flex justify-content-between mb-2">
                        <span>Tiền phòng:</span>
                        <strong><fmt:formatNumber value="${don.tienPhong}" type="currency" currencySymbol="VND"/></strong>
                    </div>
                    <div class="d-flex justify-content-between mb-2">
                        <span>Tiền dịch vụ:</span>
                        <strong><fmt:formatNumber value="${don.tienDichVu}" type="currency" currencySymbol="VND"/></strong>
                    </div>
                    <hr>
                    <div class="d-flex justify-content-between mb-2 fs-5 fw-bold text-primary">
                        <span>Tổng cộng:</span>
                        <span><fmt:formatNumber value="${don.tongTien}" type="currency" currencySymbol="VND"/></span>
                    </div>
                    <div class="d-flex justify-content-between mb-2 text-success">
                        <span>Các khoản đã thanh toán (Thực thu):</span>
                        <strong><fmt:formatNumber value="${thucThu}" type="currency" currencySymbol="VND"/></strong>
                    </div>
                    <div class="d-flex justify-content-between mb-0 fs-5 fw-bold text-danger">
                        <span>Số tiền còn lại:</span>
                        <span><fmt:formatNumber value="${soTienConLai}" type="currency" currencySymbol="VND"/></span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer Signatures -->
        <div class="row mt-5 text-center">
            <div class="col-6">
                <p class="fw-bold mb-5">Khách hàng ký tên</p>
                <p class="text-muted small">(Ký và ghi rõ họ tên)</p>
            </div>
            <div class="col-6">
                <p class="fw-bold mb-5">Nhân viên thu ngân</p>
                <p class="text-muted small">(Ký và ghi rõ họ tên)</p>
            </div>
        </div>
    </div>
</div>

</body>
</html>
