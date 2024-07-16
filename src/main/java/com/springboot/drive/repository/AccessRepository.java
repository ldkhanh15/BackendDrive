package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.AccessItem;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.ulti.constant.AccessEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccessRepository extends JpaRepository<AccessItem,Long>, JpaSpecificationExecutor<AccessItem> {

    void deleteByItemAndUser(Item item, User user);

    List<AccessItem> findByItemAndUser(Item item, User user);

    @Query(
            "SELECT ai FROM AccessItem ai WHERE ai.item = :item " +
                    "AND ai.user = :user AND " +
                    "(ai.accessType = :accessType OR " +
                    "ai.accessType = com.springboot.drive.ulti.constant.AccessEnum.ALL)"
    )
    AccessItem findByItemAndUserAndAccessTypeOrAccessTypeAll(@Param("item") Item item, @Param("user") User user, @Param("accessType") AccessEnum accessType);

    Page<AccessItem> findByItem(Item item, Pageable pageable);
}
