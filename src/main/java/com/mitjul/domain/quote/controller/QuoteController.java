package com.mitjul.domain.quote.controller;

import com.mitjul.domain.quote.dto.QuoteCreateRequest;
import com.mitjul.domain.quote.dto.QuoteResponse;
import com.mitjul.domain.quote.dto.QuoteUpdateRequest;
import com.mitjul.domain.quote.service.QuoteService;
import com.mitjul.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 문장 CRUD 엔드포인트. 전부 인증이 필요하며, 본인 문장만 다룰 수 있다.
 * userId는 JWT 필터가 넣어 준 값을 @AuthenticationPrincipal로 받는다(컨트롤러는 얇게 유지).
 */
@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    /** 문장 저장. */
    @PostMapping
    public ResponseEntity<ApiResponse<QuoteResponse>> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody QuoteCreateRequest request) {
        QuoteResponse response = quoteService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "문장이 저장되었습니다."));
    }

    /** 내 문장 목록 (페이징: ?page=0&size=20&sort=createdAt,desc). */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuoteResponse>>> getMyQuotes(
            @AuthenticationPrincipal Long userId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(quoteService.getMyQuotes(userId, pageable)));
    }

    /** 문장 상세. */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteResponse>> getOne(
            @AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(quoteService.getMyQuote(userId, id)));
    }

    /** 문장 수정 (부분). */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteResponse>> update(
            @AuthenticationPrincipal Long userId, @PathVariable Long id,
            @RequestBody QuoteUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(quoteService.update(userId, id, request), "문장이 수정되었습니다."));
    }

    /** 문장 삭제. */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Long userId, @PathVariable Long id) {
        quoteService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null, "문장이 삭제되었습니다."));
    }
}
