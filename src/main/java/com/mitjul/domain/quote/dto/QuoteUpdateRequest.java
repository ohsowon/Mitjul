package com.mitjul.domain.quote.dto;

public record QuoteUpdateRequest(
        String content,
        Integer page,
        Boolean isPublic
) {
}
