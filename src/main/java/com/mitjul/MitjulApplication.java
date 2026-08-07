package com.mitjul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 밑줄(mitjul) 애플리케이션 진입점.
 *
 * JPA Auditing(createdAt/updatedAt 자동 기록)은 global/config/JpaAuditingConfig에서 켠다.
 */
@SpringBootApplication
public class MitjulApplication {

	public static void main(String[] args) {
		SpringApplication.run(MitjulApplication.class, args);
	}
}
