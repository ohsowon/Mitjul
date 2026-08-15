package com.mitjul.domain.community.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mitjul.domain.book.dto.BookRequest;
import com.mitjul.domain.quote.dto.QuoteCreateRequest;
import com.mitjul.domain.quote.service.QuoteService;
import com.mitjul.domain.user.dto.SignupRequest;
import com.mitjul.domain.user.dto.SignupResponse;
import com.mitjul.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private QuoteService quoteService;

    @BeforeEach
    void setUp() {
        SignupResponse s = userService.signup(new SignupRequest("comm@mitjul.com", "password123", "공유자"));
        Long userId = s.id();
        quoteService.create(userId, new QuoteCreateRequest("공개 문장입니다", 1, null, true,
                new BookRequest("9788966262472", "클린 코드", "로버트 C. 마틴", null, null)));
        quoteService.create(userId, new QuoteCreateRequest("비공개 문장입니다", 2, null, false,
                new BookRequest("9788932917245", "니코마코스 윤리학", "아리스토텔레스", null, null)));
    }

    @Test
    @DisplayName("토큰 없이도 공개 문장만 조회된다")
    void publicQuotes_noAuth_returnsOnlyPublic() throws Exception {
        mockMvc.perform(get("/api/v1/community/quotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].content").value("공개 문장입니다"))
                .andExpect(jsonPath("$.data.content[0].nickname").value("공유자"));
    }

    @Test
    @DisplayName("책 제목으로 공개 문장을 검색한다")
    void publicQuotes_searchByBookTitle() throws Exception {
        mockMvc.perform(get("/api/v1/community/quotes").param("book", "클린"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].bookTitle").value("클린 코드"));
    }
}