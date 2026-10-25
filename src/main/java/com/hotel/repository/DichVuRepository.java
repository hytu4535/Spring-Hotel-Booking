package com.hotel.repository;
import com.hotel.model.DichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DichVuRepository extends JpaRepository<DichVu,Integer>{
 List<DichVu> findByTenDvContainingIgnoreCaseOrderByTenDvAsc(String tenDv);
}
