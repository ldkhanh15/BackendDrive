package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleRepository extends JpaRepository<Role,Long>, JpaSpecificationExecutor<Role> {
    Role findByName(String name);

    boolean existsByName(String name);
}
