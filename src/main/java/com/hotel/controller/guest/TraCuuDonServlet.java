package com.hotel.controller.guest;

import com.hotel.dao.DatPhongDAO;
import com.hotel.dao.KhachHangDAO;
import com.hotel.model.DatPhong;
import com.hotel.model.KhachHang;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/tra-cuu-don")
public class TraCuuDonServlet extends HttpServlet {

    private final DatPhongDAO datPhongDAO = new DatPhongDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/guest/tra-cuu-don.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // 1. CHỨC NĂNG TÌM KIẾM ĐƠN
        if ("search".equals(action)) {
            String maTraCuu = request.getParameter("maTraCuu");
            String email = request.getParameter("email");

            DatPhong dp = datPhongDAO.findByMaTraCuu(maTraCuu);
            if (dp != null) {
                KhachHang kh = khachHangDAO.findById(dp.getMaKh()); // Nhớ tạo hàm này trong KhachHangDAO
                if (kh != null && kh.getEmail().equalsIgnoreCase(email)) {
                    request.setAttribute("donDatPhong", dp);
                    request.setAttribute("khachHang", kh);
                } else {
                    request.setAttribute("error", "Email không khớp với mã tra cứu!");
                }
            } else {
                request.setAttribute("error", "Không tìm thấy đơn hàng với mã này!");
            }
            
            // Giữ lại input để khách khỏi nhập lại
            request.setAttribute("maTraCuuInput", maTraCuu);
            request.setAttribute("emailInput", email);
            request.getRequestDispatcher("/WEB-INF/views/guest/tra-cuu-don.jsp").forward(request, response);
        }
        
        // 2. CHỨC NĂNG HỦY ĐƠN (Khách tự hủy)
        else if ("cancel".equals(action)) {
            int maDp = Integer.parseInt(request.getParameter("maDp"));
            // Khách chỉ được hủy khi đơn đang ở trạng thái CHỜ XÁC NHẬN
            datPhongDAO.updateTrangThai(maDp, "DA_HUY");
            
            request.setAttribute("success", "Đã hủy đơn đặt phòng thành công!");
            request.getRequestDispatcher("/WEB-INF/views/guest/tra-cuu-don.jsp").forward(request, response);
        }
    }
}