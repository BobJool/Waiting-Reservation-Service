package com.bobjool.application.service;

import com.bobjool.application.client.NotificationClient;
import com.bobjool.application.dto.UpdateUserDto;
import com.bobjool.application.dto.UserContactResDto;
import com.bobjool.application.dto.UserResDto;
import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.domain.entity.User;
import com.bobjool.domain.repository.UserRepository;
import com.bobjool.application.dto.UpdateUserResDto;
import feign.FeignException;
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
    private final ValidationService validationService;
    private final NotificationClient notificationClient;

    @Transactional(readOnly = true)
    public UserResDto getUserById(Long id) {

        User user = findUserById(id);

        return UserResDto.from(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResDto> search(Pageable pageable) {

        Page<User> paymentPage = userRepository.search(pageable);

        return paymentPage.map(UserResDto::from);
    }

    @Transactional
    public UpdateUserResDto updateUser(UpdateUserDto request, Long id) {

        validationService.validateDuplicateSlackEmail(request.slackEmail());
        validationService.validateDuplicatePhoneNumber(request.phoneNumber());

        User user = findUserById(id);

        String newPassword = null;

        if (request.currentPassword() != null && request.newPassword() != null) {
            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                throw new BobJoolException(ErrorCode.INVALID_PASSWORD);
            }
            newPassword = passwordEncoder.encode(request.newPassword());
        }

        String slackEmail = request.slackEmail();
        String slackId = null;

        if (slackEmail != null && !slackEmail.isEmpty()) {
            try {
                slackId = notificationClient.findSlackIdByEmail(slackEmail).data().get("slack_id");
            } catch (FeignException e) {
                throw new BobJoolException(ErrorCode.INVALID_SLACK_EMAIL);
            }
        }

        user.update(
                newPassword,
                request.slackEmail(),
                slackId,
                request.phoneNumber()
        );

        return UpdateUserResDto.from(user);
    }

    @Transactional
    public UserResDto updateUserApproval(Long id, Boolean approved) {

        User user = findUserById(id);

        if (!user.isOwner()) {
            throw new BobJoolException(ErrorCode.MISSING_OWNER_ROLE);
        }

        user.updateUserApproval(approved);

        return UserResDto.from(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUserById(id);
        user.delete(id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BobJoolException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public UserContactResDto getContact(Long id) {
        User user = findUserById(id);
        return UserContactResDto.from(user);
    }
}