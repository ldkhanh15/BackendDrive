package com.springboot.drive.service.spec;

import com.springboot.drive.domain.modal.AccessItem;
import com.springboot.drive.domain.modal.Favourite;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.ulti.constant.AccessEnum;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class FavouriteSpecification {

    public static Specification<Favourite> getFavouritesWithAccess(Long userId) {
        return (root, query, builder) -> {
            Join<Favourite, Item> itemJoin = root.join("item");
            Join<Item, AccessItem> accessItemJoin = itemJoin.join("accessItems", JoinType.LEFT);

            Predicate isPublic = builder.equal(itemJoin.get("isPublic"), true);
            Predicate isEnabled=builder.equal(itemJoin.get("isEnabled"), true);
            Predicate hasViewAccess = builder.and(
                    builder.equal(accessItemJoin.get("user").get("id"), userId),
                    accessItemJoin.get("accessType").in(AccessEnum.VIEW, AccessEnum.UPDATE,AccessEnum.CREATE,
                            AccessEnum.DELETE, AccessEnum.SOFT_DELETE,AccessEnum.ALL)
            );

            query.distinct(true);
            return builder.and(
                    builder.equal(root.get("user").get("id"), userId),
                    builder.or(isPublic, hasViewAccess),
                    isEnabled
            );
        };
    }
}
