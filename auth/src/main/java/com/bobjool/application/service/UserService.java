package com.bobjool.application.service;

import com.bobjool.application.dto.UpdateUserDto;
import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.domain.entity.User;
import com.bobjool.domain.entity.UserRole;
import com.bobjool.domain.repository.UserRepository;
import com.bobjool.presentation.dto.response.UpdateUserResDto;
import com.bobjool.presentation.dto.response.UserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResDto getUserById(Long id) {

        User user = findUserById(id);

        return UserResDto.of(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResDto> search(Pageable pageable) {

        Page<User> paymentPage = userRepository.search(pageable);

        return paymentPage.map(UserResDto::of);
    }

    @Transactional
    public UpdateUserResDto updateUser(UpdateUserDto request, Long id) {

        User user = findUserById(id);

        if (request.currentPassword() != null && request.newPassword() != null) {
            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                throw new BobJoolException(ErrorCode.INVALID_PASSWORD);
            }
            user.updatePassword(passwordEncoder.encode(request.newPassword()));
        }

        if (request.slackEmail() != null && !request.slackEmail().isEmpty()) {
            user.updateSlackEmail(request.slackEmail());

            // TODO slack email로 slack id 가져오는 api 호출 후 같이 업데이트 예정
            String slackId = "";

            user.updateSlackId(slackId);
        }

        if (request.phoneNumber() != null && !request.phoneNumber().isEmpty()) {
            user.updatePhoneNumber(request.phoneNumber());
        }

        User updatedUser = userRepository.save(user);

        return UpdateUserResDto.of(updatedUser);
    }

    @Transactional
    public UserResDto updateUserApproval(Long id, Boolean approved) {

        User user = findUserById(id);

        if (user.getRole() != UserRole.OWNER) {
            throw new BobJoolException(ErrorCode.MISSING_OWNER_ROLE);
        }

        user.updateUserApproval(approved);

        userRepository.save(user);

        return UserResDto.of(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUserById(id);

        user.delete();
        user.deleteBase(id);

        userRepository.save(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BobJoolException(ErrorCode.USER_NOT_FOUND));
    }
}