package com.mitjul.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("유효한 요청이면 201과 함께 비밀번호를 뺀 회원 정보를 반환한다")
    void signup_valid_returns201() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"reader@mitjul.com","password":"password123","nickname":"책벌레"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("reader@mitjul.com"))
                .andExpect(jsonPath("$.data.nickname").value("책벌레"))
                .andExpect(jsonPath("$.data.password").doesNotExist()); // 비밀번호는 응답에 없어야 한다
    }

    @Test
    @DisplayName("이메일 형식이 잘못되거나 비밀번호가 짧으면 400을 반환한다")
    void signup_invalid_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"not-an-email","password":"short","nickname":"책벌레"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
