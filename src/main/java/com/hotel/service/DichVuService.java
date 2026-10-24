package com.hotel.service;
import com.hotel.model.DichVu;
import com.hotel.repository.DichVuRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service public class DichVuService {
 private final DichVuRepository repository;
 public DichVuService(DichVuRepository repository){this.repository=repository;}
 public List<DichVu> search(String name){return name==null||name.trim().isEmpty()?repository.findAll():repository.findByTenDvContainingIgnoreCaseOrderByTenDvAsc(name.trim());}
 public DichVu get(int id){return repository.findById(id).orElseThrow(()->new IllegalArgumentException("Không tìm thấy dịch vụ."));}
 public DichVu save(DichVu v){return repository.save(v);}
 public void delete(int id){repository.deleteById(id);}
}