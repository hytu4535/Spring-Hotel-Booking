<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Đặt phòng thành công"/>
</jsp:include>

<div class="container mt-5 mb-5 text-center">
    <div class="card shadow-sm border-0 mx-auto" style="max-width: 600px;">
        <div class="card-body p-5">
            <i class="bi bi-check-circle-fill text-success" style="font-size: 5rem;"></i>
            <h2 class="mt-3 fw-bold text-success">Đặt phòng thành công!</h2>
            <p class="text-muted">Cảm ơn bạn đã tin tưởng dịch vụ của chúng tôi.</p>
            
            <div class="alert alert-warning mt-4 fs-5">
                Mã tra cứu đơn của bạn là: <br>
                <strong class="fs-2 text-danger">${sessionScope.maTraCuu}</strong>
            </div>
            
            <p class="text-danger fw-bold"><i class="bi bi-exclamation-triangle"></i> Vui lòng lưu lại mã này cùng email (${sessionScope.emailDatPhong}) để tra cứu đơn hàng.</p>
            
            <div class="mt-4">
                <a href="${pageContext.request.contextPath}/tra-cuu-don" class="btn btn-primary me-2">Đến trang tra cứu</a>
                <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">Về trang chủ</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>