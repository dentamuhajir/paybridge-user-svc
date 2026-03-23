package com.paybridge.user.service.repository;

import com.paybridge.user.service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Boolean existsByName(String name);
    Role findByName(String name);
}
