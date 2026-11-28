package com.hotel.service;

import com.hotel.dto.DatPhongHistoryDTO;
import com.hotel.dto.KhachHangAdminDTO;
import com.hotel.dto.KhachHangProfileDTO;
import com.hotel.dto.ThanhToanHistoryDTO;
import com.hotel.model.DatPhong;
import com.hotel.model.KhachHang;
import com.hotel.model.TaiKhoan;
import com.hotel.model.ThanhToan;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.KhachHangRepository;
import com.hotel.repository.TaiKhoanRepository;
import com.hotel.repository.ChiTietDatPhongRepository;
import com.hotel.repository.ThanhToanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Comparator;

@Service
public class KhachHangService {
    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final DatPhongRepository datPhongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final ThanhToanRepository thanhToanRepository;

    public KhachHangService(KhachHangRepository khachHangRepository,
            TaiKhoanRepository taiKhoanRepository,
            DatPhongRepository datPhongRepository, ChiTietDatPhongRepository chiTietDatPhongRepository,
            ThanhToanRepository thanhToanRepository) {
        this.khachHangRepository = khachHangRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.datPhongRepository = datPhongRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
        this.thanhToanRepository = thanhToanRepository;
    }

    @Transactional
    public KhachHang getForAccount(int maTk) {
        Optional<KhachHang> linked = khachHangRepository.findByMaTk(maTk);
        if (linked.isPresent()) {
            return linked.get();
        }
        Optional<TaiKhoan> account = taiKhoanRepository.findById(maTk);
        if (account.isEmpty()) {
            return null;
        }
        Optional<KhachHang> emailMatch = khachHangRepository.findByEmailIgnoreCase(account.get().getEmail());
        if (emailMatch.isEmpty() || emailMatch.get().getMaTk() != null) {
            return null;
        }
        KhachHang customer = emailMatch.get();
        customer.setMaTk(maTk);
        return khachHangRepository.save(customer);
    }

