package com.springboot.drive.service.spec;

import com.springboot.drive.domain.modal.AccessItem;
import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.ulti.SecurityUtil;
import com.springboot.drive.ulti.constant.AccessEnum;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public class FileSpecification {

    public static Specification<File> findAccessibleFiles(Long userId, Long folderId, Boolean enabled,
                                                          Boolean deleted) {
        return (root, query, builder) -> {
            Join<File, Item> itemFile = root.join("item");
            Join<Item, AccessItem> accessItem = itemFile.join("accessItems", JoinType.LEFT);

            Predicate isPublic = builder.equal(itemFile.get("isPublic"), true);
            Predicate isEnabled = builder.equal(itemFile.get("isEnabled"), enabled);
            Predicate isDeleted = builder.equal(itemFile.get("isDeleted"), deleted);
            Predicate hasViewAccess = builder.and(
                    builder.equal(accessItem.get("user").get("id"), userId),
                    accessItem.get("accessType").in(AccessEnum.VIEW, AccessEnum.UPDATE, AccessEnum.CREATE,
                            AccessEnum.DELETE, AccessEnum.SOFT_DELETE, AccessEnum.ALL)
            );
            Predicate isParent = builder.equal(itemFile.get("parent").get("itemId"), folderId);

            query.distinct(true);
            return builder.and(
                    isParent,
                    isDeleted,
                    isEnabled,
                    builder.or(
                            isPublic,
                            hasViewAccess
                    )
            );
        };
    }

    public static Specification<File> findAccessibleFilesByAccessType(Long userId, Long folderId, Boolean enabled,
                                                                      Boolean deleted, AccessEnum accessType) {
        return (root, query, builder) -> {
            Join<File, Item> itemFile = root.join("item");
            Join<Item, AccessItem> accessItem = itemFile.join("accessItems", JoinType.LEFT);

            Predicate isPublic = builder.equal(itemFile.get("isPublic"), true);
            Predicate isEnabled = builder.equal(itemFile.get("isEnabled"), enabled);
            Predicate isDeleted = builder.equal(itemFile.get("isDeleted"), deleted);
            Predicate hasViewAccess = builder.and(
                    builder.equal(accessItem.get("user").get("id"), userId),
                    builder.equal(accessItem.get("accessType"), accessType)
            );
            Predicate isParent = builder.equal(itemFile.get("parent").get("itemId"), folderId);
            Predicate accessCondition;
            if (accessType == AccessEnum.VIEW) {
                accessCondition = builder.or(isPublic, hasViewAccess);
            } else {
                accessCondition = hasViewAccess;
            }
            query.distinct(true);
            return builder.and(
                    isParent,
                    isDeleted,
                    isEnabled,
                    accessCondition
            );
        };
    }

    public static Specification<File> findByParentIdEnabledAndDelete(Long folderId, Boolean enabled,
                                                                      Boolean deleted) {
        return (root, query, builder) -> {
            Predicate isFile=builder.equal(root.get("parent").get("itemId"),folderId);
            Predicate isEnabled = builder.equal(root.get("isEnabled"), enabled);
            Predicate isDeleted = builder.equal(root.get("isDeleted"), deleted);

            query.distinct(true);
            return builder.and(
                    isDeleted,
                    isEnabled,
                    isFile
            );
        };
    }
    public static Specification<File> findFilesByFolderRootWithAccess(Folder  parent,Boolean enabled,Boolean deleted,
                                                                      String searchQuery) {
        return (root, query, builder) -> {
            String currentUserEmail = SecurityUtil.getCurrentUserLogin().isPresent() ?
                    SecurityUtil.getCurrentUserLogin().get() : "";

            Predicate isDeleted=builder.equal(root.get("isDeleted"),deleted);
            Predicate isEnabled = builder.equal(root.get("isEnabled"), enabled);
            Predicate parentMatch = builder.equal(root.get("parent"), parent);
            Predicate publicPredicate = builder.isTrue(root.get("isPublic"));
            Predicate ownerPredicate = builder.equal(root.get("user").get("email"), currentUserEmail);
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<AccessItem> accessItemRoot = subquery.from(AccessItem.class);
            subquery.select(accessItemRoot.get("id"));
            Predicate itemMatch = builder.equal(accessItemRoot.get("item"), root);
            Predicate userMatch = builder.equal(accessItemRoot.get("user").get("email"), currentUserEmail);
            subquery.where(builder.and(itemMatch, userMatch));

            Predicate accessPredicate = builder.exists(subquery);
            Predicate searchPredicate = builder.like(root.get("fileName"), "%" + searchQuery + "%");
            query.distinct(true);
            if (searchQuery != null && !searchQuery.isEmpty()) {
                return builder.and(
                        isEnabled,
                        parentMatch,
                        builder.or(publicPredicate, accessPredicate,ownerPredicate),
                        searchPredicate,
                        isDeleted
                );
            } else {
                return builder.and(
                        isEnabled,
                        parentMatch,
                        builder.or(publicPredicate, accessPredicate,ownerPredicate),
                        isDeleted
                );
            }


        };
    }
}
