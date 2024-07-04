package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.AccessItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccessRepository extends JpaRepository<AccessItem,Long>, JpaSpecificationExecutor<AccessItem> {
}
