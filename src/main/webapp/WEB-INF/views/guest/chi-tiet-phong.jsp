<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Chi tiết phòng"/>
</jsp:include>

<div class="container mt-4 mb-5">
    <c:choose>
        <c:when test="${not empty phong}">
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h3 class="card-title text-primary mb-3">Phòng ${phong.soPhong} - ${phong.tenLoai}</h3>
                    <p><strong>Tầng:</strong> ${phong.tang}</p>
                    <p><strong>Sức chứa:</strong> ${phong.sucChua} người</p>
                    <p><strong>Diện tích:</strong> ${phong.dienTich} m²</p>
                    <p><strong>Mô tả:</strong> ${phong.moTa}</p>
                    <p class="text-danger fs-5 fw-bold">
                        <fmt:formatNumber value="${phong.giaPhong}" type="currency" currencySymbol="VND"/> / đêm
                    </p>

                    <div class="d-flex gap-2">
                        <a class="btn btn-outline-secondary"
                           href="${pageContext.request.contextPath}/tim-phong?ngayNhan=${ngayNhan}&ngayTra=${ngayTra}">
                            Quay lại tìm phòng
                        </a>

                        <c:if test="${not empty ngayNhan and not empty ngayTra}">
                            <form action="${pageContext.request.contextPath}/gio-hang" method="post" class="ms-auto">
                                <input type="hidden" name="maPhong" value="${phong.maPhong}">
                                <input type="hidden" name="soPhong" value="${phong.soPhong}">
                                <input type="hidden" name="tenLoai" value="${phong.tenLoai}">
                                <input type="hidden" name="giaPhong" value="${phong.giaPhong}">
                                <input type="hidden" name="ngayNhan" value="${ngayNhan}">
                                <input type="hidden" name="ngayTra" value="${ngayTra}">
                                <button type="submit" class="btn btn-primary">Thêm vào giỏ</button>
                            </form>
                        </c:if>
                    </div>
                </div>
            </div>

            <!-- Phần hiển thị Đánh giá / Review -->
            <div class="card shadow-sm border-0">
                <div class="card-header bg-dark text-white">
                    <h5 class="mb-0"><i class="bi bi-star-fill text-warning"></i> Đánh giá từ khách hàng (${not empty danhGiaList ? danhGiaList.size() : 0})</h5>
                </div>
                <div class="card-body">
                    <c:if test="{empty danhGiaList}">
                        <p class="text-muted mb-0">Chưa có đánh giá nào cho loại phòng này.</p>
                    </c:if>
                    <c:if test="${not empty danhGiaList}">
                        <div class="list-group list-group-flush">
                            <c:forEach var="dg" items="${danhGiaList}">
                                <div class="list-group-item px-0 py-3">
                                    <div class="d-flex justify-content-between align-items-center mb-1">
                                        <h6 class="mb-0 fw-bold">${dg.khachHang != null ? dg.khachHang.hoTen : 'Khách hàng'}</h6>
                                        <small class="text-muted">${dg.ngayDanhGia}</small>
                                    </div>
                                    <div class="text-warning mb-2">
                                        <c:forEach begin="1" end="${dg.soSao}">⭐</c:forEach> (${dg.soSao}/5 sao)
                                    </div>
                                    <p class="mb-0 text-secondary">${dg.noiDung}</p>
                                </div>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-warning">Không tìm thấy thông tin phòng.</div>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/tim-phong">Quay lại</a>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
