package com.mitjul.domain.book.service;

import com.mitjul.domain.book.dto.BookRequest;
import com.mitjul.domain.book.dto.BookSearchResponse;
import com.mitjul.domain.book.entity.Book;
import com.mitjul.domain.book.repository.BookRepository;
import com.mitjul.infra.booksearch.BookSearchClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookSearchClient bookSearchClient;

    @Transactional
    public Book findOrCreate(BookRequest request) {
        return bookRepository.findByIsbn(request.isbn())
                .orElseGet(() -> bookRepository.save(request.toEntity()));
    }

    public List<BookSearchResponse> search(String query) {
        return bookSearchClient.search(query).stream()
                .map(BookSearchResponse::from)
                .toList();
    }
}