package com.hotel.controller.guest;

import com.hotel.model.CartItem;
import com.hotel.model.GioHang;
import com.hotel.util.DateUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@WebServlet("/gio-hang")
public class GioHangServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        GioHang cart = (GioHang) session.getAttribute("cart");

        if (cart == null) {
            cart = new GioHang();
            session.setAttribute("cart", cart);
        }

        if ("remove".equals(action)) {
            int maPhong = Integer.parseInt(request.getParameter("maPhong"));
            cart.removePhong(maPhong);
        }

        request.getRequestDispatcher("/WEB-INF/views/guest/gio-hang.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Hành động thêm phòng vào giỏ (từ trang Tìm Phòng của Nguyễn Ý)
        HttpSession session = request.getSession();
        GioHang cart = (GioHang) session.getAttribute("cart");

        if (cart == null) {
            cart = new GioHang();
            session.setAttribute("cart", cart);
        }

        // Khởi tạo ngày nhận/trả cho Giỏ nếu chưa có
        String ngayNhan = request.getParameter("ngayNhan");
        String ngayTra = request.getParameter("ngayTra");
        if (cart.getNgayNhan() == null) {
            cart.setNgayNhan(ngayNhan);
            cart.setNgayTra(ngayTra);
            LocalDate d1 = DateUtil.parse(ngayNhan);
            LocalDate d2 = DateUtil.parse(ngayTra);
            cart.setSoDem(DateUtil.soDem(d1, d2));
        }

        // Lấy thông tin phòng vừa click thêm
        CartItem item = new CartItem();
        item.setMaPhong(Integer.parseInt(request.getParameter("maPhong")));
        item.setSoPhong(request.getParameter("soPhong"));
        item.setTenLoai(request.getParameter("tenLoai"));
        item.setGiaPhong(new BigDecimal(request.getParameter("giaPhong")));
        item.setSoDem(cart.getSoDem());
        item.setThanhTien(item.getGiaPhong().multiply(new BigDecimal(cart.getSoDem())));

        cart.addPhong(item);

        // Quay về trang giỏ hàng xem
        response.sendRedirect(request.getContextPath() + "/gio-hang");
    }
}