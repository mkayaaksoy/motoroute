package com.motoroute.api.domain.user.vo;

public record UserVO(
        Long id,
        String name,
        String email,
        String password,
        String refreshToken
) {}
