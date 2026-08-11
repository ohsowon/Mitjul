package com.mitjul.domain.quote.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitjul.domain.book.dto.BookRequest;
import com.mitjul.domain.quote.dto.QuoteCreateRequest;
import com.mitjul.domain.quote.dto.QuoteResponse;
import com.mitjul.domain.quote.dto.QuoteUpdateRequest;
import com.mitjul.domain.user.dto.SignupRequest;
import com.mitjul.domain.user.service.UserService;
import com.mitjul.global.exception.BusinessException;
import com.mitjul.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class QuoteServiceTest {
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private UserService userService;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = userService.signup(new SignupRequest("owner@mitjul.com", "password123", "주인")).id();
    }

    private QuoteCreateRequest sampleRequest(boolean isPublic) {
        return new QuoteCreateRequest(
                "깨끗한 코드는 한 가지를 제대로 한다.", 42, null, isPublic,
                new BookRequest("9788966262472", "클린 코드", "로버트 C. 마틴", "인사이트", null));
    }

    @Test
    @DisplayName("문장을 저장하면 책 정보와 함께 응답이 반환된다")
    void create_success() {
        QuoteResponse res = quoteService.create(userId, sampleRequest(false));

        assertThat(res.id()).isNotNull();
        assertThat(res.content()).isEqualTo("깨끗한 코드는 한 가지를 제대로 한다.");
        assertThat(res.book().title()).isEqualTo("클린 코드");
        assertThat(res.isPublic()).isFalse();
    }

    @Test
    @DisplayName("내 문장 목록을 페이징으로 조회한다")
    void getMyQuotes_paged() {
        quoteService.create(userId, sampleRequest(false));
        quoteService.create(userId, sampleRequest(true));

        Page<QuoteResponse> page = quoteService.getMyQuotes(userId, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("PATCH는 값이 있는 필드만 바꾼다 (isPublic만 토글, 내용은 유지)")
    void update_partial_togglesPublicOnly() {
        QuoteResponse created = quoteService.create(userId, sampleRequest(false));

        QuoteResponse updated = quoteService.update(userId, created.id(),
                new QuoteUpdateRequest(null, null, true));

        assertThat(updated.isPublic()).isTrue();
        assertThat(updated.content()).isEqualTo(created.content());
    }

    @Test
    @DisplayName("삭제 후 조회하면 QUOTE_NOT_FOUND")
    void delete_thenNotFound() {
        QuoteResponse created = quoteService.create(userId, sampleRequest(false));

        quoteService.delete(userId, created.id());

        assertThatThrownBy(() -> quoteService.getMyQuote(userId, created.id()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.QUOTE_NOT_FOUND);
    }

    @Test
    @DisplayName("남의 문장에 접근하면 ACCESS_DENIED")
    void otherUsersQuote_forbidden() {
        QuoteResponse created = quoteService.create(userId, sampleRequest(false));
        Long otherId = userService.signup(new SignupRequest("other@mitjul.com", "password123", "남")).id();

        assertThatThrownBy(() -> quoteService.getMyQuote(otherId, created.id()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
    }
}