    @Transactional
    public KhachHang updateProfile(int maTk, KhachHangProfileDTO request) {
        TaiKhoan account = taiKhoanRepository.findById(maTk)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản."));
        KhachHang customer = getForAccount(maTk);
        if (customer == null) {
            customer = new KhachHang();
            customer.setMaTk(maTk);
            customer.setNgayTao(new java.sql.Timestamp(System.currentTimeMillis()));
        }
        Integer currentCustomerId = customer.getMaKh();
        String email = request.getEmail().trim();
        khachHangRepository.findByEmailIgnoreCase(email).ifPresent(existing -> {
            if (currentCustomerId == null || !existing.getMaKh().equals(currentCustomerId)) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi hồ sơ khách hàng khác.");
            }
        });
        taiKhoanRepository.findAll().stream()
                .filter(item -> item.getEmail() != null && item.getEmail().equalsIgnoreCase(email))
                .filter(item -> item.getMaTk() != maTk)
                .findFirst()
                .ifPresent(item -> {
                    throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác.");
                });
        customer.setHoTen(request.getHoTen().trim());
        customer.setEmail(email);
        customer.setSoDienThoai(normalize(request.getSoDienThoai()));
        customer.setCccd(normalize(request.getCccd()));
        customer.setDiaChi(normalize(request.getDiaChi()));
        account.setHoTen(customer.getHoTen());
        account.setEmail(email);
        account.setSoDienThoai(customer.getSoDienThoai());
        taiKhoanRepository.save(account);
        customer.setMaTk(maTk);
        return khachHangRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<KhachHangAdminDTO> search(String keyword) {
        String value = keyword == null ? "" : keyword.trim();
        return (value.isEmpty() ? khachHangRepository.findAll() : khachHangRepository.search(value))
                .stream().map(this::toAdminDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<KhachHangAdminDTO> searchAdvanced(String hoTen, String email, String phone,
            String cccd, String order) {
        String nameFilter = normalizeFilter(hoTen);
        String emailFilter = normalizeFilter(email);
        String phoneFilter = normalizeFilter(phone);
        String cccdFilter = normalizeFilter(cccd);
        List<KhachHangAdminDTO> result = khachHangRepository.findAllByOrderByNgayTaoDesc().stream()
                .filter(item -> contains(item.getHoTen(), nameFilter))
                .filter(item -> contains(item.getEmail(), emailFilter))
                .filter(item -> contains(item.getSoDienThoai(), phoneFilter))
                .filter(item -> contains(item.getCccd(), cccdFilter))
                .map(this::toAdminDTO)
                .collect(Collectors.toList());
        Comparator<KhachHangAdminDTO> comparator = Comparator.comparingLong(KhachHangAdminDTO::getSoDonDatPhong);
        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        if ("asc".equalsIgnoreCase(order) || "desc".equalsIgnoreCase(order)) {
            result.sort(comparator);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public KhachHangAdminDTO getAdmin(int maKh) {
        return khachHangRepository.findById(maKh).map(this::toAdminDTO)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng."));
    }

    @Transactional
    public KhachHang createProfile(KhachHangProfileDTO request) {
        validateEmail(request.getEmail(), null);
        KhachHang customer = new KhachHang();
        customer.setHoTen(request.getHoTen().trim());
        customer.setEmail(request.getEmail().trim());
        taiKhoanRepository.findByEmailIgnoreCase(customer.getEmail())
                .ifPresent(account -> customer.setMaTk(account.getMaTk()));
        customer.setSoDienThoai(normalize(request.getSoDienThoai()));
        customer.setCccd(normalize(request.getCccd()));
        customer.setDiaChi(normalize(request.getDiaChi()));
        customer.setNgayTao(new java.sql.Timestamp(System.currentTimeMillis()));
        return khachHangRepository.save(customer);
    }

    @Transactional
    public KhachHang updateProfileByAdmin(int maKh, KhachHangProfileDTO request) {
        KhachHang customer = khachHangRepository.findById(maKh)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng."));
        validateEmail(request.getEmail(), maKh);
        customer.setHoTen(request.getHoTen().trim());
        customer.setEmail(request.getEmail().trim());
        customer.setSoDienThoai(normalize(request.getSoDienThoai()));
        customer.setCccd(normalize(request.getCccd()));
        customer.setDiaChi(normalize(request.getDiaChi()));
        if (customer.getMaTk() != null) {
            taiKhoanRepository.findById(customer.getMaTk()).ifPresent(account -> {
                account.setHoTen(customer.getHoTen());
                account.setEmail(customer.getEmail());
                account.setSoDienThoai(customer.getSoDienThoai());
                taiKhoanRepository.save(account);
            });
        }
        return khachHangRepository.save(customer);
    }

    @Transactional
    public void deleteProfile(int maKh) {
        KhachHang customer = khachHangRepository.findById(maKh)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng."));
        if (!datPhongRepository.findByMaKhOrderByNgayDatDesc(maKh).isEmpty()) {
            throw new IllegalArgumentException(
                    "Không thể xóa khách hàng đã có lịch sử đặt phòng. Hãy gộp hồ sơ hoặc khóa tài khoản.");
        }
        khachHangRepository.delete(customer);
    }

    @Transactional(readOnly = true)
    public List<DatPhongHistoryDTO> history(int maKh) {
        return datPhongRepository.findByMaKhOrderByNgayDatDesc(maKh).stream().map(this::toHistory)
                .collect(Collectors.toList());
    }

    @Transactional
    public void merge(int primaryId, int duplicateId) {
        if (primaryId == duplicateId)
            throw new IllegalArgumentException("Hồ sơ chính và hồ sơ trùng không được giống nhau.");
        KhachHang primary = khachHangRepository.findById(primaryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ chính."));
        KhachHang duplicate = khachHangRepository.findById(duplicateId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ trùng."));
        datPhongRepository.moveBookings(duplicate.getMaKh(), primary.getMaKh());
        khachHangRepository.delete(duplicate);
    }

    private KhachHangAdminDTO toAdminDTO(KhachHang item) {
        KhachHangAdminDTO dto = new KhachHangAdminDTO();
        dto.setMaKh(item.getMaKh());
        dto.setMaTk(item.getMaTk());
        dto.setHoTen(item.getHoTen());
        dto.setEmail(item.getEmail());
        dto.setSoDienThoai(item.getSoDienThoai());
        dto.setCccd(item.getCccd());
        dto.setDiaChi(item.getDiaChi());
        dto.setSoDonDatPhong(datPhongRepository.findByMaKhOrderByNgayDatDesc(item.getMaKh()).size());
        return dto;
    }

    private DatPhongHistoryDTO toHistory(DatPhong item) {
        DatPhongHistoryDTO dto = new DatPhongHistoryDTO();
        dto.setMaDp(item.getMaDp());
        dto.setMaTraCuu(item.getMaTraCuu());
        dto.setNgayDat(item.getNgayDat());
        dto.setNgayNhanPhong(item.getNgayNhanPhong());
        dto.setNgayTraPhong(item.getNgayTraPhong());
        dto.setTrangThai(item.getTrangThai());
        dto.setTrangThaiLabel(statusLabel(item.getTrangThai()));
        dto.setTongTien(item.getTongTien());
        dto.setTienPhong(item.getTienPhong());
        dto.setTienDichVu(item.getTienDichVu());
        dto.setSoPhong(chiTietDatPhongRepository.findByMaDp(item.getMaDp()).stream()
                .filter(ct -> ct.getPhong() != null)
                .map(ct -> ct.getPhong().getSoPhong())
                .distinct()
                .collect(Collectors.toList()));
        dto.setLichSuThanhToan(thanhToanRepository.findByDatPhong_MaDp(item.getMaDp()).stream()
                .sorted(Comparator.comparing(
                        ThanhToan::getNgayThanhToan,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(payment -> {
                    ThanhToanHistoryDTO paymentDTO = new ThanhToanHistoryDTO();
                    paymentDTO.setSoTien(payment.getSoTien());
                    paymentDTO.setNgayThanhToan(payment.getNgayThanhToan());
                    paymentDTO.setPhuongThuc(payment.getPhuongThuc());
                    paymentDTO.setMaGiaoDich(payment.getMaGiaoDich());
                    paymentDTO.setLoaiThanhToan(payment.getLoaiThanhToan());
                    return paymentDTO;
                })
                .collect(Collectors.toList()));
        return dto;
    }

    private String statusLabel(String status) {
        if ("CHO_XAC_NHAN".equals(status))
            return "Chờ xác nhận";
        if ("DA_XAC_NHAN".equals(status))
            return "Đã xác nhận";
        if ("DA_NHAN_PHONG".equals(status))
            return "Đã nhận phòng";
        if ("DA_TRA_PHONG".equals(status))
            return "Đã trả phòng";
        if ("DA_HUY".equals(status))
            return "Đã hủy";
        return status == null ? "" : status;
    }

    private String normalize(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String normalizeFilter(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private boolean contains(String value, String filter) {
        return filter.isEmpty() || (value != null && value.toLowerCase().contains(filter));
    }

    private void validateEmail(String email, Integer customerId) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email là bắt buộc.");
        }
        Optional<KhachHang> existing = khachHangRepository.findByEmailIgnoreCase(email.trim());
        if (existing.isPresent() && (customerId == null || existing.get().getMaKh() != customerId)) {
            throw new IllegalArgumentException("Email đã được sử dụng bởi hồ sơ khách hàng khác.");
        }
        taiKhoanRepository.findByEmailIgnoreCase(email.trim()).ifPresent(account -> {
            if (customerId != null) {
                KhachHang customer = khachHangRepository.findById(customerId).orElse(null);
                if (customer != null && customer.getMaTk() != null
                        && customer.getMaTk().equals(account.getMaTk())) {
                    return;
                }
            }
            if (customerId == null || existingAccountBelongsToAnotherCustomer(account.getMaTk(), customerId)) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác.");
            }
        });
    }

    private boolean existingAccountBelongsToAnotherCustomer(int maTk, int customerId) {
        return khachHangRepository.findByMaTk(maTk)
                .map(customer -> customer.getMaKh() != customerId)
                .orElse(false);
    }
}
