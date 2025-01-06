package com.bobjool.domain.entity;

import com.bobjool.common.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(name = "slack_email")
    private String slackEmail;

    @Column(name = "slack_id")
    private String slackId;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private User(
            String username,
            String password,
            String name,
            String nickname,
            String email,
            String slackEmail,
            String slackId,
            String phoneNumber,
            Boolean isApproved,
            UserRole role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.slackEmail = slackEmail;
        this.slackId = slackId;
        this.phoneNumber = phoneNumber;
        this.isApproved = isApproved;
        this.role = role;
    }

    public static User create(
            String username,
            String password,
            String name,
            String nickname,
            String email,
            String slackEmail,
            String slackId,
            String phoneNumber,
            Boolean isApproved,
            UserRole role) {
        return new User(
                username,
                password,
                name,
                nickname,
                email,
                slackEmail,
                slackId,
                phoneNumber,
                isApproved,
                role
        );
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateSlackEmail(String slackEmail) {
        this.slackEmail = slackEmail;
    }

    public void updateSlackId(String slackId) {
        this.slackId = slackId;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateUserApproval(Boolean approved) {
        this.isApproved = approved;
    }

    public void delete() {
        this.isApproved = false;
    }
}