package com.mitjul.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 액세스 토큰 생성·검증 유틸.
 *
 * 비밀키(jwt.secret)로 HS256 서명한다. 비밀키는 환경변수로 주입하며(§7) 코드/깃에 두지 않는다.
 * 외부 라이브러리(jjwt) 의존을 이 한 곳에 가둬, 도메인·서비스 코드가 jjwt를 직접 모르게 한다.
 */
@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpireMs;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expire-ms}") long accessTokenExpireMs) {
        // HS256은 최소 256비트(32바이트) 키가 필요하다. 짧으면 여기서 예외가 나 앱이 안 뜬다(fail-fast).
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpireMs = accessTokenExpireMs;
    }

    /** 사용자 식별자(subject)와 이메일(claim)을 담은 액세스 토큰을 만든다. */
    public String createAccessToken(Long userId, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpireMs);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /** 토큰에서 사용자 id를 꺼낸다. (서명·만료 검증 포함) */
    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getSubject());
    }

    /** 서명·만료가 유효한 토큰인지. 위조·만료·형식오류는 모두 false로 처리한다. */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
