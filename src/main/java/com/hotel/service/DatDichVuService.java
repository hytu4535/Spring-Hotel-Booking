package com.hotel.service;

import com.hotel.model.CartServiceItem;
import com.hotel.model.DatDichVu;
import com.hotel.model.DatDichVuRecord;
import com.hotel.model.DatPhong;
import com.hotel.model.DichVu;
import com.hotel.model.TaiKhoan;
import com.hotel.repository.DatDichVuRepository;
import com.hotel.repository.DatPhongRepository;
import com.hotel.repository.DichVuRepository;
import com.hotel.repository.TaiKhoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatDichVuService {

    private final DatDichVuRepository datDichVuRepository;
    private final DatPhongRepository datPhongRepository;
    private final DichVuRepository dichVuRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    public DatDichVuService(DatDichVuRepository datDichVuRepository,
                            DatPhongRepository datPhongRepository,
                            DichVuRepository dichVuRepository,
                            TaiKhoanRepository taiKhoanRepository) {
        this.datDichVuRepository = datDichVuRepository;
        this.datPhongRepository = datPhongRepository;
        this.dichVuRepository = dichVuRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @Transactional(readOnly = true)
    public List<DatDichVu> findByBooking(Integer maDp) {
        return datDichVuRepository.findByDatPhong_MaDpOrderByNgaySuDungAscMaDatDvAsc(maDp)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addForBooking(int maDp, int maDv, int soLuong, Date ngaySuDung, Integer maNvGhiNhan) {
        if (soLuong < 1 || soLuong > 100) {
            throw new IllegalArgumentException("Số lượng dịch vụ phải từ 1 đến 100.");
        }
        if (ngaySuDung == null) {
            throw new IllegalArgumentException("Ngày sử dụng dịch vụ là bắt buộc.");
        }

        DatPhong datPhong = datPhongRepository.findById(maDp)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt phòng."));
        DichVu dichVu = dichVuRepository.findById(maDv)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dịch vụ."));
        if (dichVu.getTrangThai() != 1) {
            throw new IllegalArgumentException("Dịch vụ đã ngừng cung cấp.");
        }

        DatDichVuRecord record = new DatDichVuRecord();
        record.setDatPhong(datPhong);
        record.setDichVu(dichVu);
        record.setSoLuong(soLuong);
        record.setDonGia(dichVu.getDonGia());
        record.setNgaySuDung(ngaySuDung);
        record.setThanhTien(dichVu.getDonGia().multiply(BigDecimal.valueOf(soLuong)));
        if (maNvGhiNhan != null) {
            record.setNhanVienGhiNhan(taiKhoanRepository.findById(maNvGhiNhan)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên ghi nhận.")));
        }
        datDichVuRepository.save(record);
        recalculateBookingTotal(datPhong);
    }

    @Transactional
    public void addCartServices(int maDp, List<CartServiceItem> services, Date ngaySuDung) {
        if (services == null) {
            return;
        }
        for (CartServiceItem service : services) {
            addForBooking(maDp, service.getMaDv(), service.getSoLuong(), ngaySuDung, null);
        }
    }

    @Transactional
    public void removeFromBooking(int maDatDv, int maDp) {
        DatDichVuRecord record = datDichVuRepository.findById(maDatDv)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dịch vụ trong đơn."));
        if (record.getDatPhong() == null || !Integer.valueOf(maDp).equals(record.getDatPhong().getMaDp())) {
            throw new IllegalArgumentException("Dịch vụ không thuộc đơn đặt phòng này.");
        }
        DatPhong datPhong = record.getDatPhong();
        datDichVuRepository.delete(record);
        recalculateBookingTotal(datPhong);
    }

    private void recalculateBookingTotal(DatPhong datPhong) {
        BigDecimal tienDichVu = datDichVuRepository
                .findByDatPhong_MaDpOrderByNgaySuDungAscMaDatDvAsc(datPhong.getMaDp())
                .stream()
                .map(DatDichVuRecord::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        datPhong.setTienDichVu(tienDichVu);
        datPhong.setTongTien(datPhong.getTienPhong().add(tienDichVu));
        datPhongRepository.save(datPhong);
    }

    private DatDichVu toDto(DatDichVuRecord record) {
        DatDichVu dto = new DatDichVu();
        dto.setMaDatDv(record.getMaDatDv());
        dto.setMaDp(record.getDatPhong().getMaDp());
        dto.setMaDv(record.getDichVu().getMaDv());
        dto.setTenDv(record.getDichVu().getTenDv());
        dto.setSoLuong(record.getSoLuong());
        dto.setGiaTaiThoiDiem(record.getDonGia());
        dto.setNgaySuDung(record.getNgaySuDung());
        dto.setThanhTien(record.getThanhTien());
        if (record.getNhanVienGhiNhan() != null) {
            dto.setMaNvGhiNhan(record.getNhanVienGhiNhan().getMaTk());
            dto.setTenNhanVien(record.getNhanVienGhiNhan().getHoTen());
        }
        return dto;
    }
}
