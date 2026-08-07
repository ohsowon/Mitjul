package com.mitjul.domain.user.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * 실제 내장 톰캣을 띄워(RANDOM_PORT) HTTP 요청을 보내는 통합 테스트.
 *
 * 왜 이게 필요한가: MockMvc 슬라이스는 서블릿의 /error forward를 실제로 재현하지 않아,
 * "에러가 403으로 둔갑하던" 보안 설정 버그를 놓쳤다. 이 테스트는 진짜 서버를 거치므로 그 부류의
 * 버그를 잡아낸다(회귀 방지). /error를 permitAll에서 빼면 두 번째 테스트가 400 대신 403으로 깨진다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpEntity<String> jsonBody(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }

    @Test
    @DisplayName("유효한 요청이면 실제 서버에서 201을 반환한다")
    void signup_valid_returns201() {
        ResponseEntity<String> res = restTemplate.postForEntity("/api/v1/auth/signup",
                jsonBody("{\"email\":\"integration@mitjul.com\",\"password\":\"password123\",\"nickname\":\"통합\"}"),
                String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("유효성 위반은 403이 아니라 400을 반환한다 (/error 경로가 열려 있어야 함)")
    void signup_invalid_returns400_notForbidden() {
        ResponseEntity<String> res = restTemplate.postForEntity("/api/v1/auth/signup",
                jsonBody("{\"email\":\"bad\",\"password\":\"short\",\"nickname\":\"x\"}"),
                String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
