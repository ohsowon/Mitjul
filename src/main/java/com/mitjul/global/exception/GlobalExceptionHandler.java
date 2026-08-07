package com.mitjul.global.exception;

import com.mitjul.global.common.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리기(CLAUDE.md §5). 컨트롤러·서비스에서 던진 예외를 한 곳에서 잡아,
 * 항상 ApiResponse 규격의 에러 응답으로 변환한다. 덕분에 개별 컨트롤러는 예외 처리를 신경 쓰지 않는다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 우리가 의도적으로 던진 비즈니스 예외 → ErrorCode에 정의된 상태코드로. */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity.status(code.getStatus()).body(ApiResponse.error(code.getMessage()));
    }

    /** @Valid 검증 실패 → 400 + 어떤 필드가 왜 틀렸는지. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("입력값이 올바르지 않습니다.", fieldErrors));
    }

    /** 요청 본문 JSON이 깨졌을 때 → 400. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(ApiResponse.error("요청 본문을 읽을 수 없습니다. JSON 형식을 확인하세요."));
    }

    /** 그 밖의 예상치 못한 예외 → 500. 원인은 로그로만 남기고, 응답엔 상세를 노출하지 않는다. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e) {
        log.error("처리되지 않은 예외", e);
        return ResponseEntity.internalServerError().body(ApiResponse.error("서버 오류가 발생했습니다."));
    }
}
