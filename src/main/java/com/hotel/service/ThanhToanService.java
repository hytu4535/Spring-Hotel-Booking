
package com.hotel.service;

import com.hotel.model.DatPhong;
import com.hotel.model.ThanhToan;
import com.hotel.util.VNPayUtil;

import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.ThanhToanRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ThanhToanService {

    private static final Logger log =
            LoggerFactory.getLogger(ThanhToanService.class);

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

    // Constructor injection
    public ThanhToanService(
            ThanhToanRepository thanhToanRepository,
            DatPhongRepository datPhongRepository) {

        this.thanhToanRepository = thanhToanRepository;
        this.datPhongRepository = datPhongRepository;
    }

    // =========================================================
    // 1. TÍNH TIỀN ĐẶT CỌC
    // =========================================================

    public BigDecimal calculateDepositAmount(BigDecimal totalAmount) {

        if (totalAmount == null) {
            return BigDecimal.ZERO;
        }

        return totalAmount
                .multiply(new BigDecimal("0.30"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // =========================================================
    // 2. TẠO URL THANH TOÁN VNPAY
    // =========================================================

    public String createVnpayPaymentUrl(
            String maTraCuu,
            HttpServletRequest request) {

        DatPhong dp = datPhongRepository.findByMaTraCuu(maTraCuu)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Không tìm thấy đơn đặt phòng."
                        )
                );

        BigDecimal depositAmount =
                calculateDepositAmount(dp.getTongTien());

        long amountInVnpay = depositAmount
                .multiply(new BigDecimal(100))
                .longValue();

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

        vnp_Params.put(
                "vnp_TxnRef",
                maTraCuu + "_" + System.currentTimeMillis()
        );

        vnp_Params.put(
                "vnp_OrderInfo",
                "Dat coc dat phong ma: " + maTraCuu
        );

        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put(
                "vnp_IpAddr",
                VNPayUtil.getIpAddress(request)
        );

        Calendar cld = Calendar.getInstance(
                TimeZone.getTimeZone("Etc/GMT+7")
        );

        SimpleDateFormat formatter =
                new SimpleDateFormat("yyyyMMddHHmmss");

        String vnp_CreateDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);

        String vnp_ExpireDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Sắp xếp tham số theo tên trường tăng dần
        Map<String, String> sortedParams =
                new TreeMap<>(vnp_Params);

        StringBuilder hashData = new StringBuilder();

        try {

            for (Map.Entry<String, String> entry
                    : sortedParams.entrySet()) {

                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                if (fieldValue == null || fieldValue.isEmpty()) {
                    continue;
                }

                if (hashData.length() > 0) {
                    hashData.append('&');
                }

                hashData.append(
                        URLEncoder.encode(
                                fieldName,
                                StandardCharsets.UTF_8.toString()
                        )
                );

                hashData.append('=');

                hashData.append(
                        URLEncoder.encode(
                                fieldValue,
                                StandardCharsets.UTF_8.toString()
                        )
                );
            }

        } catch (java.io.UnsupportedEncodingException e) {

            throw new IllegalStateException(
                    "Không thể mã hóa tham số VNPay",
                    e
            );
        }

        // Tạo chữ ký
        String vnp_SecureHash = VNPayUtil.hmacSHA512(
                vnp_HashSecret,
                hashData.toString()
        );

        // Tạo URL thanh toán
        String separator = vnp_PayUrl.contains("?") ? "&" : "?";

        String paymentUrl = vnp_PayUrl
                + separator
                + hashData
                + "&vnp_SecureHash="
                + vnp_SecureHash;

        log.info(
                "VNPay payment URL created: {}",
                sanitizePaymentUrlForLog(paymentUrl)
        );

        return paymentUrl;
    }

    // =========================================================
    // 3. LỌC THÔNG TIN NHẠY CẢM TRƯỚC KHI GHI LOG
    // =========================================================

    private String sanitizePaymentUrlForLog(String paymentUrl) {

        return paymentUrl.replaceAll(
                "([?&]vnp_(SecureHash|TxnRef|Amount|IpAddr))=[^&]*",
                "$1=[REDACTED]"
        );
    }

    // =========================================================
    // 4. XỬ LÝ KẾT QUẢ TRẢ VỀ TỪ VNPAY
    // =========================================================

    @Transactional
    public Map<String, Object> processVnpayReturn(
            Map<String, String> params) {

        Map<String, Object> result = new HashMap<>();

        // Tạo bản sao để không làm thay đổi Map đầu vào
        Map<String, String> verifyParams =
                new HashMap<>(params);

        String vnp_SecureHash =
                verifyParams.remove("vnp_SecureHash");

        verifyParams.remove("vnp_SecureHashType");

        // Kiểm tra chữ ký
        String signValue = VNPayUtil.hashAllFields(
                verifyParams,
                vnp_HashSecret
        );

        if (vnp_SecureHash == null
                || !signValue.equalsIgnoreCase(vnp_SecureHash)) {

            result.put("status", "INVALID_SIGNATURE");
            result.put(
                    "message",
                    "Chữ ký không hợp lệ (Invalid signature)."
            );

            return result;
        }

        String txnRef = verifyParams.get("vnp_TxnRef");
        String responseCode = verifyParams.get("vnp_ResponseCode");
        String transactionNo = verifyParams.get("vnp_TransactionNo");
        String amountStr = verifyParams.get("vnp_Amount");

        // Kiểm tra mã giao dịch
        if (txnRef == null || !txnRef.contains("_")) {

            result.put("status", "INVALID_PARAMS");
            result.put("message", "Mã giao dịch không hợp lệ.");

            return result;
        }

        String maTraCuu = txnRef.split("_")[0];

        Optional<DatPhong> dpOpt =
                datPhongRepository.findByMaTraCuu(maTraCuu);

        if (!dpOpt.isPresent()) {

            result.put("status", "ORDER_NOT_FOUND");
            result.put(
                    "message",
                    "Không tìm thấy đơn đặt phòng tương ứng."
            );

            return result;
        }

        DatPhong dp = dpOpt.get();

        result.put("datPhong", dp);
        result.put("maTraCuu", maTraCuu);

        // -----------------------------------------------------
        // Thanh toán thành công
        // -----------------------------------------------------

        if ("00".equals(responseCode)) {

            if (amountStr == null
                    || transactionNo == null
                    || transactionNo.trim().isEmpty()) {

                result.put("status", "INVALID_PARAMS");
                result.put(
                        "message",
                        "Thiếu thông tin giao dịch thanh toán."
                );

                return result;
            }

            BigDecimal amountPaid;

            try {

                amountPaid = new BigDecimal(amountStr)
                        .divide(
                                new BigDecimal(100),
                                2,
                                RoundingMode.HALF_UP
                        );

            } catch (NumberFormatException e) {

                result.put("status", "INVALID_PARAMS");
                result.put(
                        "message",
                        "Số tiền thanh toán không hợp lệ."
                );

                return result;
            }

            // Kiểm tra giao dịch đã được ghi nhận chưa
            boolean exists = thanhToanRepository.findAll()
                    .stream()
                    .anyMatch(t ->
                            transactionNo.equals(t.getMaGiaoDich())
                    );

            if (!exists) {

                ThanhToan tt = new ThanhToan();

                tt.setDatPhong(dp);
                tt.setSoTien(amountPaid);

                tt.setNgayThanhToan(
                        new Timestamp(System.currentTimeMillis())
                );

                tt.setPhuongThuc("VNPAY");
                tt.setLoaiThanhToan("DAT_COC");
                tt.setMaGiaoDich(transactionNo);

                tt.setGhiChu(
                        "Thanh toan dat coc qua VNPay Sandbox"
                );

                thanhToanRepository.save(tt);
            }

            // Tự động xác nhận đơn khi thanh toán thành công
            if ("CHO_XAC_NHAN".equals(dp.getTrangThai())) {

                dp.setTrangThai("DA_XAC_NHAN");

                datPhongRepository.save(dp);
            }

            /*
             * Không cập nhật trạng thái phòng nữa.
             *
             * Phòng được xem là đã đặt thông qua:
             * - dat_phong.trang_thai
             * - dat_phong.ngay_nhan_phong
             * - dat_phong.ngay_tra_phong
             * - chi_tiet_dat_phong.ma_phong
             *
             * Khi trang thái đơn chuyển thành DA_XAC_NHAN,
             * các truy vấn kiểm tra phòng trống sẽ tự nhận biết
             * phòng đã được đặt trong khoảng thời gian tương ứng.
             */

            result.put("status", "SUCCESS");
            result.put(
                    "message",
                    "Thanh toán đặt cọc thành công qua VNPay."
            );

        } else {

            result.put("status", "FAILED");

            result.put(
                    "message",
                    "Thanh toán không thành công. Mã lỗi VNPay: "
                            + responseCode
            );
        }

        return result;
    }

    // =========================================================
    // 5. LẤY DANH SÁCH THANH TOÁN THEO MÃ ĐẶT PHÒNG
    // =========================================================

    @Transactional(readOnly = true)
    public List<ThanhToan> getThanhToanByMaDp(Integer maDp) {

        return thanhToanRepository.findByDatPhong_MaDp(maDp);
    }

    // =========================================================
    // 6. LẤY CHI TIẾT GIAO DỊCH
    // =========================================================

    @Transactional(readOnly = true)
    public ThanhToan getById(Integer maTt) {

        return thanhToanRepository.findById(maTt).orElse(null);
    }

    // =========================================================
    // 7. TÍNH SỐ TIỀN ĐÃ THANH TOÁN THỰC TẾ
    // =========================================================

    @Transactional(readOnly = true)
    public BigDecimal calculatePaidAmount(Integer maDp) {

        return getThanhToanByMaDp(maDp)
                .stream()
                .map(payment -> {

                    BigDecimal amount = payment.getSoTien() == null
                            ? BigDecimal.ZERO
                            : payment.getSoTien();

                    return "HOAN_TIEN".equalsIgnoreCase(
                            payment.getLoaiThanhToan()
                    )
                            ? amount.negate()
                            : amount;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // =========================================================
    // 8. TÌM KIẾM GIAO DỊCH
    // =========================================================

    @Transactional(readOnly = true)
    public List<ThanhToan> searchTransactions(
            String loaiThanhToan,
            String phuongThuc,
            String keyword) {

        if ((loaiThanhToan == null || loaiThanhToan.isEmpty())
                && (phuongThuc == null || phuongThuc.isEmpty())
                && (keyword == null || keyword.isEmpty())) {

            return thanhToanRepository.findAll();
        }

        return thanhToanRepository.searchTransactions(
                loaiThanhToan != null && loaiThanhToan.isEmpty()
                        ? null
                        : loaiThanhToan,

                phuongThuc != null && phuongThuc.isEmpty()
                        ? null
                        : phuongThuc,

                keyword
        );
    }

    // =========================================================
    // 9. ĐỐI SOÁT THANH TOÁN ONLINE
    // =========================================================

    @Transactional(readOnly = true)
    public Map<String, Object> getOnlineReconciliation() {

        List<ThanhToan> vnpayList =
                thanhToanRepository.searchTransactions(
                        null,
                        "VNPAY",
                        null
                );

        BigDecimal totalOnlineAmount = vnpayList.stream()
                .map(ThanhToan::getSoTien)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> report = new HashMap<>();

        report.put("vnpayTransactions", vnpayList);
        report.put("totalTransactions", vnpayList.size());
        report.put("totalOnlineAmount", totalOnlineAmount);
        report.put("reconciliationStatus", "KHỚP (MATCHED)");

        report.put(
                "lastCheckedTime",
                new Timestamp(System.currentTimeMillis())
        );

        return report;
    }

    // =========================================================
    // 10. GHI NHẬN THANH TOÁN THỦ CÔNG
    // =========================================================

    @Transactional
    public Map<String, Object> ghiNhanThanhToanThuCong(
            Integer maDp,
            BigDecimal soTien,
            String loaiThanhToan,
            String phuongThuc,
            String maGiaoDich,
            String ghiChu,
            com.hotel.model.TaiKhoan currentUser) {

        if (maDp == null
                || soTien == null
                || soTien.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Mã đặt phòng và số tiền thanh toán không hợp lệ."
            );
        }

        DatPhong dp = datPhongRepository.findById(maDp)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Không tìm thấy đơn đặt phòng với ID: "
                                        + maDp
                        )
                );

        ThanhToan thanhToan = new ThanhToan();

        thanhToan.setDatPhong(dp);
        thanhToan.setSoTien(soTien);

        thanhToan.setLoaiThanhToan(
                loaiThanhToan != null && !loaiThanhToan.isEmpty()
                        ? loaiThanhToan
                        : "THANH_TOAN_DU"
        );

        thanhToan.setPhuongThuc(
                phuongThuc != null && !phuongThuc.isEmpty()
                        ? phuongThuc
                        : "TIEN_MAT"
        );

        thanhToan.setMaGiaoDich(
                maGiaoDich != null && !maGiaoDich.isEmpty()
                        ? maGiaoDich
                        : "MANUAL_" + System.currentTimeMillis()
        );

        thanhToan.setMaNvThu(
                currentUser != null
                        ? currentUser.getMaTk()
                        : null
        );

        thanhToan.setGhiChu(
                ghiChu != null && !ghiChu.isEmpty()
                        ? ghiChu
                        : "Thu tiền thủ công tại quầy"
        );

        thanhToan.setNgayThanhToan(
                new Timestamp(System.currentTimeMillis())
        );

        thanhToanRepository.save(thanhToan);

        Map<String, Object> result = new HashMap<>();

        result.put("success", true);
        result.put(
                "message",
                "Ghi nhận thanh toán thủ công thành công!"
        );

        return result;
    }

    // =========================================================
    // 11. CẬP NHẬT GIAO DỊCH THANH TOÁN THỦ CÔNG
    // =========================================================

    @Transactional
    public void updateManualPayment(
            Integer maTt,
            BigDecimal soTien,
            String loaiThanhToan,
            String phuongThuc,
            String maGiaoDich,
            String ghiChu,
            com.hotel.model.TaiKhoan currentUser) {

        if (soTien == null
                || soTien.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Số tiền thanh toán phải lớn hơn 0."
            );
        }

        ThanhToan thanhToan = thanhToanRepository.findById(maTt)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Không tìm thấy giao dịch thanh toán."
                        )
                );

        if ("VNPAY".equalsIgnoreCase(
                thanhToan.getPhuongThuc())) {

            throw new IllegalArgumentException(
                    "Không được sửa giao dịch VNPay."
            );
        }

        if (!Arrays.asList(
                "DAT_COC",
                "THANH_TOAN_DU",
                "HOAN_TIEN"
        ).contains(loaiThanhToan)) {

            throw new IllegalArgumentException(
                    "Loại thanh toán không hợp lệ."
            );
        }

        if (!Arrays.asList(
                "TIEN_MAT",
                "CHUYEN_KHOAN",
                "MOMO"
        ).contains(phuongThuc)) {

            throw new IllegalArgumentException(
                    "Phương thức thanh toán không hợp lệ."
            );
        }

        thanhToan.setSoTien(soTien);
        thanhToan.setLoaiThanhToan(loaiThanhToan);
        thanhToan.setPhuongThuc(phuongThuc);

        thanhToan.setMaGiaoDich(
                maGiaoDich == null || maGiaoDich.trim().isEmpty()
                        ? thanhToan.getMaGiaoDich()
                        : maGiaoDich.trim()
        );

        thanhToan.setGhiChu(
                ghiChu == null
                        ? ""
                        : ghiChu.trim()
        );

        thanhToan.setMaNvThu(
                currentUser == null
                        ? thanhToan.getMaNvThu()
                        : currentUser.getMaTk()
        );

        thanhToanRepository.save(thanhToan);
    }
}
