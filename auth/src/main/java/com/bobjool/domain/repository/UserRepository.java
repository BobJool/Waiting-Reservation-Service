package com.bobjool.domain.repository;

import com.bobjool.domain.entity.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String email);
}
