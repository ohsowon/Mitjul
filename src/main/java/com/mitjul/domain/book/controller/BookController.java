package com.mitjul.domain.book.controller;

import com.mitjul.domain.book.dto.BookSearchResponse;
import com.mitjul.domain.book.service.BookService;
import com.mitjul.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookSearchResponse>>> search(@RequestParam String query) {
        return ResponseEntity.ok(ApiResponse.success(bookService.search(query)));
    }
}
