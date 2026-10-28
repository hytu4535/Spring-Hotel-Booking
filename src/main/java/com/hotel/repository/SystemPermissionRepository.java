package com.hotel.repository;

import com.hotel.model.SystemPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemPermissionRepository extends JpaRepository<SystemPermission, Integer> {
    Optional<SystemPermission> findByCode(String code);
    java.util.List<SystemPermission> findAllByOrderByCodeAsc();
}
