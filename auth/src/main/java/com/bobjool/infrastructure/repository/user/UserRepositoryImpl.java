package com.bobjool.infrastructure.repository.user;

import com.bobjool.domain.entity.User;
import com.bobjool.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoryImpl extends JpaRepository<User, Long>, UserRepository, UserRepositoryCustom {
}