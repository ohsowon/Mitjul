package com.mitjul.domain.book.dto;

import com.mitjul.infra.booksearch.BookSearchResult;

public record BookSearchResponse(
        String isbn,
        String title,
        String author,
        String publisher,
        String coverUrl
) {
    public static BookSearchResponse from(BookSearchResult result) {
        return new BookSearchResponse(result.isbn(), result.title(), result.author(), result.publisher(), result.coverUrl());
    }
}
