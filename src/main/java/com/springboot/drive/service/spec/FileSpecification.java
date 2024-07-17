package com.springboot.drive.service.spec;

import com.springboot.drive.domain.modal.AccessItem;
import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Item;
import com.springboot.drive.ulti.constant.AccessEnum;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
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
            Join<File, Item> itemFile = root.join("item");
            Predicate isFile=builder.equal(itemFile.get("parent").get("itemId"),folderId);
            Predicate isEnabled = builder.equal(itemFile.get("isEnabled"), enabled);
            Predicate isDeleted = builder.equal(itemFile.get("isDeleted"), deleted);

            query.distinct(true);
            return builder.and(
                    isDeleted,
                    isEnabled,
                    isFile
            );
        };
    }
}
