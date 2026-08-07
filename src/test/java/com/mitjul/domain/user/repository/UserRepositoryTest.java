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

/**
 * UserRepository 슬라이스 테스트.
 *
 * 여기 쓴 @DataJpaTest는 JPA 관련 빈(리포지토리, EntityManager)만 올리는 "얇은" 테스트다.
 * 웹 계층 등은 로드하지 않아 빠르고, 각 테스트를 트랜잭션으로 감싼 뒤 끝나면 롤백해 서로 격리된다.
 *
 * 여기서 @Import(JpaAuditingConfig.class)가 필요한 이유: 이 슬라이스는 메인 클래스 설정을
 * 통째로 안 불러오므로 감사 설정이 빠진다. 그러면 createdAt(not null)이 채워지지 않아 저장이 실패한다.
 * 그래서 감사 설정만 콕 집어 가져온다.
 */
@DataJpaTest
@Import(JpaAuditingConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원을 저장하면 id와 생성/수정 시각이 자동으로 채워진다")
    void save_assignsIdAndTimestamps() {
        // given
        User user = User.builder()
                .email("reader@mitjul.com")
                .password("hashed-pw")
                .nickname("책벌레")
                .build();

        // when
        User saved = userRepository.save(user);

        // then
        assertThat(saved.getId()).isNotNull();          // IDENTITY로 PK 자동 생성
        assertThat(saved.getCreatedAt()).isNotNull();   // JPA Auditing 동작 확인
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("이메일로 회원을 조회할 수 있다")
    void findByEmail_returnsUser() {
        // given
        userRepository.save(User.builder()
                .email("reader@mitjul.com")
                .password("hashed-pw")
                .nickname("책벌레")
                .build());

        // when
        Optional<User> found = userRepository.findByEmail("reader@mitjul.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getNickname()).isEqualTo("책벌레");
    }

    @Test
    @DisplayName("이메일 존재 여부를 확인할 수 있다")
    void existsByEmail_works() {
        // given
        userRepository.save(User.builder()
                .email("reader@mitjul.com")
                .password("hashed-pw")
                .nickname("책벌레")
                .build());

        // then
        assertThat(userRepository.existsByEmail("reader@mitjul.com")).isTrue();
        assertThat(userRepository.existsByEmail("nobody@mitjul.com")).isFalse();
    }
}
