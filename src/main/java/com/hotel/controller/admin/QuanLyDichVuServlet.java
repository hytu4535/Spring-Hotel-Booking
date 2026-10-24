package com.hotel.controller.admin;

import com.hotel.model.DichVu;
import com.hotel.service.DichVuService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/admin/dich-vu/*")
public class QuanLyDichVuServlet extends HttpServlet {

    private final DichVuService dichVuService = new DichVuService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<DichVu> list = dichVuService.findAll();
            request.setAttribute("dichVuList", list);
            request.getRequestDispatcher("/WEB-INF/views/admin/quan-ly-dich-vu.jsp").forward(request, response);
        } else if (pathInfo.equals("/sua")) {
            int maDv = Integer.parseInt(request.getParameter("id"));
            DichVu dv = dichVuService.findById(maDv);
            request.setAttribute("dichVu", dv);
            request.getRequestDispatcher("/WEB-INF/views/admin/quan-ly-dich-vu.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/dich-vu");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        if ("/them".equals(pathInfo)) {
            String tenDv = request.getParameter("tenDv");
            BigDecimal gia = new BigDecimal(request.getParameter("gia"));
            String donViTinh = request.getParameter("donViTinh");
            String moTa = request.getParameter("moTa");
            int trangThai = request.getParameter("trangThai") != null ? Integer.parseInt(request.getParameter("trangThai")) : 1;

            DichVu dv = new DichVu();
            dv.setTenDv(tenDv);
            dv.setGia(gia);
            dv.setDonViTinh(donViTinh);
            dv.setMoTa(moTa);
            dv.setTrangThai(trangThai);

            dichVuService.create(dv);
            response.sendRedirect(request.getContextPath() + "/admin/dich-vu?success=them");
        } else if ("/sua".equals(pathInfo)) {
            int maDv = Integer.parseInt(request.getParameter("maDv"));
            String tenDv = request.getParameter("tenDv");
            BigDecimal gia = new BigDecimal(request.getParameter("gia"));
            String donViTinh = request.getParameter("donViTinh");
            String moTa = request.getParameter("moTa");
            int trangThai = request.getParameter("trangThai") != null ? Integer.parseInt(request.getParameter("trangThai")) : 1;

            DichVu dv = new DichVu();
            dv.setMaDv(maDv);
            dv.setTenDv(tenDv);
            dv.setGia(gia);
            dv.setDonViTinh(donViTinh);
            dv.setMoTa(moTa);
            dv.setTrangThai(trangThai);

            dichVuService.update(dv);
            response.sendRedirect(request.getContextPath() + "/admin/dich-vu?success=sua");
        } else if ("/xoa".equals(pathInfo)) {
            int maDv = Integer.parseInt(request.getParameter("maDv"));
            dichVuService.delete(maDv);
            response.sendRedirect(request.getContextPath() + "/admin/dich-vu?success=xoa");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/dich-vu");
        }
    }
}
