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
        public static final String MASTER = "MASTER";
        public static final String OWNER = "OWNER";
        public static final String CUSTOMER = "CUSTOMER";
    }
}
