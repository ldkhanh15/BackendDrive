package com.springboot.drive.service.spec;

import com.springboot.drive.domain.modal.File;
import com.springboot.drive.domain.modal.Folder;
import com.springboot.drive.domain.modal.Item;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public class FolderSpecification {

    public static Specification<Folder> findFolderByParentIsNullAndEnabledAndDeleted(Boolean enabled,
                                                                                     Boolean deleted) {
        return (root, query, builder) -> {
            Predicate isNull = builder.isNull(root.get("parent"));
            Predicate isEnabled = builder.equal(root.get("isEnabled"), enabled);
            Predicate isDeleted = builder.equal(root.get("isDeleted"), deleted);

            query.distinct(true);
            return builder.and(
                    isDeleted,
                    isEnabled,
                    isNull
            );
        };
    }

    public static Specification<Folder> findSubFolderAndIsEnabledAndIsDeleted(
            Long parentId, Boolean enabled, Boolean deleted
    ) {
        return (root, query, builder) -> {
            Predicate isSubFolder = builder.equal(root.get("parent").get("itemId"),parentId);
            Predicate isEnabled = builder.equal(root.get("isEnabled"), enabled);
            Predicate isDeleted = builder.equal(root.get("isDeleted"), deleted);

            query.distinct(true);
            return builder.and(
                    isDeleted,
                    isEnabled,
                    isSubFolder
            );
        };
    }


}
