package com.motoroute.api.domain.user.entity;

import com.motoroute.api.common.entity.AbstractIdStatusEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email")
})
public class User extends AbstractIdStatusEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;
}
