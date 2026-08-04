package com.mitjul.domain.user.dto;

import com.mitjul.domain.user.entity.User;
import java.time.LocalDateTime;

/**
 * 회원가입 응답 DTO.
 *
 * <p><b>password가 없다.</b> 응답에는 절대 비밀번호(해시조차)를 담지 않는다. 엔티티를 그대로
 * 반환하지 않고 이렇게 필요한 필드만 골라 내보내는 이유이기도 하다(엔티티 노출 금지, CLAUDE.md §5).
 */
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
