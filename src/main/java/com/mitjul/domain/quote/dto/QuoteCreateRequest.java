package com.mitjul.domain.quote.dto;

import com.mitjul.domain.book.dto.BookRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuoteCreateRequest(
        @NotBlank(message = "문장 내용은 필수입니다.") String content,
        Integer page,
        String imageUrl,
        boolean isPublic,
        @NotNull(message = "책 정보는 필수입니다.") @Valid BookRequest book
) {
}
