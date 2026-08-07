package com.mitjul.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * 모든 API 응답을 감싸는 공통 래퍼(CLAUDE.md §5, §8).
 *
 * 성공이든 실패든 항상 같은 모양{success, data, message}으로 내보내, 프론트엔드가
 * 응답 파싱을 단순화할 수 있게 한다. null 필드는 JSON에서 생략한다(@JsonInclude).
 *
 * @param <T> data에 담기는 실제 응답 본문 타입
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;

    private ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    /** 실패 응답 (본문 없음). */
    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(false, null, message);
    }

    /** 실패 응답 (검증 필드 오류 등 부가 정보를 data에 담는다). */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, data, message);
    }
}
