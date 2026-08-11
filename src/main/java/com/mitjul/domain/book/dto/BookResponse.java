package com.mitjul.domain.book.dto;

import com.mitjul.domain.book.entity.Book;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        String author,
        String publisher,
        String coverUrl
) {
    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getIsbn(), book.getTitle(),
                book.getAuthor(), book.getPublisher(), book.getCoverUrl());
    }
}
