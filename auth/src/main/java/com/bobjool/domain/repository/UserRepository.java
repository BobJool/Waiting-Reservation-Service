package com.bobjool.domain.repository;

import com.bobjool.domain.entity.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByNickname(String nickname);

    Optional<User> findBySlackId(String slackId);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    User save(User user);
}
