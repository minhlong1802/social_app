package com.training.social_app.entity;

import com.training.social_app.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="username", nullable = false, unique = true)
    private String username;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name="otp")
    private String otp;

    @Column(name="otp_expiry")
    private LocalDateTime otpExpiry;

    @Column(name="is_verified")
    private Boolean isVerified = false;

    @Column(name="forgot_password_token")
    private String forgotPasswordToken;

    @Column(name="forgot_password_token_expiry")
    private LocalDateTime forgotPasswordTokenExpiry;

    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name="updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile userProfile;

}
