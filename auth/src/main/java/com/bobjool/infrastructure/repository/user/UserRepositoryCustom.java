package com.bobjool.infrastructure.repository.user;

import com.bobjool.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<User> search(Pageable pageable);
}