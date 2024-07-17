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

            query.distinct(true);
            return builder.and(
                    isEnabled
            );
        };
    }
}
