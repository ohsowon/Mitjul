package com.mitjul.domain.community.dto;

import com.mitjul.domain.quote.entity.Quote;

import java.time.LocalDateTime;

public record CommunityQuoteResponse(
        Long id,
        String content,
        Integer page,
        String nickname,
        String bookTitle,
        String author,
        LocalDateTime createdAt
) {
    public static CommunityQuoteResponse from(Quote quote) {
        return new CommunityQuoteResponse(
                quote.getId(),
                quote.getContent(),
                quote.getPage(),
                quote.getUser().getNickname(),
                quote.getBook().getTitle(),
                quote.getBook().getAuthor(),
                quote.getCreatedAt());
    }
}
