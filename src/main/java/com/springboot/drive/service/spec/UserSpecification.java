package com.springboot.drive.service.spec;

import com.springboot.drive.domain.modal.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> findByEnabled(
            Boolean enabled
    ) {
        return (root, query, builder) -> {
            Predicate isEnabled = builder.equal(root.get("enabled"), enabled);
            return builder.and(
                    isEnabled
            );
        };
    }

    public static Specification<User> findByEnabledAndNameOrEmail(
            Boolean enabled, String name
    ) {
        return (root, query, builder) -> {
            Predicate isEnabled = builder.equal(root.get("enabled"), enabled);
            Predicate likeName = builder.like(root.get("name"), "%"+name+"%");
            Predicate likeEmail = builder.like(root.get("email"), "%"+name+"%");
            if (name != null && !name.isEmpty()) {
                return builder.and(
                        isEnabled,
                        builder.or(likeName, likeEmail)
                );
            } else {
                return builder.and(
                        isEnabled
                );
            }
        };
    }
}
