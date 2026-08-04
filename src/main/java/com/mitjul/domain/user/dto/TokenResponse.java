package com.mitjul.domain.user.dto;

/**
 * 로그인 성공 응답.
 *
 * <p>클라이언트는 이후 요청에 {@code Authorization: Bearer <accessToken>} 헤더를 붙인다(CLAUDE.md §8).
 * {@code tokenType}을 함께 내려 프론트가 헤더를 조립하기 쉽게 한다.
 */
public record TokenResponse(
        String accessToken,
        String tokenType
) {
    public static TokenResponse bearer(String accessToken) {
        return new TokenResponse(accessToken, "Bearer");
    }
}
