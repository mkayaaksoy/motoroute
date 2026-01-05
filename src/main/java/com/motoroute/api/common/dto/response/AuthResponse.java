package com.motoroute.api.common.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        UserDto user
) {
    public record UserDto(
            Long id,
            String name,
            String email
    ) {}
}
