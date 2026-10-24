package com.hotel.service;

import com.hotel.dao.PhongDAO;
import com.hotel.model.PhongTrongDTO;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class RoomSearchService {
    private final PhongDAO phongDAO = new PhongDAO();

    public java.util.List<PhongTrongDTO> findAvailable(Date checkIn, Date checkOut) {
        return phongDAO.timPhongTrong(checkIn, checkOut);
    }

    public PhongTrongDTO findDetails(int maPhong) {
        return phongDAO.findPhongChiTiet(maPhong);
    }
}
