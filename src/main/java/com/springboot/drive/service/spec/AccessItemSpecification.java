package com.springboot.drive.service.spec;

import com.springboot.drive.domain.modal.AccessItem;
import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.domain.modal.User;
import com.springboot.drive.ulti.constant.AccessEnum;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class AccessItemSpecification {


    public static Specification<AccessItem> findByItemAndUserAndAccessType(Item item, User user, Boolean enabled,
                                                                           Boolean deleted, AccessEnum accessType) {
        return (root, query, builder) -> {
            Predicate isEnabled = builder.equal(root.get("item").get("isEnabled"), enabled);
            Predicate isDeleted = builder.equal(root.get("item").get("isDeleted"), deleted);
            Predicate itemPredicate = builder.equal(root.get("item"), item);
            Predicate userPredicate = builder.equal(root.get("user"), user);
            Predicate accessTypePredicate = builder.or(
                    builder.equal(root.get("accessType"), accessType),
                    builder.equal(root.get("accessType"), AccessEnum.ALL)
            );

            return builder.and(
                    itemPredicate,
                    userPredicate,
                    accessTypePredicate,
                    isDeleted,
                    isEnabled
            );
        };

    }
}
