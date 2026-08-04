package com.mitjul.domain.user.controller;

import com.mitjul.domain.user.dto.SignupRequest;
import com.mitjul.domain.user.dto.SignupResponse;
import com.mitjul.domain.user.service.UserService;
import com.mitjul.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 엔드포인트(회원가입·로그인). 회원 도메인에 속하므로 user 패키지에 둔다.
 *
 * <p>컨트롤러는 얇게 유지한다: 요청 검증({@code @Valid})과 응답 포장(ApiResponse)만 담당하고,
 * 실제 로직은 Service에 위임한다.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /** 회원가입. 성공 시 201 Created + 생성된 회원 정보(비밀번호 제외). */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = userService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "회원가입이 완료되었습니다."));
    }
}
