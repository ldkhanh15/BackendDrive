package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityRepository extends JpaRepository<Activity,Long>, JpaSpecificationExecutor<Activity> {
}
