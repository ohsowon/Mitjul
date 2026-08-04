package com.mitjul.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 요청 헤더의 JWT를 검증해 인증 정보를 SecurityContext에 심는 필터.
 *
 * <p>매 요청마다 한 번 실행된다(OncePerRequestFilter). {@code Authorization: Bearer <token>} 헤더가
 * 있고 토큰이 유효하면 "이 요청은 userId 사용자"라고 SecurityContext에 등록한다. 이후 컨트롤러는
 * {@code @AuthenticationPrincipal}로 그 userId를 꺼내 쓴다.
 *
 * <p>토큰이 없거나 무효하면 아무것도 하지 않고 통과시킨다 → 뒤의 인가 단계에서 보호된 경로는 401 처리된다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        if (token != null && jwtProvider.isValid(token)) {
            Long userId = jwtProvider.getUserId(token);
            // principal에 userId를 담는다. 권한(authorities)은 지금은 비워 둔다(역할 도입은 후순위).
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    /** "Authorization: Bearer <token>" 헤더에서 토큰만 추출한다. 없으면 null. */
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER);
        if (StringUtils.hasText(header) && header.startsWith(PREFIX)) {
            return header.substring(PREFIX.length());
        }
        return null;
    }
}
