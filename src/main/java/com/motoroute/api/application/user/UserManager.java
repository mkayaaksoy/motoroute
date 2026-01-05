package com.motoroute.api.application.user;

import com.motoroute.api.common.dto.request.LoginRequest;
import com.motoroute.api.common.dto.request.RefreshTokenRequest;
import com.motoroute.api.common.dto.request.RegisterRequest;
import com.motoroute.api.common.dto.response.AuthResponse;
import com.motoroute.api.common.dto.response.UserResponse;
import com.motoroute.api.common.exception.MotoRouteApiBusinessException;
import com.motoroute.api.domain.user.service.UserService;
import com.motoroute.api.domain.user.vo.CreateUserVO;
import com.motoroute.api.domain.user.vo.UserVO;
import com.motoroute.api.infrastructure.config.JwtProperties;
import com.motoroute.api.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManager {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public AuthResponse register(RegisterRequest request) {
        log.info("Processing registration request for email: {}", request.email());

        CreateUserVO createUserVO = new CreateUserVO(
                request.name(),
                request.email(),
                request.password()
        );

        UserVO userVO = userService.createUser(createUserVO);

        String accessToken = jwtTokenProvider.generateAccessToken(userVO.id(), userVO.email());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userVO.id());

        userService.updateRefreshToken(userVO.id(), refreshToken);

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtProperties.expiration(),
                new AuthResponse.UserDto(userVO.id(), userVO.name(), userVO.email())
        );
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Processing login request for email: {}", request.email());

        UserVO userVO = userService.findByEmail(request.email());

        if (!userService.checkPassword(request.password(), userVO.password())) {
            throw new MotoRouteApiBusinessException("error.user.invalid_credentials");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(userVO.id(), userVO.email());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userVO.id());

        userService.updateRefreshToken(userVO.id(), refreshToken);

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtProperties.expiration(),
                new AuthResponse.UserDto(userVO.id(), userVO.name(), userVO.email())
        );
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Processing refresh token request");

        if (!jwtTokenProvider.validateToken(request.refreshToken())) {
            throw new MotoRouteApiBusinessException("error.auth.invalid_token");
        }

        UserVO userVO = userService.findByRefreshToken(request.refreshToken());

        String accessToken = jwtTokenProvider.generateAccessToken(userVO.id(), userVO.email());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userVO.id());

        userService.updateRefreshToken(userVO.id(), newRefreshToken);

        return new AuthResponse(
                accessToken,
                newRefreshToken,
                jwtProperties.expiration(),
                new AuthResponse.UserDto(userVO.id(), userVO.name(), userVO.email())
        );
    }

    public UserResponse getCurrentUser(Long userId) {
        log.info("Getting current user for id: {}", userId);

        UserVO userVO = userService.findById(userId);

        return new UserResponse(userVO.id(), userVO.name(), userVO.email());
    }
}
