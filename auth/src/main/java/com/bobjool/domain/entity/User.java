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

    @Column(name = "slack_id", nullable = false)
    private String slackId;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

}

