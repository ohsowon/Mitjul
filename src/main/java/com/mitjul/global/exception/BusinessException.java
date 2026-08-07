package com.mitjul.global.exception;

import lombok.Getter;

/**
 * 비즈니스 규칙 위반을 나타내는 예외. 서비스 계층에서 ErrorCode를 담아 던지면,
 * 전역 핸들러(GlobalExceptionHandler)가 그 ErrorCode의 상태코드·메시지로 응답을 만든다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
