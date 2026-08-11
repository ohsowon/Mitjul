package com.mitjul.domain.quote.dto;

import com.mitjul.domain.book.dto.BookResponse;
import com.mitjul.domain.quote.entity.Quote;
import java.time.LocalDateTime;

public record QuoteResponse(
        Long id,
        String content,
        Integer page,
        String imageUrl,
        boolean isPublic,
        LocalDateTime createdAt,
        BookResponse book
) {
    public static QuoteResponse from(Quote quote) {
        return new QuoteResponse(
                quote.getId(),
                quote.getContent(),
                quote.getPage(),
                quote.getImageUrl(),
                quote.isPublic(),
                quote.getCreatedAt(),
                BookResponse.from(quote.getBook()));
    }
}
