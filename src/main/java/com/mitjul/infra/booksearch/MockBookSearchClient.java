package com.mitjul.infra.booksearch;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockBookSearchClient implements BookSearchClient {

    @Override
    public List<BookSearchResult> search(String query) {
        return List.of(
                new BookSearchResult("9788966262472", query + " 관련 도서 (예시 1)", "로버트 C. 마틴", "민음사", null),
                new BookSearchResult("9788932917245", query + " 관련 도서 (예시 2)", "아리스토텔레스", "창비", null)
        );
    }
}
