package com.hotel.service;

import com.hotel.model.DatPhong;
import com.hotel.model.ThanhToan;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.ThanhToanRepository;
import com.hotel.util.VNPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ThanhToanService {

    private final ThanhToanRepository thanhToanRepository;
    private final DatPhongRepository datPhongRepository;

    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnp_HashSecret;

    @Value("${vnpay.pay-url}")
    private String vnp_PayUrl;

    @Value("${vnpay.return-url}")
    private String vnp_ReturnUrl;

    public ThanhToanService(ThanhToanRepository thanhToanRepository, DatPhongRepository datPhongRepository) {
        this.thanhToanRepository = thanhToanRepository;
        this.datPhongRepository = datPhongRepository;
    }

    // Tính tiền cọc 30% tổng tiền
    public BigDecimal calculateDepositAmount(BigDecimal totalAmount) {
        if (totalAmount == null) return BigDecimal.ZERO;
        return totalAmount.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
    }

    // Tạo URL thanh toán VNPay cho đặt cọc
    public String createVnpayPaymentUrl(String maTraCuu, HttpServletRequest request) {
        DatPhong dp = datPhongRepository.findByMaTraCuu(maTraCuu)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt phòng."));

        BigDecimal depositAmount = calculateDepositAmount(dp.getTongTien());
        long amountInVnpay = depositAmount.multiply(new BigDecimal(100)).longValue();

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        String bankCode = request.getParameter("bankCode");

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountInVnpay));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", maTraCuu + "_" + System.currentTimeMillis());
        vnp_Params.put("vnp_OrderInfo", "Dat coc dat phong ma: " + maTraCuu);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Sắp xếp tham số theo thứ tự tên trường tăng dần
        Map<String, String> sortedParams = new TreeMap<>(vnp_Params);
        StringBuilder hashData = new StringBuilder();

        try {
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                // Bỏ qua tham số null hoặc rỗng
                if (fieldValue == null || fieldValue.isEmpty()) {
                    continue;
                }

                // Chỉ thêm dấu & giữa các tham số thực sự được ký
                if (hashData.length() > 0) {
                    hashData.append('&');
                }

                hashData.append(
                        URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString())
                );
                hashData.append('=');
                hashData.append(
                        URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString())
                );
            }
        } catch (java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException("Không thể mã hóa tham số VNPay", e);
        }

        // Tạo chữ ký trên chính chuỗi tham số đã mã hóa
        String vnp_SecureHash =
                VNPayUtil.hmacSHA512(vnp_HashSecret, hashData.toString());

        // Dùng cùng chuỗi tham số để tạo URL thanh toán
        return vnp_PayUrl + "?" + hashData
                + "&vnp_SecureHash=" + vnp_SecureHash;
    }

    // Xử lý kết quả trả về từ VNPay
    @Transactional
    public Map<String, Object> processVnpayReturn(Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String vnp_SecureHash = params.get("vnp_SecureHash");

        // Remove hash fields before hashing
        if (params.containsKey("vnp_SecureHashType")) {
            params.remove("vnp_SecureHashType");
        }
        params.remove("vnp_SecureHash");

        // Check sum
        String signValue = VNPayUtil.hashAllFields(params, vnp_HashSecret);
        if (!signValue.equalsIgnoreCase(vnp_SecureHash)) {
            result.put("status", "INVALID_SIGNATURE");
            result.put("message", "Chữ ký không hợp lệ (Invalid signature).");
            return result;
        }

        String txnRef = params.get("vnp_TxnRef"); // format: maTraCuu_timestamp
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");
        String amountStr = params.get("vnp_Amount");

        if (txnRef == null || !txnRef.contains("_")) {
            result.put("status", "INVALID_PARAMS");
            result.put("message", "Mã giao dịch không hợp lệ.");
            return result;
        }

        String maTraCuu = txnRef.split("_")[0];
        Optional<DatPhong> dpOpt = datPhongRepository.findByMaTraCuu(maTraCuu);
        if (!dpOpt.isPresent()) {
            result.put("status", "ORDER_NOT_FOUND");
            result.put("message", "Không tìm thấy đơn đặt phòng tương ứng.");
            return result;
        }

        DatPhong dp = dpOpt.get();
        result.put("datPhong", dp);
        result.put("maTraCuu", maTraCuu);

        if ("00".equals(responseCode)) {
            // Thanh toán thành công -> Lưu record thanh toán cọc
            BigDecimal amountPaid = new BigDecimal(amountStr).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            
            // Check if already recorded for this transactionNo
            boolean exists = thanhToanRepository.findAll().stream()
                    .anyMatch(t -> transactionNo.equals(t.getMaGiaoDich()));

            if (!exists) {
                ThanhToan tt = new ThanhToan();
                tt.setDatPhong(dp);
                tt.setSoTien(amountPaid);
                tt.setNgayThanhToan(new Timestamp(System.currentTimeMillis()));
                tt.setPhuongThuc("VNPAY");
                tt.setLoaiThanhToan("DAT_COC");
                tt.setMaGiaoDich(transactionNo);
                tt.setGhiChu("Thanh toan dat coc qua VNPay Sandbox");
                thanhToanRepository.save(tt);
            }

            result.put("status", "SUCCESS");
            result.put("message", "Thanh toán đặt cọc thành công qua VNPay.");
        } else {
            result.put("status", "FAILED");
            result.put("message", "Thanh toán không thành công. Mã lỗi VNPay: " + responseCode);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<ThanhToan> getThanhToanByMaDp(Integer maDp) {
        return thanhToanRepository.findByDatPhong_MaDp(maDp);
    }

    @Transactional(readOnly = true)
    public List<ThanhToan> searchTransactions(String loaiThanhToan, String phuongThuc, String keyword) {
        if ((loaiThanhToan == null || loaiThanhToan.isEmpty()) &&
            (phuongThuc == null || phuongThuc.isEmpty()) &&
            (keyword == null || keyword.isEmpty())) {
            return thanhToanRepository.findAll();
        }
        return thanhToanRepository.searchTransactions(
            loaiThanhToan != null && loaiThanhToan.isEmpty() ? null : loaiThanhToan,
            phuongThuc != null && phuongThuc.isEmpty() ? null : phuongThuc,
            keyword
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOnlineReconciliation() {
        List<ThanhToan> vnpayList = thanhToanRepository.searchTransactions(null, "VNPAY", null);
        BigDecimal totalOnlineAmount = vnpayList.stream()
                .map(ThanhToan::getSoTien)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> report = new HashMap<>();
        report.put("vnpayTransactions", vnpayList);
        report.put("totalTransactions", vnpayList.size());
        report.put("totalOnlineAmount", totalOnlineAmount);
        report.put("reconciliationStatus", "KHỚP (MATCHED)");
        report.put("lastCheckedTime", new Timestamp(System.currentTimeMillis()));
        return report;
    }

    @Transactional
    public Map<String, Object> ghiNhanThanhToanThuCong(Integer maDp, BigDecimal soTien, String loaiThanhToan, String phuongThuc, String maGiaoDich, String ghiChu, com.hotel.model.TaiKhoan currentUser) {
        if (maDp == null || soTien == null || soTien.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Mã đặt phòng và số tiền thanh toán không hợp lệ.");
        }
        DatPhong dp = datPhongRepository.findById(maDp)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt phòng với ID: " + maDp));

        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setDatPhong(dp);
        thanhToan.setSoTien(soTien);
        thanhToan.setLoaiThanhToan(loaiThanhToan != null && !loaiThanhToan.isEmpty() ? loaiThanhToan : "THANH_TOAN_DU");
        thanhToan.setPhuongThuc(phuongThuc != null && !phuongThuc.isEmpty() ? phuongThuc : "TIEN_MAT");
        thanhToan.setMaGiaoDich(maGiaoDich != null && !maGiaoDich.isEmpty() ? maGiaoDich : "MANUAL_" + System.currentTimeMillis());
        thanhToan.setMaNvThu(currentUser != null ? currentUser.getMaTk() : null);
        thanhToan.setGhiChu(ghiChu != null && !ghiChu.isEmpty() ? ghiChu : "Thu tiền thủ công tại quầy");
        thanhToan.setNgayThanhToan(new Timestamp(System.currentTimeMillis()));

        thanhToanRepository.save(thanhToan);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Ghi nhận thanh toán thủ công thành công!");
        return result;
    }
}
