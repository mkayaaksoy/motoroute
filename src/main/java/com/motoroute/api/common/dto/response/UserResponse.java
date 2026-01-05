package com.motoroute.api.common.dto.response;

public record UserResponse(
        Long id,
        String name,
        String email
) {}
