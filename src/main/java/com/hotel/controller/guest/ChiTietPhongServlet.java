package com.hotel.controller.guest;

import com.hotel.dao.PhongDAO;
import com.hotel.model.DanhGia;
import com.hotel.model.PhongTrongDTO;
import com.hotel.service.DanhGiaService;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/chi-tiet-phong")
public class ChiTietPhongServlet extends HttpServlet {

    private final PhongDAO phongDAO = new PhongDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String maPhongRaw = request.getParameter("maPhong");
        if (maPhongRaw == null) {
            response.sendRedirect(request.getContextPath() + "/tim-phong");
            return;
        }

        try {
            int maPhong = Integer.parseInt(maPhongRaw);
            PhongTrongDTO phong = phongDAO.findPhongChiTiet(maPhong);
            if (phong == null) {
                response.sendRedirect(request.getContextPath() + "/tim-phong");
                return;
            }

            DanhGiaService danhGiaService = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(getServletContext())
                    .getBean(DanhGiaService.class);
            List<DanhGia> danhGiaList = danhGiaService.getDanhGiaByLoaiPhong(phong.getMaLoai());

            request.setAttribute("phong", phong);
            request.setAttribute("danhGiaList", danhGiaList);
            request.setAttribute("ngayNhan", request.getParameter("ngayNhan"));
            request.setAttribute("ngayTra", request.getParameter("ngayTra"));
            request.getRequestDispatcher("/WEB-INF/views/guest/chi-tiet-phong.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/tim-phong");
        }
    }
}
