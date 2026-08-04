package com.mitjul.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitjul.domain.user.dto.SignupRequest;
import com.mitjul.domain.user.dto.SignupResponse;
import com.mitjul.domain.user.entity.User;
import com.mitjul.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional // 각 테스트 후 롤백해 서로 격리
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입에 성공하면 id가 부여된 응답을 돌려준다")
    void signup_success() {
        SignupResponse response = userService.signup(
                new SignupRequest("new@mitjul.com", "password123", "신규회원"));

        assertThat(response.id()).isNotNull();
        assertThat(response.email()).isEqualTo("new@mitjul.com");
        assertThat(response.nickname()).isEqualTo("신규회원");
    }

    @Test
    @DisplayName("비밀번호는 평문이 아니라 BCrypt 해시로 저장된다")
    void signup_hashesPassword() {
        userService.signup(new SignupRequest("hash@mitjul.com", "password123", "해시"));

        User saved = userRepository.findByEmail("hash@mitjul.com").orElseThrow();
        assertThat(saved.getPassword()).isNotEqualTo("password123");             // 평문이 아님
        assertThat(passwordEncoder.matches("password123", saved.getPassword()))  // 대조는 성공
                .isTrue();
    }

    @Test
    @DisplayName("이미 가입된 이메일이면 예외가 발생한다")
    void signup_duplicateEmail_throws() {
        userRepository.save(User.builder()
                .email("dup@mitjul.com").password("x").nickname("기존").build());

        assertThatThrownBy(() -> userService.signup(
                new SignupRequest("dup@mitjul.com", "password123", "중복시도")))
                .isInstanceOf(IllegalStateException.class);
    }
}
