<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Giỏ Đặt Phòng & Dịch Vụ"/>
</jsp:include>

<div class="container mt-4 mb-5">
    <h2 class="fw-bold text-primary mb-4"><i class="bi bi-cart-check"></i> Giỏ Hàng & Dịch Vụ Đặt Phòng</h2>

    <c:if test="${empty sessionScope.cart || (empty sessionScope.cart.items && empty sessionScope.cart.services)}">
        <div class="alert alert-warning text-center p-5">
            <h4>Giỏ hàng của bạn đang trống!</h4>
            <a href="${pageContext.request.contextPath}/tim-phong" class="btn btn-primary mt-3">Tìm phòng ngay</a>
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.cart && (not empty sessionScope.cart.items || not empty sessionScope.cart.services)}">
        <div class="row">
            <div class="col-md-8">
                <!-- Danh sách Phòng -->
                <div class="card shadow-sm border-0 mb-4">
                    <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0"><i class="bi bi-house-door"></i> Phòng đã chọn 
                            <c:if test="${not empty sessionScope.cart.ngayNhan}">
                                <small class="text-light">(${sessionScope.cart.ngayNhan} đến ${sessionScope.cart.ngayTra} - ${sessionScope.cart.soDem} đêm)</small>
                            </c:if>
                        </h5>
                        <a href="${pageContext.request.contextPath}/tim-phong" class="btn btn-sm btn-outline-light"><i class="bi bi-plus-circle"></i> Chọn thêm phòng</a>
                    </div>
                    <div class="card-body p-0">
                        <table class="table mb-0 align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th>Phòng</th>
                                    <th>Loại phòng</th>
                                    <th>Giá / Đêm</th>
                                    <th>Thành tiền</th>
                                    <th class="text-center">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${sessionScope.cart.items}">
                                    <tr>
                                        <td><strong>P.${item.soPhong}</strong></td>
                                        <td>${item.tenLoai}</td>
                                        <td><fmt:formatNumber value="${item.giaPhong}" type="currency" currencySymbol="VND"/></td>
                                        <td class="text-primary fw-bold"><fmt:formatNumber value="${item.thanhTien}" type="currency" currencySymbol="VND"/></td>
                                        <td class="text-center">
                                            <a href="${pageContext.request.contextPath}/gio-hang?action=remove&maPhong=${item.maPhong}" class="btn btn-sm btn-outline-danger" title="Xóa phòng"><i class="bi bi-trash"></i></a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty sessionScope.cart.items}">
                                    <tr><td colspan="5" class="text-center text-muted p-3">Chưa có phòng nào trong giỏ.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Danh sách Dịch vụ phát sinh -->
                <div class="card shadow-sm border-0 mb-4">
                    <div class="card-header bg-secondary text-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0"><i class="bi bi-bag-plus"></i> Dịch vụ phát sinh kèm theo</h5>
                        <button type="button" class="btn btn-sm btn-light" data-bs-toggle="modal" data-bs-target="#addServiceModal">
                            <i class="bi bi-plus-circle"></i> Thêm dịch vụ
                        </button>
                    </div>
                    <div class="card-body p-0">
                        <table class="table mb-0 align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th>Tên dịch vụ</th>
                                    <th>Đơn giá</th>
                                    <th>Số lượng</th>
                                    <th>Thành tiền</th>
                                    <th class="text-center">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="s" items="${sessionScope.cart.services}">
                                    <tr>
                                        <td><strong>${s.tenDv}</strong></td>
                                        <td><fmt:formatNumber value="${s.gia}" type="currency" currencySymbol="VND"/></td>
                                        <td>${s.soLuong}</td>
                                        <td class="text-success fw-bold"><fmt:formatNumber value="${s.thanhTien}" type="currency" currencySymbol="VND"/></td>
                                        <td class="text-center">
                                            <a href="${pageContext.request.contextPath}/gio-hang?action=remove_service&maDv=${s.maDv}" class="btn btn-sm btn-outline-danger" title="Xóa dịch vụ"><i class="bi bi-trash"></i></a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty sessionScope.cart.services}">
                                    <tr><td colspan="5" class="text-center text-muted p-3">Chưa có dịch vụ phát sinh nào được chọn.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Tổng kết tiền & Nút Đặt phòng -->
            <div class="col-md-4">
                <div class="card shadow-sm border-primary">
                    <div class="card-body bg-light">
                        <h5 class="card-title fw-bold text-primary"><i class="bi bi-receipt"></i> Tổng Thanh Toán</h5>
                        <hr>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Tiền phòng:</span>
                            <span class="fw-bold"><fmt:formatNumber value="${sessionScope.cart.tienPhong}" type="currency" currencySymbol="VND"/></span>
                        </div>
                        <div class="d-flex justify-content-between mb-3">
                            <span>Tiền dịch vụ:</span>
                            <span class="fw-bold text-success"><fmt:formatNumber value="${sessionScope.cart.tienDichVu}" type="currency" currencySymbol="VND"/></span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between mb-4">
                            <span class="fs-6 fw-bold">TỔNG CỘNG:</span>
                            <span class="fs-5 fw-bold text-danger"><fmt:formatNumber value="${sessionScope.cart.tongTien}" type="currency" currencySymbol="VND"/></span>
                        </div>
                        <c:choose>
                            <c:when test="${not empty sessionScope.cart.items}">
                                <a href="${pageContext.request.contextPath}/dat-phong" class="btn btn-primary w-100 btn-lg fw-bold">
                                    <i class="bi bi-check2-circle"></i> TIẾN HÀNH ĐẶT PHÒNG
                                </a>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-secondary w-100 btn-lg fw-bold" disabled>Vui lòng chọn ít nhất 1 phòng</button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>

<!-- Modal Thêm Dịch Vụ Phát Sinh -->
<div class="modal fade" id="addServiceModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/gio-hang" method="post">
                <input type="hidden" name="action" value="add_service">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title"><i class="bi bi-bag-plus"></i> Thêm dịch vụ phát sinh</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Chọn dịch vụ</label>
                        <select name="maDv" class="form-select" required>
                            <option value="">-- Chọn dịch vụ --</option>
                            <c:forEach var="dv" items="${danhSachDichVu}">
                                <option value="${dv.maDv}">${dv.tenDv} - <fmt:formatNumber value="${dv.gia}" type="currency" currencySymbol="VND"/>/${dv.donViTinh}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Số lượng</label>
                        <input type="number" name="soLuong" class="form-control" value="1" min="1" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Thêm vào đơn</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
