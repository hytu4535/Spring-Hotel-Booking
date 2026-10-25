package com.hotel.repository;

import com.hotel.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer> {
    List<RolePermission> findByRoleCode(String code);
    Optional<RolePermission> findByRoleCodeAndPermissionCode(String roleCode, String permissionCode);
    void deleteByRoleCode(String code);
}
