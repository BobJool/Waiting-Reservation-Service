package com.bobjool.application.service;

import com.bobjool.application.dto.UpdateUserDto;
import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.domain.entity.User;
import com.bobjool.domain.repository.UserRepository;
import com.bobjool.presentation.dto.response.UpdateUserResDto;
import com.bobjool.presentation.dto.response.UserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResDto getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BobJoolException(ErrorCode.USER_NOT_FOUND));

        return UserResDto.of(user);
    }

    public Page<UserResDto> search(Pageable pageable) {

        Page<User> paymentPage = userRepository.search(pageable);

        return paymentPage.map(UserResDto::of);
    }

    public UpdateUserResDto updateUser(UpdateUserDto request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BobJoolException(ErrorCode.USER_NOT_FOUND));

        if (request.currentPassword() != null && request.newPassword() != null) {
            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                throw new BobJoolException(ErrorCode.INVALID_PASSWORD);
            }
            user.updatePassword(passwordEncoder.encode(request.newPassword()));
        }

        if (request.slackEmail() != null && !request.slackEmail().isEmpty()) {
            user.updateSlackEmail(request.slackEmail());

            // slack email로 slack id 가져오는 api 호출 후 같이 업데이트
            String slackId = "";

            user.updateSlackId(slackId);
        }

        if (request.phoneNumber() != null && !request.phoneNumber().isEmpty()) {
            user.updatePhoneNumber(request.phoneNumber());
        }

        User updatedUser = userRepository.save(user);

        return UpdateUserResDto.of(updatedUser);
    }

}