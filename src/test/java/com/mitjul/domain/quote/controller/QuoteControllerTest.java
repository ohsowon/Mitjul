package com.mitjul.domain.quote.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mitjul.domain.book.dto.BookRequest;
import com.mitjul.domain.quote.dto.QuoteCreateRequest;
import com.mitjul.domain.quote.dto.QuoteResponse;
import com.mitjul.domain.quote.service.QuoteService;
import com.mitjul.domain.user.dto.SignupRequest;
import com.mitjul.domain.user.dto.SignupResponse;
import com.mitjul.domain.user.service.UserService;
import com.mitjul.global.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private JwtProvider jwtProvider;

    private Long userId;
    private String token;

    private static final String BODY = """
            {"content":"깨끗한 코드는 한 가지를 제대로 한다.","page":42,"isPublic":false,
             "book":{"isbn":"9788966262472","title":"클린 코드","author":"로버트 C. 마틴"}}
            """;

    @BeforeEach
    void setUp() {
        SignupResponse s = userService.signup(new SignupRequest("owner@mitjul.com", "password123", "주인"));
        userId = s.id();
        token = jwtProvider.createAccessToken(s.id(), s.email());
    }

    private QuoteCreateRequest sampleRequest() {
        return new QuoteCreateRequest("문장 본문", 10, null, false,
                new BookRequest("9788966262472", "클린 코드", "로버트 C. 마틴", null, null));
    }

    @Test
    @DisplayName("토큰과 함께 문장을 저장하면 201 + 책 정보를 포함해 반환한다")
    void create_returns201() throws Exception {
        mockMvc.perform(post("/api/v1/quotes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.content").value("깨끗한 코드는 한 가지를 제대로 한다."))
                .andExpect(jsonPath("$.data.book.title").value("클린 코드"));
    }

    @Test
    @DisplayName("내 문장 목록을 조회하면 200 + 페이지 형태로 반환한다")
    void getMyQuotes_returns200() throws Exception {
        quoteService.create(userId, sampleRequest());

        mockMvc.perform(get("/api/v1/quotes").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].book.title").value("클린 코드"));
    }

    @Test
    @DisplayName("토큰 없이 문장을 저장하려 하면 401")
    void create_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("남의 문장을 조회하려 하면 403")
    void getOthersQuote_returns403() throws Exception {
        QuoteResponse created = quoteService.create(userId, sampleRequest());
        SignupResponse other = userService.signup(new SignupRequest("other@mitjul.com", "password123", "남"));
        String otherToken = jwtProvider.createAccessToken(other.id(), other.email());

        mockMvc.perform(get("/api/v1/quotes/" + created.id())
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden());
    }
}
