package com.mitjul.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.mitjul.domain.user.entity.User;
import com.mitjul.global.config.JpaAuditingConfig;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원을 저장하면 id와 생성/수정 시각이 자동으로 채워진다")
    void save_assignsIdAndTimestamps() {
        User user = User.builder()
                .email("reader@mitjul.com")
                .password("hashed-pw")
                .nickname("책벌레")
                .build();

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("이메일로 회원을 조회할 수 있다")
    void findByEmail_returnsUser() {
        userRepository.save(User.builder()
                .email("reader@mitjul.com")
                .password("hashed-pw")
                .nickname("책벌레")
                .build());

        Optional<User> found = userRepository.findByEmail("reader@mitjul.com");

        assertThat(found).isPresent();
        assertThat(found.get().getNickname()).isEqualTo("책벌레");
    }

    @Test
    @DisplayName("이메일 존재 여부를 확인할 수 있다")
    void existsByEmail_works() {
        userRepository.save(User.builder()
                .email("reader@mitjul.com")
                .password("hashed-pw")
                .nickname("책벌레")
                .build());

        assertThat(userRepository.existsByEmail("reader@mitjul.com")).isTrue();
        assertThat(userRepository.existsByEmail("nobody@mitjul.com")).isFalse();
    }
}
