package com.mitjul.domain.ocr.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mitjul.domain.user.dto.SignupResponse;
import com.mitjul.domain.user.dto.SignupRequest;
import com.mitjul.domain.user.service.UserService;
import com.mitjul.global.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "storage.local.path=build/test-uploads")
@AutoConfigureMockMvc
@Transactional
class OcrControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;

    private String token;

    @BeforeEach
    void setUp() {
        SignupResponse s = userService.signup(new SignupRequest("ocr@mitjul.com", "password123", "오시알"));
        token = jwtProvider.createAccessToken(s.id(), s.email());
    }

    @Test
    @DisplayName("이미지를 올리면 200 + 추출 텍스트와 imageUrl을 반환한다")
    void extract_returns200() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "page.jpg", "image/jpeg", "dummy-image-bytes".getBytes());

        mockMvc.perform(multipart("/api/v1/ocr").file(image)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.text").isNotEmpty())
                .andExpect(jsonPath("$.data.imageUrl").exists());
    }

    @Test
    @DisplayName("토큰 없이 올리면 401")
    void extract_withoutToken_returns401() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "page.jpg", "image/jpeg", "dummy-image-bytes".getBytes());

        mockMvc.perform(multipart("/api/v1/ocr").file(image))
                .andExpect(status().isUnauthorized());
    }
}