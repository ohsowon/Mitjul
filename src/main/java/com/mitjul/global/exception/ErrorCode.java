package com.mitjul.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션이 의도적으로 발생시키는 오류의 목록.
 *
 * 각 항목이 "어떤 HTTP 상태코드로, 어떤 메시지를 줄지"를 함께 들고 있어, 서비스는 상황에 맞는
 * ErrorCode만 골라 던지고(BusinessException) 상태코드·메시지는 전역 핸들러가 여기서 읽어 처리한다.
 * 새로운 오류 유형은 여기에 추가한다.
 */
@Getter
public enum ErrorCode {

    // 회원 / 인증
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
