package com.mitjul.global.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtProviderTest {
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
        String tampered = token + "tampered";

        assertThat(jwtProvider.isValid(tampered)).isFalse();
    }
}
