package com.mitjul.domain.book.dto;

import com.mitjul.domain.book.entity.Book;

/** 도서 응답 DTO. 문장 응답 안에 함께 실린다. */
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
