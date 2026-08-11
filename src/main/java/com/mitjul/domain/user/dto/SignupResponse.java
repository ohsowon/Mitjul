package com.mitjul.domain.user.dto;

import com.mitjul.domain.user.entity.User;
import java.time.LocalDateTime;

public record SignupResponse(
        Long id,
        String email,
        String nickname,
        LocalDateTime createdAt
) {
    public static SignupResponse from(User user) {
        return new SignupResponse(user.getId(), user.getEmail(), user.getNickname(), user.getCreatedAt());
    }
}
