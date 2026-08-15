package com.mitjul.domain.book.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;

    private String token;

    @BeforeEach
    void setUp() {
        SignupResponse s = userService.signup(new SignupRequest("book@mitjul.com", "password123", "북서치"));
        token = jwtProvider.createAccessToken(s.id(), s.email());
    }

    @Test
    @DisplayName("검색어로 조회하면 200 + 도서 목록을 반환한다")
    void search_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/books").param("query", "클린코드")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].isbn").exists())
                .andExpect(jsonPath("$.data[0].title").exists());
    }

    @Test
    @DisplayName("토큰 없이 검색하면 401")
    void search_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/books").param("query", "클린코드"))
                .andExpect(status().isUnauthorized());
    }
}