package com.mitjul.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing(생성/수정 시각 자동 기록) 활성화 설정.
 *
 * 메인 클래스가 아니라 별도 설정 클래스에 둔 이유: @DataJpaTest 같은 슬라이스 테스트는
 * 메인 애플리케이션 클래스의 설정을 통째로 로드하지 않는다. 감사 기능을 독립된 설정으로 빼 두면
 * 테스트에서 @Import(JpaAuditingConfig.class)로 필요한 것만 정확히 가져올 수 있다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
