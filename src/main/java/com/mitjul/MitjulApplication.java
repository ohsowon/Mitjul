package com.mitjul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 밑줄(mitjul) 애플리케이션 진입점.
 *
 * <p>{@code @EnableJpaAuditing}은 BaseEntity의 createdAt/updatedAt 자동 기록을 위해 켜 둔다.
 * (CLAUDE.md §4 - 모든 엔티티가 수집일시를 가진다)
 */
@EnableJpaAuditing
@SpringBootApplication
public class MitjulApplication {

	public static void main(String[] args) {
		SpringApplication.run(MitjulApplication.class, args);
	}
}
