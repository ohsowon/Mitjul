package com.mitjul.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    QUOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "문장을 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "이 문장에 대한 권한이 없습니다."),

    EMPTY_IMAGE(HttpStatus.BAD_REQUEST, "이미지 파일이 비어 있습니다."),
    IMAGE_STORE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장에 실패했습니다."),
    OCR_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지에서 텍스트를 추출하지 못했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
