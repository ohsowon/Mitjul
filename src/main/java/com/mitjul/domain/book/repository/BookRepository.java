package com.mitjul.domain.book.repository;

import com.mitjul.domain.book.entity.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 도서 저장소.
 *
 * findByIsbn(String)은 "있으면 재사용, 없으면 생성"(CLAUDE.md §4) 로직의 핵심 조회다.
 * 실제 재사용/생성 판단은 Service 계층(마일스톤 5, 도서 검색 연동 시)에서 이 메서드를 이용해 수행한다.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /** ISBN으로 도서 조회 → select ... from books where isbn = ? */
    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);
}
