package com.mitjul.domain.book.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitjul.domain.book.entity.Book;
import com.mitjul.global.config.JpaAuditingConfig;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("ISBN으로 도서를 조회할 수 있다")
    void findByIsbn_returnsBook() {
        bookRepository.save(Book.builder()
                .isbn("9788966262472")
                .title("클린 코드")
                .author("로버트 C. 마틴")
                .publisher("인사이트")
                .build());

        Optional<Book> found = bookRepository.findByIsbn("9788966262472");

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("클린 코드");
    }

    @Test
    @DisplayName("같은 ISBN으로 두 번 저장하면 유니크 제약에 걸린다 (전역 유니크 보장)")
    void duplicateIsbn_violatesUniqueConstraint() {
        bookRepository.saveAndFlush(Book.builder()
                .isbn("9788966262472")
                .title("클린 코드")
                .build());

        Book duplicate = Book.builder()
                .isbn("9788966262472")
                .title("제목이 달라도 ISBN이 같으면 불가")
                .build();

        assertThatThrownBy(() -> bookRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("저장하면 id와 생성 시각이 자동으로 채워진다")
    void save_assignsIdAndCreatedAt() {
        Book saved = bookRepository.save(Book.builder()
                .isbn("9788966262472")
                .title("클린 코드")
                .build());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
