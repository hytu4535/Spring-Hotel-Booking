package com.hotel.controller.admin;

import com.hotel.dao.TaiKhoanDAO;
import com.hotel.model.TaiKhoan;
import com.hotel.util.PasswordUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Servlet để quản lý tài khoản (chỉ ADMIN)
 * Cho phép xem danh sách tài khoản, khóa/mở khóa, đổi vai trò, tạo tài khoản mới
 */
@WebServlet("/admin/tai-khoan")
public class AccountManagementServlet extends HttpServlet {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra quyền_ADMIN
        if (user == null || !"ADMIN".equals(user.getVaiTro())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy danh sách tất cả tài khoản
        List<TaiKhoan> taiKhoanList = loadFilteredAccounts(request);
        request.setAttribute("taiKhoanList", taiKhoanList);
        request.setAttribute("currentUser", user);
        setFilterAttributes(request);

        request.getRequestDispatcher("/WEB-INF/views/admin/taiKhoanManage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute("currentUser") : null;

        // Kiểm tra quyền_ADMIN
        if (user == null || !"ADMIN".equals(user.getVaiTro())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        // Xử lý tạo tài khoản mới
        if ("create".equals(action)) {
            String tenDangNhap = request.getParameter("tenDangNhap");
            String matKhau = request.getParameter("matKhau");
            String hoTen = request.getParameter("hoTen");
            String email = request.getParameter("email");
            String soDienThoai = request.getParameter("soDienThoai");
            String vaiTro = request.getParameter("vaiTro"); // NHANVIEN or ADMIN (but admin should only create NHANVIEN per requirements)

            boolean hasError = false;
            if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
                request.setAttribute("errorTenDangNhap", "Tên đăng nhập không được để trống");
                hasError = true;
            } else if (taiKhoanDAO.existsTenDangNhap(tenDangNhap.trim(), null)) {
                request.setAttribute("errorTenDangNhap", "Tên đăng nhập đã tồn tại");
                hasError = true;
            }
            if (matKhau == null || matKhau.isBlank()) {
                request.setAttribute("errorMatKhau", "Mật khẩu không được để trống");
                hasError = true;
            } else if (matKhau.length() < 6) {
                request.setAttribute("errorMatKhau", "Mật khẩu phải có ít nhất 6 ký tự");
                hasError = true;
            }
            if (hoTen == null || hoTen.trim().isEmpty()) {
                request.setAttribute("errorHoTen", "Họ tên không được để trống");
                hasError = true;
            }
            if (email == null || email.trim().isEmpty()) {
                request.setAttribute("errorEmail", "Email không được để trống");
                hasError = true;
            } else if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
                request.setAttribute("errorEmail", "Email không đúng định dạng");
                hasError = true;
            } else if (taiKhoanDAO.existsEmail(email.trim(), null)) {
                request.setAttribute("errorEmail", "Email đã tồn tại");
                hasError = true;
            }
            // Per requirements: Admin chỉ được tạo tài khoản nhân viên
            if (vaiTro == null || (!"NHANVIEN".equals(vaiTro) && !"ADMIN".equals(vaiTro))) {
                request.setAttribute("errorVaiTro", "Vai trò không hợp lệ");
                hasError = true;
            } else if ("ADMIN".equals(vaiTro)) {
                // Admin không được tạo tài khoản admin mới (per requirements: chỉ được tạo nhân viên)
                request.setAttribute("errorVaiTro", "Chỉ được tạo tài khoản nhân viên");
                hasError = true;
            }

            if (hasError) {
                // Giữ lại các giá trị đã nhập
                request.setAttribute("tenDangNhap", tenDangNhap);
                request.setAttribute("hoTen", hoTen);
                request.setAttribute("email", email);
                request.setAttribute("soDienThoai", soDienThoai);
                request.setAttribute("vaiTro", vaiTro);

                List<TaiKhoan> taiKhoanList = loadFilteredAccounts(request);
                request.setAttribute("taiKhoanList", taiKhoanList);
                request.setAttribute("currentUser", user);
                request.getRequestDispatcher("/WEB-INF/views/admin/taiKhoanManage.jsp").forward(request, response);
                return;
            }

            // Tài khoản mới luôn được kích hoạt (trang_thai = 1)
            TaiKhoan newTk = new TaiKhoan();
            newTk.setTenDangNhap(tenDangNhap.trim());
            newTk.setMatKhau(PasswordUtil.hash(matKhau)); // Hash mật khẩu
            newTk.setHoTen(hoTen.trim());
            newTk.setEmail(email.trim());
            newTk.setSoDienThoai(soDienThoai == null ? null : soDienThoai.trim());
            newTk.setVaiTro(vaiTro);
            newTk.setTrangThai(1); // Hoạt động

            boolean success = taiKhoanDAO.insert(newTk);
            if (success) {
                request.setAttribute("success", "Tạo tài khoản thành công!");
            } else {
                request.setAttribute("error", "Có lỗi xảy ra khi tạo tài khoản. Vui lòng thử lại.");
            }

            // Lấy lại danh sách sau khi tạo
            List<TaiKhoan> taiKhoanList = loadFilteredAccounts(request);
            request.setAttribute("taiKhoanList", taiKhoanList);
            request.setAttribute("currentUser", user);
            request.getRequestDispatcher("/WEB-INF/views/admin/taiKhoanManage.jsp").forward(request, response);
            return;
        }

        // Xử lý các hành động khác (lock, unlock, role) trên tài khoản hiện có
        String maTkStr = request.getParameter("maTk");
        if (maTkStr == null || maTkStr.isBlank()) {
            request.setAttribute("error", "Thông tin tài khoản không hợp lệ");
            List<TaiKhoan> taiKhoanList = loadFilteredAccounts(request);
            request.setAttribute("taiKhoanList", taiKhoanList);
            request.setAttribute("currentUser", user);
            request.getRequestDispatcher("/WEB-INF/views/admin/taiKhoanManage.jsp").forward(request, response);
            return;
        }

        int maTk = Integer.parseInt(maTkStr);

        // Không cho phép tự thao tác trên chính mình
        if (maTk == user.getMaTk()) {
            request.setAttribute("error", "Không thể thực hiện hành động trên tài khoản của chính bạn");
            List<TaiKhoan> taiKhoanList = loadFilteredAccounts(request);
            request.setAttribute("taiKhoanList", taiKhoanList);
            request.setAttribute("currentUser", user);
            request.getRequestDispatcher("/WEB-INF/views/admin/taiKhoanManage.jsp").forward(request, response);
            return;
        }

        boolean success = false;
        String message = "";

        switch (action) {
            case "lock":
                success = taiKhoanDAO.updateTrangThai(maTk, 0); // 0: khóa
                message = success ? "Khóa tài khoản thành công" : "Khóa tài khoản thất bại";
                break;
            case "unlock":
                success = taiKhoanDAO.updateTrangThai(maTk, 1); // 1: hoạt động
                message = success ? "Mở khóa tài khoản thành công" : "Mở khóa tài khoản thất bại";
                break;
            case "role":
                // Lấy tài khoản hiện tại để xác định vai trò mới
                TaiKhoan tk = taiKhoanDAO.findById(maTk);
                if (tk != null) {
                    String newRole = "ADMIN".equals(tk.getVaiTro()) ? "NHANVIEN" : "ADMIN";
                    tk.setVaiTro(newRole);
                    success = taiKhoanDAO.update(tk);
                    message = success ? "Đổi vai trò thành công" : "Đổi vai trò thất bại";
                }
                break;
            default:
                request.setAttribute("error", "Hành động không hợp lệ");
                List<TaiKhoan> taiKhoanList = loadFilteredAccounts(request);
                request.setAttribute("taiKhoanList", taiKhoanList);
                request.setAttribute("currentUser", user);
                request.getRequestDispatcher("/WEB-INF/views/admin/taiKhoanManage.jsp").forward(request, response);
                return;
        }

        if (success) {
            request.setAttribute("success", message);
        } else {
            request.setAttribute("error", message);
        }

        // Lấy lại danh sách sau khi thực hiện hành động
        List<TaiKhoan> taiKhoanList = loadFilteredAccounts(request);
        request.setAttribute("taiKhoanList", taiKhoanList);
        request.setAttribute("currentUser", user);
        setFilterAttributes(request);
        request.getRequestDispatcher("/WEB-INF/views/admin/taiKhoanManage.jsp").forward(request, response);
    }

