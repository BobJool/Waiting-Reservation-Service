package com.bobjool.domain.repository;

import com.bobjool.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByNickname(String nickname);

    Optional<User> findBySlackEmail(String slackEmail);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    User save(User user);

    Page<User> search(Pageable pageable);
}