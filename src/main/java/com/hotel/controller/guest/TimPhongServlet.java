package com.hotel.controller.guest;

import com.hotel.dao.PhongDAO;
import com.hotel.model.PhongTrongDTO;
import com.hotel.util.DateUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class TimPhongServlet extends HttpServlet {

    private final PhongDAO phongDAO = new PhongDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ngayNhan = request.getParameter("ngayNhan");
        String ngayTra = request.getParameter("ngayTra");

        request.setAttribute("ngayNhan", ngayNhan);
        request.setAttribute("ngayTra", ngayTra);

        if (ngayNhan == null || ngayTra == null) {
            request.setAttribute("danhSachPhong", Collections.emptyList());
            request.getRequestDispatcher("/WEB-INF/views/guest/tim-phong.jsp").forward(request, response);
            return;
        }

        LocalDate dNhan = DateUtil.parse(ngayNhan);
        LocalDate dTra = DateUtil.parse(ngayTra);

        if (!DateUtil.isValidRange(dNhan, dTra)) {
            request.setAttribute("error", "Ngày nhận/trả phòng không hợp lệ.");
            request.setAttribute("danhSachPhong", Collections.emptyList());
            request.getRequestDispatcher("/WEB-INF/views/guest/tim-phong.jsp").forward(request, response);
            return;
        }

        int soDem = DateUtil.soDem(dNhan, dTra);
        request.setAttribute("soDem", soDem);

        List<PhongTrongDTO> danhSachPhong = phongDAO.timPhongTrong(Date.valueOf(dNhan), Date.valueOf(dTra));
        request.setAttribute("danhSachPhong", danhSachPhong);
        request.getRequestDispatcher("/WEB-INF/views/guest/tim-phong.jsp").forward(request, response);
    }
}