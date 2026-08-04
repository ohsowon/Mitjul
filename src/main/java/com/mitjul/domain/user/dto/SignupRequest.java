package com.mitjul.domain.user.dto;

import com.mitjul.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청 DTO.
 *
 * <p>검증 애너테이션은 <b>엔티티가 아니라 DTO에</b> 둔다(입력 검증은 표현 계층의 관심사).
 * 컨트롤러에서 {@code @Valid}로 이 규칙들을 강제한다.
 */
public record SignupRequest(

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하여야 합니다.")
        String password,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하여야 합니다.")
        String nickname
) {
    /**
     * 검증된 요청 + <b>해싱된</b> 비밀번호로 User 엔티티를 만든다.
     * 평문 비밀번호를 그대로 엔티티에 넣지 않기 위해, 해시는 Service에서 만들어 인자로 받는다.
     */
    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .build();
    }
}
