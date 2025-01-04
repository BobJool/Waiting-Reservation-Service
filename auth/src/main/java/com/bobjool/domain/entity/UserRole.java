package com.bobjool.domain.entity;

import lombok.Getter;

@Getter
public enum UserRole {
    MASTER(Authority.MASTER),
    OWNER(Authority.OWNER),
    CUSTOMER(Authority.CUSTOMER);

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public static class Authority {
        public static final String MASTER = "ROLE_MASTER";
        public static final String OWNER = "ROLE_OWNER";
        public static final String CUSTOMER = "ROLE_CUSTOMER";
    }
}
