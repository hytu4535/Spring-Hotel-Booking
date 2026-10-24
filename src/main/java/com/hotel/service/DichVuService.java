package com.hotel.service;

import com.hotel.dao.DichVuDAO;
import com.hotel.model.DichVu;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DichVuService {

    private final DichVuDAO dichVuDAO = new DichVuDAO();

    public List<DichVu> findAll() {
        return dichVuDAO.findAll();
    }

    public DichVu findById(int maDv) {
        return dichVuDAO.findById(maDv);
    }

    public boolean create(DichVu dv) {
        return dichVuDAO.insert(dv);
    }

    public boolean update(DichVu dv) {
        return dichVuDAO.update(dv);
    }

    public boolean delete(int maDv) {
        return dichVuDAO.delete(maDv);
    }
}
