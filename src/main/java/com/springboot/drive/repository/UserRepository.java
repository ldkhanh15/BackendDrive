package com.springboot.drive.repository;

import com.springboot.drive.domain.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {
    User findByEmailAndEnabled(String email,boolean enabled);

    User findByEmailAndRefreshTokenAndEnabled(String email, String refreshToken,boolean enabled);

    User findByIdAndEnabled(Long id,Boolean enabled);

    User findByEmail(String email);
}
