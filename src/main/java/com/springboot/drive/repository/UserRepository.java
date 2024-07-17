package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {
    User findByEmail(String email);

    User findByEmailAndRefreshToken(String email, String refreshToken);

    User findByIdAndEnabled(Long id,Boolean enabled);
}
