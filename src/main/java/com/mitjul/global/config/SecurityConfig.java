package com.mitjul.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정.
 *
 * <p>이 서비스는 <b>JWT 기반 stateless REST API</b>다. 그래서 전통적인 세션·폼로그인 방식을 전부 끄고,
 * 요청마다 토큰으로 인증하는 구조로 맞춘다. (JWT 필터 자체는 다음 단계에서 추가)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** 인증 없이 열어 둘 경로. 회원가입·로그인과 개발용 H2 콘솔. */
    private static final String[] PUBLIC_PATHS = {
            "/api/v1/auth/**", // 회원가입·로그인
            "/h2-console/**",  // 개발용 H2 콘솔 (dev 프로필에서만 실제로 켜짐)
            "/error",          // 에러 처리용 forward 경로. 안 열면 모든 에러 응답이 403으로 둔갑한다.
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF: 세션/쿠키가 아니라 토큰으로 인증하므로 불필요 → 끈다
                .csrf(AbstractHttpConfigurer::disable)
                // 브라우저 기본 로그인 UI(폼로그인)·기본 인증창(httpBasic)도 쓰지 않는다
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 서버가 세션을 만들지 않는다 = 상태를 안 들고 있다(stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // H2 콘솔은 iframe을 쓰므로 같은 출처(same-origin) 프레임만 허용
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // 경로별 접근 권한: 공개 경로 외에는 전부 인증 필요
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    /**
     * 비밀번호 해시 인코더. 회원가입 시 평문 비밀번호를 BCrypt로 해싱해 저장하고(CLAUDE.md §7),
     * 로그인 시 입력값과 저장된 해시를 대조하는 데 쓴다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
