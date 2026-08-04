package com.mitjul.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mitjul.domain.user.dto.SignupRequest;
import com.mitjul.domain.user.dto.SignupResponse;
import com.mitjul.domain.user.service.UserService;
import com.mitjul.global.security.JwtProvider;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("유효한 토큰으로 내 정보를 조회하면 200과 회원 정보를 반환한다")
    void me_withValidToken_returns200() throws Exception {
        SignupResponse signup = userService.signup(
                new SignupRequest("me@mitjul.com", "password123", "미투"));
        String token = jwtProvider.createAccessToken(signup.id(), signup.email());

        mockMvc.perform(get("/api/v1/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("me@mitjul.com"))
                .andExpect(jsonPath("$.data.nickname").value("미투"));
    }

    @Test
    @DisplayName("토큰 없이 보호된 엔드포인트에 접근하면 401을 반환한다")
    void me_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("잘못된 토큰이면 401을 반환한다")
    void me_withInvalidToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me").header("Authorization", "Bearer not-a-real-token"))
                .andExpect(status().isUnauthorized());
    }
}
