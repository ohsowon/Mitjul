package com.mitjul.domain.book.dto;

import com.mitjul.domain.book.entity.Book;
import jakarta.validation.constraints.NotBlank;

/**
 * 도서 정보 입력 DTO. 문장을 저장할 때 함께 받아 "있으면 재사용, 없으면 생성"에 쓴다.
 * (마일스톤 5에서 외부 도서 검색 API가 이 값을 자동으로 채워 주도록 확장된다.)
 */
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
