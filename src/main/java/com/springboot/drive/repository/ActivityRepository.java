package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.Activity;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.ulti.constant.AccessEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityRepository extends JpaRepository<Activity,Long>, JpaSpecificationExecutor<Activity> {
    Activity findByItem(Item item);

    Activity findByItemAndActivityType(Item item, AccessEnum access);

}
