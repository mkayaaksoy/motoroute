package com.motoroute.api.domain.user.service;

import com.motoroute.api.common.exception.MotoRouteApiBusinessException;
import com.motoroute.api.domain.user.entity.User;
import com.motoroute.api.domain.user.repository.UserRepository;
import com.motoroute.api.domain.user.vo.CreateUserVO;
import com.motoroute.api.domain.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserVO createUser(CreateUserVO createUserVO) {
        log.debug("Creating user with email: {}", createUserVO.email());

        if (userRepository.existsByEmail(createUserVO.email())) {
            throw new MotoRouteApiBusinessException("error.user.already_exists");
        }

        User user = new User();
        user.setName(createUserVO.name());
        user.setEmail(createUserVO.email());
        user.setPassword(passwordEncoder.encode(createUserVO.password()));

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());

        return toUserVO(savedUser);
    }

    @Transactional(readOnly = true)
    public UserVO findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new MotoRouteApiBusinessException("error.user.not_found"));
        return toUserVO(user);
    }

    @Transactional(readOnly = true)
    public UserVO findById(Long id) {
        log.debug("Finding user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new MotoRouteApiBusinessException("error.user.not_found"));
        return toUserVO(user);
    }

    @Transactional(readOnly = true)
    public UserVO findByRefreshToken(String refreshToken) {
        log.debug("Finding user by refresh token");
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new MotoRouteApiBusinessException("error.auth.invalid_token"));
        return toUserVO(user);
    }

    @Transactional
    public void updateRefreshToken(Long userId, String refreshToken) {
        log.debug("Updating refresh token for user id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MotoRouteApiBusinessException("error.user.not_found"));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    @Transactional
    public void removeRefreshToken(Long userId) {
        log.debug("Removing refresh token for user id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MotoRouteApiBusinessException("error.user.not_found"));
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private UserVO toUserVO(User user) {
        return new UserVO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRefreshToken()
        );
    }
}
