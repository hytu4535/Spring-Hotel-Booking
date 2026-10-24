package com.hotel.controller.admin;

import com.hotel.service.ThongKeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/dashboard")
public class DashboardServlet extends HttpServlet {

    private final ThongKeService thongKeService = new ThongKeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        BigDecimal doanhThuHomNay = thongKeService.getDoanhThuHomNay();
        BigDecimal doanhThuThangNay = thongKeService.getDoanhThuThangNay();
        int tongSoDon = thongKeService.getTongSoDon();
        Map<String, Object> thongKePhong = thongKeService.getThongKePhong();
        List<Map<String, Object>> hoatDongGganDay = thongKeService.getHoatDongGganDay(5);

        request.setAttribute("doanhThuHomNay", doanhThuHomNay);
        request.setAttribute("doanhThuThangNay", doanhThuThangNay);
        request.setAttribute("tongSoDon", tongSoDon);
        request.setAttribute("thongKePhong", thongKePhong);
        request.setAttribute("hoatDongGganDay", hoatDongGganDay);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
