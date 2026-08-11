package com.mitjul.domain.book.dto;

import com.mitjul.domain.book.entity.Book;
import jakarta.validation.constraints.NotBlank;

public record BookRequest(
        @NotBlank(message = "ISBN은 필수입니다.") String isbn,
        @NotBlank(message = "책 제목은 필수입니다.") String title,
        String author,
        String publisher,
        String coverUrl
) {
    public Book toEntity() {
        return Book.builder()
                .isbn(isbn)
                .title(title)
                .author(author)
                .publisher(publisher)
                .coverUrl(coverUrl)
                .build();
    }
}
