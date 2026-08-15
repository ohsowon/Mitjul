package com.mitjul.infra.booksearch;

import java.util.List;

public interface BookSearchClient {

    List<BookSearchResult> search(String query);
}
