package com.hotel.controller.guest;

import com.hotel.dao.DichVuDAO;
import com.hotel.model.CartItem;
import com.hotel.model.CartServiceItem;
import com.hotel.model.DichVu;
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
import java.util.List;

@WebServlet("/gio-hang")
public class GioHangServlet extends HttpServlet {

    private final DichVuDAO dichVuDAO = new DichVuDAO();

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
        } else if ("remove_service".equals(action)) {
            int maDv = Integer.parseInt(request.getParameter("maDv"));
            cart.removeService(maDv);
        }

        // Lấy danh sách dịch vụ phát sinh có sẵn để khách chọn thêm
        List<DichVu> danhSachDichVu = dichVuDAO.findAll();
        request.setAttribute("danhSachDichVu", danhSachDichVu);

        request.getRequestDispatcher("/WEB-INF/views/guest/gio-hang.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        GioHang cart = (GioHang) session.getAttribute("cart");

        if (cart == null) {
            cart = new GioHang();
            session.setAttribute("cart", cart);
        }

        String action = request.getParameter("action");

        if ("add_service".equals(action)) {
            try {
                int maDv = Integer.parseInt(request.getParameter("maDv"));
                int soLuong = Integer.parseInt(request.getParameter("soLuong"));
                if (soLuong < 1) soLuong = 1;

                // Tìm thông tin dịch vụ từ DB
                List<DichVu> listDv = dichVuDAO.findAll();
                DichVu selectedDv = null;
                for (DichVu dv : listDv) {
                    if (dv.getMaDv() == maDv) {
                        selectedDv = dv;
                        break;
                    }
                }

                if (selectedDv != null) {
                    CartServiceItem serviceItem = new CartServiceItem(
                            selectedDv.getMaDv(),
                            selectedDv.getTenDv(),
                            selectedDv.getGia(),
                            soLuong
                    );
                    cart.addService(serviceItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/gio-hang");
            return;
        }

        // Khởi tạo ngày nhận/trả cho Giỏ nếu chưa có
        String ngayNhan = request.getParameter("ngayNhan");
        String ngayTra = request.getParameter("ngayTra");
        if (cart.getNgayNhan() == null && ngayNhan != null && ngayTra != null) {
            cart.setNgayNhan(ngayNhan);
            cart.setNgayTra(ngayTra);
            LocalDate d1 = DateUtil.parse(ngayNhan);
            LocalDate d2 = DateUtil.parse(ngayTra);
            cart.setSoDem(DateUtil.soDem(d1, d2));
        }

        // Lấy thông tin phòng vừa click thêm
        if (request.getParameter("maPhong") != null) {
            CartItem item = new CartItem();
            item.setMaPhong(Integer.parseInt(request.getParameter("maPhong")));
            item.setSoPhong(request.getParameter("soPhong"));
            item.setTenLoai(request.getParameter("tenLoai"));
            item.setGiaPhong(new BigDecimal(request.getParameter("giaPhong")));
            item.setSoDem(cart.getSoDem() > 0 ? cart.getSoDem() : 1);
            item.setThanhTien(item.getGiaPhong().multiply(new BigDecimal(item.getSoDem())));

            cart.addPhong(item);
        }

        // Quay về trang giỏ hàng xem
        response.sendRedirect(request.getContextPath() + "/gio-hang");
    }
}
