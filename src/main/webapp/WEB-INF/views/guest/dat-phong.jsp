<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Xác nhận đặt phòng"/>
</jsp:include>

<div class="row mt-4 mb-5">
    <div class="col-md-8 mx-auto">
        
        <!-- Tóm tắt Đơn Đặt Phòng -->
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                <h5 class="mb-0"><i class="bi bi-cart-check"></i> Tóm tắt thông tin đặt phòng & dịch vụ</h5>
                <a href="${pageContext.request.contextPath}/gio-hang" class="btn btn-sm btn-outline-light">Sửa giỏ hàng</a>
            </div>
            <div class="card-body bg-light">
                <p><strong>Ngày nhận:</strong> ${sessionScope.cart.ngayNhan} &nbsp;&nbsp;|&nbsp;&nbsp; 
                   <strong>Ngày trả:</strong> ${sessionScope.cart.ngayTra} &nbsp;&nbsp;
                   <span class="badge bg-info text-dark">(${sessionScope.cart.soDem} đêm)</span>
                </p>
                <hr>
                <!-- Phòng -->
                <h6 class="text-primary fw-bold">Danh sách phòng:</h6>
                <ul class="list-group mb-3">
                    <c:forEach var="item" items="${sessionScope.cart.items}">
                        <li class="list-group-item d-flex justify-content-between lh-sm">
                            <div>
                                <h6 class="my-0">Phòng ${item.soPhong} (${item.tenLoai})</h6>
                                <small class="text-muted"><fmt:formatNumber value="${item.giaPhong}" type="currency" currencySymbol="VND"/> x ${item.soDem} đêm</small>
                            </div>
                            <span class="text-muted fw-bold"><fmt:formatNumber value="${item.thanhTien}" type="currency" currencySymbol="VND"/></span>
                        </li>
                    </c:forEach>
                    <li class="list-group-item d-flex justify-content-between bg-light">
                        <span>Tiền phòng</span>
                        <strong><fmt:formatNumber value="${sessionScope.cart.tienPhong}" type="currency" currencySymbol="VND"/></strong>
                    </li>
                </ul>

                <!-- Dịch vụ phát sinh -->
                <c:if test="${not empty sessionScope.cart.services}">
                    <h6 class="text-success fw-bold">Dịch vụ phát sinh:</h6>
                    <ul class="list-group mb-3">
                        <c:forEach var="s" items="${sessionScope.cart.services}">
                            <li class="list-group-item d-flex justify-content-between lh-sm">
                                <div>
                                    <h6 class="my-0">${s.tenDv}</h6>
                                    <small class="text-muted"><fmt:formatNumber value="${s.gia}" type="currency" currencySymbol="VND"/> x ${s.soLuong}</small>
                                </div>
                                <span class="text-success fw-bold"><fmt:formatNumber value="${s.thanhTien}" type="currency" currencySymbol="VND"/></span>
                            </li>
                        </c:forEach>
                        <li class="list-group-item d-flex justify-content-between bg-light">
                            <span>Tiền dịch vụ</span>
                            <strong><fmt:formatNumber value="${sessionScope.cart.tienDichVu}" type="currency" currencySymbol="VND"/></strong>
                        </li>
                    </ul>
                </c:if>

                <ul class="list-group mb-3">
                    <li class="list-group-item d-flex justify-content-between text-danger fs-5">
                        <span><strong>TỔNG CỘNG (VND)</strong></span>
                        <strong><fmt:formatNumber value="${sessionScope.cart.tongTien}" type="currency" currencySymbol="VND"/></strong>
                    </li>
                </ul>
            </div>
        </div>

        <!-- Form nhập thông tin khách hàng -->
        <div class="card shadow-sm border-0 border-top border-primary border-3">
            <div class="card-body p-4">
                <h4 class="mb-4 text-primary"><i class="bi bi-person-lines-fill"></i> Thông tin người đặt phòng</h4>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger fw-bold"><i class="bi bi-exclamation-triangle"></i> ${error}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/dat-phong" method="post">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Họ và Tên <span class="text-danger">*</span></label>
                            <input type="text" name="hoTen" class="form-control" required value="${not empty currentUser ? currentUser.hoTen : ''}" placeholder="Nhập họ tên...">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Email <span class="text-danger">*</span></label>
                            <input type="email" name="email" class="form-control" required value="${not empty currentUser ? currentUser.email : ''}" placeholder="Gửi mã tra cứu về mail này">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Số điện thoại <span class="text-danger">*</span></label>
                            <input type="text" name="soDienThoai" class="form-control" required value="${not empty currentUser ? currentUser.soDienThoai : ''}" placeholder="09xxxxxxxxx">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">CCCD/CMND <span class="text-muted fw-normal">(Tùy chọn)</span></label>
                            <input type="text" name="cccd" class="form-control" placeholder="Số CCCD của bạn">
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-bold">Địa chỉ <span class="text-muted fw-normal">(Tùy chọn)</span></label>
                            <input type="text" name="diaChi" class="form-control" placeholder="Tỉnh/Thành phố...">
                        </div>
                    </div>

                    <div class="mt-4 text-end">
                        <button type="submit" class="btn btn-warning btn-lg fw-bold px-5 text-dark">
                            <i class="bi bi-check2-circle"></i> XÁC NHẬN ĐẶT PHÒNG
                        </button>
                    </div>
                </form>
            </div>
        </div>
        
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
