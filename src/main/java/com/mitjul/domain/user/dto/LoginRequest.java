package com.mitjul.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO.
 *
 * <p>비밀번호는 길이 규칙을 두지 않고 "비어있지 않음"만 검증한다. 실제 옳고 그름은 저장된 해시와의
 * 대조로 판단하며, 여기서 8자 미만이라고 400을 주면 오히려 기존 계정 로그인을 막을 수 있다.
 */
public record LoginRequest(

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
