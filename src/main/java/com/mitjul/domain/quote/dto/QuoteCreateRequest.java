package com.mitjul.domain.quote.dto;

import com.mitjul.domain.book.dto.BookRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 문장 저장 요청 DTO. 문장 본문과 함께, 어느 책에서 나온 문장인지 책 정보(BookRequest)를 받는다.
 * 중첩된 book도 검증하도록 @Valid를 붙인다.
 */
public record QuoteCreateRequest(
        @NotBlank(message = "문장 내용은 필수입니다.") String content,
        Integer page,
        String imageUrl,
        boolean isPublic,
        @NotNull(message = "책 정보는 필수입니다.") @Valid BookRequest book
) {
}
