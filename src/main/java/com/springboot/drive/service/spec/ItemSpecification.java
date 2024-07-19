package com.springboot.drive.service.spec;

import com.springboot.drive.domain.modal.AccessItem;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.ulti.SecurityUtil;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecification {

    public static Specification<Item> findItemsByFolderRootWithAccess(Long folderRootId,
                                                                      Boolean enabled) {
        return (root, query, builder) -> {
            String currentUserEmail = SecurityUtil.getCurrentUserLogin().isPresent() ?
                    SecurityUtil.getCurrentUserLogin().get() : "";

            Predicate isEnabled = builder.equal(root.get("isEnabled"), enabled);
            Predicate parentMatch = builder.equal(root.get("parent").get("itemId"), folderRootId);
            Predicate publicPredicate = builder.isTrue(root.get("isPublic"));

            // Subquery for access items
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<AccessItem> accessItemRoot = subquery.from(AccessItem.class);
            subquery.select(accessItemRoot.get("id"));
            Predicate itemMatch = builder.equal(accessItemRoot.get("item"), root);
            Predicate userMatch = builder.equal(accessItemRoot.get("user").get("email"), currentUserEmail);
            subquery.where(builder.and(itemMatch, userMatch));

            Predicate accessPredicate = builder.exists(subquery);

            query.distinct(true);
            return builder.and(
                    isEnabled,
                    parentMatch,
                    builder.or(publicPredicate, accessPredicate)
            );
        };
    }
}
