package com.mitjul.global.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * JwtProvider 단위 테스트 — 스프링 컨텍스트 없이 직접 생성해 검증한다(순수 로직).
 */
class JwtProviderTest {

    // 테스트용 비밀키도 32바이트 이상이어야 HS256 키로 유효하다.
    private final JwtProvider jwtProvider =
            new JwtProvider("test-secret-key-for-jwt-provider-unit-test-0123456789", 3_600_000L);

    @Test
    @DisplayName("생성한 토큰을 파싱하면 담았던 userId가 그대로 나온다")
    void createAndParse_roundTrip() {
        String token = jwtProvider.createAccessToken(42L, "reader@mitjul.com");

        assertThat(jwtProvider.isValid(token)).isTrue();
        assertThat(jwtProvider.getUserId(token)).isEqualTo(42L);
    }

    @Test
    @DisplayName("변조된 토큰은 유효하지 않다")
    void tamperedToken_isInvalid() {
        String token = jwtProvider.createAccessToken(42L, "reader@mitjul.com");
        String tampered = token + "tampered"; // 서명 뒤를 훼손

        assertThat(jwtProvider.isValid(tampered)).isFalse();
    }
}
