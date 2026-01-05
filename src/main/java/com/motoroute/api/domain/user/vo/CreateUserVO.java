package com.motoroute.api.domain.user.vo;

public record CreateUserVO(
        String name,
        String email,
        String password
) {}
