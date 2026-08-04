package com.mitjul.domain.user.controller;

import com.mitjul.domain.user.dto.UserResponse;
import com.mitjul.domain.user.service.UserService;
import com.mitjul.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 정보 엔드포인트. 인증이 필요한 보호된 경로다(공개 경로 목록에 없으므로 토큰이 있어야 접근 가능).
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 조회. JwtAuthenticationFilter가 인증에 성공하면 principal에 userId가 들어 있고,
     * {@code @AuthenticationPrincipal}로 그 값을 바로 주입받는다.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal Long userId) {
        UserResponse response = userService.getMyInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
