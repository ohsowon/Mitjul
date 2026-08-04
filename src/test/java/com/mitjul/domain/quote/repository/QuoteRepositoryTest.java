package com.mitjul.domain.quote.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.mitjul.domain.book.entity.Book;
import com.mitjul.domain.book.repository.BookRepository;
import com.mitjul.domain.quote.entity.Quote;
import com.mitjul.domain.user.entity.User;
import com.mitjul.domain.user.repository.UserRepository;
import com.mitjul.global.config.JpaAuditingConfig;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class QuoteRepositoryTest {

    @Autowired
    private QuoteRepository quoteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        // Quote는 User·Book FK가 not null이라, 먼저 두 엔티티를 저장해 둔다.
        user = userRepository.save(User.builder()
                .email("reader@mitjul.com").password("pw").nickname("책벌레").build());
        book = bookRepository.save(Book.builder()
                .isbn("9788966262472").title("클린 코드").build());
    }

    @Test
    @DisplayName("문장을 저장하면 작성자·책과 연결되고 생성 시각이 채워진다")
    void save_linksUserAndBook() {
        // when
        Quote saved = quoteRepository.save(Quote.builder()
                .content("깨끗한 코드는 한 가지를 제대로 한다.")
                .page(42)
                .isPublic(false)
                .user(user)
                .book(book)
                .build());

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());   // user_id FK로 연결됨
        assertThat(saved.getBook().getTitle()).isEqualTo("클린 코드");  // book_id FK로 연결됨
    }

    @Test
    @DisplayName("사용자별로 문장 목록을 조회할 수 있다")
    void findByUserId_returnsUsersQuotes() {
        // given
        quoteRepository.save(quoteOf("문장 1", false));
        quoteRepository.save(quoteOf("문장 2", false));

        // when
        List<Quote> quotes = quoteRepository.findByUserId(user.getId());

        // then
        assertThat(quotes).hasSize(2);
    }

    @Test
    @DisplayName("공개 문장만 조회할 수 있다 (커뮤니티 피드)")
    void findByIsPublicTrue_returnsOnlyPublic() {
        // given
        quoteRepository.save(quoteOf("공개 문장", true));
        quoteRepository.save(quoteOf("비공개 문장", false));

        // when
        List<Quote> publicQuotes = quoteRepository.findByIsPublicTrue();

        // then
        assertThat(publicQuotes).hasSize(1);
        assertThat(publicQuotes.get(0).getContent()).isEqualTo("공개 문장");
    }

    private Quote quoteOf(String content, boolean isPublic) {
        return Quote.builder()
                .content(content).isPublic(isPublic).user(user).book(book).build();
    }
}
