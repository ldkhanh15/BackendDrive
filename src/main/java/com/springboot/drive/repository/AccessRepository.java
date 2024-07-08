package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.AccessItem;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.ulti.constant.AccessEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccessRepository extends JpaRepository<AccessItem,Long>, JpaSpecificationExecutor<AccessItem> {

    void deleteByItemAndUser(Item item, User user);

    AccessItem findByItemAndUser(Item item, User user);

    AccessItem findByItemAndUserAndAccessType(Item item, User user, AccessEnum accessType);
}
