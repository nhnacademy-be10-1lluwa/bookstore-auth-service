package com.nhnacademy.illuwa.domain.auth.repository;

import com.nhnacademy.illuwa.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
