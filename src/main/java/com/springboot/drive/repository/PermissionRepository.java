package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission,Long>, JpaSpecificationExecutor<Permission> {
    List<Permission> findByIdIn(List<Long> reqPermissions);

    boolean existsByModuleAndMethodAndApiPath(String module, String method, String apiPath);
}
