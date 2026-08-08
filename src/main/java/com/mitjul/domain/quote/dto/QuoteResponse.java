package com.mitjul.domain.quote.dto;

import com.mitjul.domain.book.dto.BookResponse;
import com.mitjul.domain.quote.entity.Quote;
import java.time.LocalDateTime;

/**
 * 문장 응답 DTO. 연관된 책 정보(BookResponse)를 함께 담는다.
 *
 * 주의: Quote.getBook()은 지연 로딩이라, 이 변환은 반드시 트랜잭션(Service) 안에서 호출해야 한다.
 * 컨트롤러에서 호출하면 세션이 닫혀 LazyInitializationException이 난다(open-in-view: false).
 */
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
