package com.mitjul.domain.user.dto;

import com.mitjul.domain.user.entity.User;
import java.time.LocalDateTime;

/**
 * 회원 정보 응답 (비밀번호 제외). "내 정보 조회" 등에서 사용한다.
 */
public record UserResponse(
        Long id,
        String email,
        String nickname,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname(), user.getCreatedAt());
    }
}
