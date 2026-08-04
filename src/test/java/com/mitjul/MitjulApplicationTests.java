package com.mitjul;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 스프링 컨텍스트가 정상적으로 로딩되는지 확인하는 스모크 테스트.
 * 빈 설정이나 DataSource 설정이 깨지면 여기서 가장 먼저 실패한다.
 */
@SpringBootTest
class MitjulApplicationTests {

	@Test
	void contextLoads() {
	}
}
