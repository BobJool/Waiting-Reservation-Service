package com.bobjool.application.service;

import com.bobjool.common.exception.*;
import com.bobjool.common.exception.BobJoolException;
import com.bobjool.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final UserRepository userRepository;

    public void validateDuplicateUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BobJoolException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    public void validateDuplicateNickname(String nickname) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new BobJoolException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validateDuplicateSlackId(String slackId) {
        if (userRepository.findBySlackId(slackId).isPresent()) {
            throw new BobJoolException(ErrorCode.DUPLICATE_SLACK_ID);
        }
    }

    public void validateDuplicateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BobJoolException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    public void validateDuplicatePhoneNumber(String phoneNumber) {
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BobJoolException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }
    }
}
