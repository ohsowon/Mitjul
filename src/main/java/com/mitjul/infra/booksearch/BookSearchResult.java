package com.mitjul.infra.booksearch;

public record BookSearchResult(
        String isbn,
        String title,
        String author,
        String publisher,
        String coverUrl
) {
}