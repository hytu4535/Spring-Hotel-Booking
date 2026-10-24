package com.hotel.controller.staff;

import com.hotel.dao.DatPhongDAO;
import com.hotel.model.DatPhongDTO;
import com.hotel.model.TaiKhoan;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class DonDatPhongServlet extends HttpServlet {

    private final DatPhongDAO datPhongDAO = new DatPhongDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy danh sách toàn bộ đơn đặt phòng
        List<DatPhongDTO> danhSachDon = datPhongDAO.findAllWithKhachHang();
        request.setAttribute("danhSachDon", danhSachDon);
        
        request.setAttribute("bookingBasePath", isAdminRequest(request) ? "/admin/dat-phong" : "/staff/don");
        request.setAttribute("isAdminView", isAdminRequest(request));
        request.getRequestDispatcher("/WEB-INF/views/staff/quan-ly-don.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("update_status".equals(action)) {
            int maDp = Integer.parseInt(request.getParameter("maDp"));
            String trangThaiMoi = request.getParameter("trangThaiMoi");
            
            // Lấy mã NV đang thao tác từ Session
            HttpSession session = request.getSession();
            TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
            
            // Đổi trạng thái Đơn + Phòng bằng Transaction
            boolean success = datPhongDAO.updateTrangThaiTransaction(maDp, trangThaiMoi, currentUser.getMaTk());
            
            if (success) {
                request.getSession().setAttribute("successMsg", "Cập nhật trạng thái đơn thành công!");
            } else {
                request.getSession().setAttribute("errorMsg", "Lỗi khi cập nhật trạng thái!");
            }
        }
        
        // Cập nhật xong thì reload lại trang danh sách
        String basePath = isAdminRequest(request) ? "/admin/dat-phong" : "/staff/don";
        response.sendRedirect(request.getContextPath() + basePath);
    }

    private boolean isAdminRequest(HttpServletRequest request) {
        return request.getServletPath().startsWith("/admin/");
    }
}