    private List<TaiKhoan> loadFilteredAccounts(HttpServletRequest request) {
        String username = normalize(request.getParameter("tenDangNhap"));
        String name = normalize(request.getParameter("hoTen"));
        String email = normalize(request.getParameter("email"));
        String phone = normalize(request.getParameter("soDienThoai"));
        String role = normalize(request.getParameter("vaiTro"));
        String status = normalize(request.getParameter("trangThai"));
        return taiKhoanDAO.findAll().stream()
                .filter(tk -> contains(tk.getTenDangNhap(), username))
                .filter(tk -> contains(tk.getHoTen(), name))
                .filter(tk -> contains(tk.getEmail(), email))
                .filter(tk -> contains(tk.getSoDienThoai(), phone))
                .filter(tk -> role.isEmpty() || role.equalsIgnoreCase(tk.getVaiTro()))
                .filter(tk -> status.isEmpty() || (("1".equals(status) || "0".equals(status))
                        && Integer.toString(tk.getTrangThai()).equals(status)))
                .collect(Collectors.toList());
    }

    private void setFilterAttributes(HttpServletRequest request) {
        request.setAttribute("filterTenDangNhap", valueOrEmpty(request.getParameter("tenDangNhap")));
        request.setAttribute("filterHoTen", valueOrEmpty(request.getParameter("hoTen")));
        request.setAttribute("filterEmail", valueOrEmpty(request.getParameter("email")));
        request.setAttribute("filterSoDienThoai", valueOrEmpty(request.getParameter("soDienThoai")));
        request.setAttribute("filterVaiTro", valueOrEmpty(request.getParameter("vaiTro")));
        request.setAttribute("filterTrangThai", valueOrEmpty(request.getParameter("trangThai")));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private boolean contains(String value, String filter) {
        return filter.isEmpty() || (value != null && value.toLowerCase(Locale.ROOT).contains(filter));
    }
}