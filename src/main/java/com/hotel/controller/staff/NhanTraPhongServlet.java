package com.hotel.controller.staff;

import com.hotel.dao.DatPhongDAO;
import com.hotel.dao.PhongDAO;
import com.hotel.model.PhongSoDoDTO;
import com.hotel.model.TaiKhoan;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet({"/staff/nhan-tra-phong", "/admin/nhan-tra-phong"})
public class NhanTraPhongServlet extends HttpServlet {

    private final PhongDAO phongDAO = new PhongDAO();
    private final DatPhongDAO datPhongDAO = new DatPhongDAO(); // Dùng lại Transaction đã làm đợt trước

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy dữ liệu hiển thị Sơ đồ
        List<PhongSoDoDTO> danhSachPhong = phongDAO.getSoDoPhong();
        request.setAttribute("danhSachPhong", danhSachPhong);
        request.setAttribute("roomBasePath", isAdminRequest(request) ? "/admin/nhan-tra-phong" : "/staff/nhan-tra-phong");
        request.setAttribute("isAdminView", isAdminRequest(request));
        request.getRequestDispatcher("/WEB-INF/views/staff/so-do-phong.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int maDp = Integer.parseInt(request.getParameter("maDp"));
        
        HttpSession session = request.getSession();
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");

        boolean success = false;
        
        // Gọi lại Transaction ông đã code hôm trước
        if ("check_in".equals(action)) {
            success = datPhongDAO.updateTrangThaiTransaction(maDp, "DA_NHAN_PHONG", currentUser.getMaTk());
            session.setAttribute(success ? "successMsg" : "errorMsg", 
                                 success ? "Đã Check-in nhận phòng thành công!" : "Lỗi Check-in!");
        } else if ("check_out".equals(action)) {
            success = datPhongDAO.updateTrangThaiTransaction(maDp, "DA_TRA_PHONG", currentUser.getMaTk());
            session.setAttribute(success ? "successMsg" : "errorMsg", 
                                 success ? "Đã Check-out trả phòng thành công!" : "Lỗi Check-out!");
        }

        String basePath = isAdminRequest(request) ? "/admin/nhan-tra-phong" : "/staff/nhan-tra-phong";
        response.sendRedirect(request.getContextPath() + basePath);
    }

    private boolean isAdminRequest(HttpServletRequest request) {
        return request.getServletPath().startsWith("/admin/");
    }
}