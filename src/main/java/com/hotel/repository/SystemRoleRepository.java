package com.hotel.repository;

import com.hotel.model.SystemRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemRoleRepository extends JpaRepository<SystemRole, Integer> {
    Optional<SystemRole> findByCode(String code);
}
