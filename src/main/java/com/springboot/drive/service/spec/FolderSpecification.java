package com.springboot.drive.service.spec;

import com.springboot.drive.domain.modal.*;
import com.springboot.drive.ulti.SecurityUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public class FolderSpecification {

    public static Specification<Folder> findFolderByParentIsNullAndEnabledAndDeleted(Boolean enabled,
                                                                                     Boolean deleted,String name) {
        return (root, query, builder) -> {
            Predicate isNull = builder.isNull(root.get("parent"));
            Predicate isEnabled = builder.equal(root.get("isEnabled"), enabled);
            Predicate isDeleted = builder.equal(root.get("isDeleted"), deleted);
            Predicate isName=builder.like(root.get("folderName"),"%"+name+"%");
            query.distinct(true);
            if (name != null && !name.isEmpty()) {
                return builder.and(
                        isEnabled,
                       isName
                );
            } else {
                return builder.and(
                        isDeleted,
                        isEnabled,
                        isNull
                );
            }

        };
    }

    public static Specification<Folder> findSubFolderAndIsEnabledAndIsDeleted(
            Long parentId, Boolean enabled, Boolean deleted
    ) {
        return (root, query, builder) -> {
            Predicate isSubFolder = builder.equal(root.get("parent").get("itemId"), parentId);
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

    public static Specification<Folder> findFolderByParentAndEnabledAndDeletedWithAccess(Folder parent,
                                                                                         Boolean enabled,
                                                                                         Boolean deleted,
                                                                                         String searchQuery) {
        return (root, query, builder) -> {
            String currentUserEmail = SecurityUtil.getCurrentUserLogin().isPresent() ?
                    SecurityUtil.getCurrentUserLogin().get() :
                    "";
            Predicate isEnabled = builder.equal(root.get("isEnabled"), enabled);
            Predicate isDeleted = builder.equal(root.get("isDeleted"), deleted);
            Predicate isFolder = builder.equal(root.get("parent"), parent);
            Predicate ownerPredicate = builder.equal(root.get("user").get("email"), currentUserEmail);

            Predicate publicPredicate = builder.isTrue(root.get("isPublic"));

            System.out.println(currentUserEmail);


            // Subquery for access items
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<AccessItem> accessItemRoot = subquery.from(AccessItem.class);
            subquery.select(accessItemRoot.get("id"));
            Predicate itemMatch = builder.equal(accessItemRoot.get("item"), root);
            Predicate userMatch = builder.equal(accessItemRoot.get("user").get("email"), currentUserEmail);
            subquery.where(builder.and(itemMatch, userMatch));

            Predicate accessPredicate = builder.exists(subquery);
            Predicate searchPredicate = builder.like(root.get("folderName"), "%" + searchQuery + "%");
            query.distinct(true);
            return builder.and(
                    isDeleted,
                    isEnabled,
                    isFolder,
                    builder.or(ownerPredicate, publicPredicate, accessPredicate),
                    searchPredicate
            );
        };
    }

    public static Specification<Folder> findFolderByIdAndEnabledAndDeletedWithAccess(Long folderId,
                                                                                     Boolean enabled,
                                                                                     Boolean deleted) {
        return (root, query, builder) -> {
            String currentUserEmail = SecurityUtil.getCurrentUserLogin().isPresent() ?
                    SecurityUtil.getCurrentUserLogin().get() :
                    "";
            Predicate isEnabled = builder.equal(root.get("isEnabled"), enabled);
            Predicate isDeleted = builder.equal(root.get("isDeleted"), deleted);
            Predicate isFolder = builder.equal(root.get("itemId"), folderId);
            Predicate ownerPredicate = builder.equal(root.get("user").get("email"), currentUserEmail);

            Predicate publicPredicate = builder.isTrue(root.get("isPublic"));

            System.out.println(currentUserEmail);


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
                    isDeleted,
                    isEnabled,
                    isFolder,
                    builder.or(ownerPredicate, publicPredicate, accessPredicate)
                    );
        };
    }
}
