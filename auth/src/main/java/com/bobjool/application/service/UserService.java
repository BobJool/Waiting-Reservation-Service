package com.bobjool.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.domain.entity.User;
import com.bobjool.domain.repository.UserRepository;
import com.bobjool.presentation.dto.response.UserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResDto getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BobJoolException(ErrorCode.USER_NOT_FOUND));

        return UserResDto.of(user);
    }
}
