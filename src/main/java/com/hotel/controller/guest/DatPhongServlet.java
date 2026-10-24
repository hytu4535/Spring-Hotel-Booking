package com.hotel.controller.guest;

import com.hotel.model.CartItem;
import com.hotel.model.ChiTietDatPhong;
import com.hotel.model.DatPhong;
import com.hotel.model.GioHang;
import com.hotel.model.KhachHang;
import com.hotel.service.DatPhongService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatPhongServlet extends HttpServlet {

    @Autowired
    private DatPhongService datPhongService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        GioHang cart = (GioHang) session.getAttribute("cart");

        // 1. Nếu giỏ hàng rỗng -> Đá về trang Tìm phòng
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/tim-phong");
            return;
        }

        // 2. Nếu có đồ trong giỏ -> Mở trang điền thông tin khách
        request.getRequestDispatcher("/WEB-INF/views/guest/dat-phong.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        GioHang cart = (GioHang) session.getAttribute("cart");

        // Kiểm tra lại lần nữa lúc Submit form lỡ giỏ hàng bị timeout
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/tim-phong");
            return;
        }

        try {
            // 1. Lấy thông tin khách hàng từ Form
            KhachHang kh = new KhachHang();
            kh.setHoTen(request.getParameter("hoTen"));
            kh.setEmail(request.getParameter("email"));
            kh.setSoDienThoai(request.getParameter("soDienThoai"));
            kh.setCccd(request.getParameter("cccd"));
            kh.setDiaChi(request.getParameter("diaChi"));

            // 2. Map dữ liệu Đặt Phòng từ Giỏ hàng
            DatPhong dp = new DatPhong();
            dp.setNgayNhanPhong(java.sql.Date.valueOf(cart.getNgayNhan()));
            dp.setNgayTraPhong(java.sql.Date.valueOf(cart.getNgayTra()));
            dp.setTienPhong(cart.getTongTien());

            // 3. Map chi tiết các phòng đặt từ Giỏ hàng
            List<ChiTietDatPhong> ctList = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                ChiTietDatPhong ct = new ChiTietDatPhong();
                ct.setMaPhong(item.getMaPhong());
                ct.setGiaPhong(item.getGiaPhong());
                ct.setSoDem(item.getSoDem());
                ct.setThanhTien(item.getThanhTien());
                ctList.add(ct);
            }

            // 4. Gọi Service thực thi Transaction lưu vào DB
            String maTraCuu = datPhongService.taoDonDatPhong(kh, dp, ctList);

            if (maTraCuu != null) {
                // Đặt thành công: Xóa giỏ hàng và chuyển hướng
                session.removeAttribute("cart"); 
                session.setAttribute("maTraCuu", maTraCuu);
                session.setAttribute("emailDatPhong", kh.getEmail());
                
                // (Chỗ này sau này Trung Kiên sẽ đổi sang redirect VNPay)
                response.sendRedirect(request.getContextPath() + "/dat-phong-thanh-cong");
            } else {
                // Thất bại do ai đó nhanh tay đặt mất phòng
                request.setAttribute("error", "Một số phòng bạn chọn vừa bị người khác đặt mất. Vui lòng kiểm tra lại sơ đồ phòng!");
                request.getRequestDispatcher("/WEB-INF/views/guest/dat-phong.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Dữ liệu nhập vào chưa hợp lệ, vui lòng kiểm tra lại.");
            request.getRequestDispatcher("/WEB-INF/views/guest/dat-phong.jsp").forward(request, response);
        }
    }
}