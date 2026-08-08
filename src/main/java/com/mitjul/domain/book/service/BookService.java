package com.mitjul.domain.book.service;

import com.mitjul.domain.book.dto.BookRequest;
import com.mitjul.domain.book.entity.Book;
import com.mitjul.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 도서 도메인 서비스.
 */
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    /**
     * ISBN 기준 "있으면 재사용, 없으면 생성"(CLAUDE.md §4).
     * 동시에 같은 ISBN이 들어와도 books.isbn 유니크 제약이 최후의 방어선이 된다.
     */
    @Transactional
    public Book findOrCreate(BookRequest request) {
        return bookRepository.findByIsbn(request.isbn())
                .orElseGet(() -> bookRepository.save(request.toEntity()));
    }
}
