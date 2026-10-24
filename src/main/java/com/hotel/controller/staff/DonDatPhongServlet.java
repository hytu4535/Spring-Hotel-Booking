package com.hotel.controller.staff;

import com.hotel.dao.DatPhongDAO;
import com.hotel.dao.DichVuDAO;
import com.hotel.dao.PhongDAO;
import com.hotel.dao.TaiKhoanDAO;
import com.hotel.model.DatPhongDTO;
import com.hotel.model.DichVu;
import com.hotel.model.PhongTrongDTO;
import com.hotel.model.TaiKhoan;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet({"/staff/don", "/admin/dat-phong"})
public class DonDatPhongServlet extends HttpServlet {

    private final DatPhongDAO datPhongDAO = new DatPhongDAO();
    private final DichVuDAO dichVuDAO = new DichVuDAO();
    private final PhongDAO phongDAO = new PhongDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<DatPhongDTO> danhSachDon = datPhongDAO.findAllWithKhachHang();
        request.setAttribute("danhSachDon", danhSachDon);

        List<DichVu> danhSachDichVu = dichVuDAO.findAll();
        request.setAttribute("danhSachDichVu", danhSachDichVu);

        List<PhongTrongDTO> danhSachPhong = phongDAO.findAllPhong();
        request.setAttribute("danhSachPhong", danhSachPhong);

        List<TaiKhoan> danhSachNhanVien = taiKhoanDAO.findAll();
        request.setAttribute("danhSachNhanVien", danhSachNhanVien);
        
        request.setAttribute("bookingBasePath", isAdminRequest(request) ? "/admin/dat-phong" : "/staff/don");
        request.setAttribute("isAdminView", isAdminRequest(request));
        request.getRequestDispatcher("/WEB-INF/views/staff/quan-ly-don.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        int maNv = (currentUser != null) ? currentUser.getMaTk() : 1;
        
        if ("update_status".equals(action)) {
            int maDp = Integer.parseInt(request.getParameter("maDp"));
            String trangThaiMoi = request.getParameter("trangThaiMoi");
            
            boolean success = datPhongDAO.updateTrangThaiTransaction(maDp, trangThaiMoi, maNv);
            if (success) {
                session.setAttribute("successMsg", "Cập nhật trạng thái đơn thành công!");
            } else {
                session.setAttribute("errorMsg", "Lỗi khi cập nhật trạng thái!");
            }
        } else if ("add_dich_vu".equals(action)) {
            try {
                int maDp = Integer.parseInt(request.getParameter("maDp"));
                int maDv = Integer.parseInt(request.getParameter("maDv"));
                int soLuong = Integer.parseInt(request.getParameter("soLuong"));
                String ngayStr = request.getParameter("ngaySuDung");
                Date ngaySuDung = (ngayStr != null && !ngayStr.isEmpty()) ? Date.valueOf(ngayStr) : new Date(System.currentTimeMillis());

                String nvStr = request.getParameter("maNvGhiNhan");
                int maNvGhiNhan = (nvStr != null && !nvStr.isEmpty()) ? Integer.parseInt(nvStr) : maNv;

                boolean success = datPhongDAO.addDichVuTransaction(maDp, maDv, soLuong, ngaySuDung, maNvGhiNhan);
                if (success) {
                    session.setAttribute("successMsg", "Thêm dịch vụ phát sinh thành công!");
                } else {
                    session.setAttribute("errorMsg", "Lỗi khi thêm dịch vụ phát sinh (đơn có thể đã hủy/trả phòng hoặc lỗi dữ liệu)!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorMsg", "Dữ liệu dịch vụ không hợp lệ: " + e.getMessage());
            }
        } else if ("delete_dich_vu".equals(action)) {
            try {
                int maDatDv = Integer.parseInt(request.getParameter("maDatDv"));
                int maDp = Integer.parseInt(request.getParameter("maDp"));

                boolean success = datPhongDAO.deleteDichVuTransaction(maDatDv, maDp);
                if (success) {
                    session.setAttribute("successMsg", "Đã xóa dịch vụ phát sinh!");
                } else {
                    session.setAttribute("errorMsg", "Lỗi khi xóa dịch vụ phát sinh!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorMsg", "Lỗi khi xóa dịch vụ: " + e.getMessage());
            }
        } else if ("add_phong".equals(action)) {
            try {
                int maDp = Integer.parseInt(request.getParameter("maDp"));
                int maPhong = Integer.parseInt(request.getParameter("maPhong"));

                boolean success = datPhongDAO.addPhongTransaction(maDp, maPhong);
                if (success) {
                    session.setAttribute("successMsg", "Thêm phòng vào đơn thành công!");
                } else {
                    session.setAttribute("errorMsg", "Lỗi khi thêm phòng (phòng có thể đã được đặt hoặc không khả dụng)!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorMsg", "Lỗi khi thêm phòng: " + e.getMessage());
            }
        } else if ("delete_phong".equals(action)) {
            try {
                int maCtdp = Integer.parseInt(request.getParameter("maCtdp"));
                int maDp = Integer.parseInt(request.getParameter("maDp"));

                boolean success = datPhongDAO.deletePhongTransaction(maCtdp, maDp);
                if (success) {
                    session.setAttribute("successMsg", "Đã xóa phòng khỏi đơn!");
                } else {
                    session.setAttribute("errorMsg", "Lỗi khi xóa phòng!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorMsg", "Lỗi khi xóa phòng: " + e.getMessage());
            }
        }
        
        String basePath = isAdminRequest(request) ? "/admin/dat-phong" : "/staff/don";
        response.sendRedirect(request.getContextPath() + basePath);
    }

    private boolean isAdminRequest(HttpServletRequest request) {
        return request.getServletPath().startsWith("/admin/");
    }
}
