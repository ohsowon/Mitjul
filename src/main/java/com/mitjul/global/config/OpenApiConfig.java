package com.mitjul.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger(OpenAPI) 문서 설정.
 *
 * API 기본 정보와 함께 JWT Bearer 인증 스키마를 등록한다. 이렇게 해야 Swagger UI의
 * "Authorize" 버튼에 토큰을 넣으면, 보호된 API 호출에 Authorization: Bearer 헤더가 자동으로 붙는다.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI mitjulOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("밑줄(mitjul) API")
                        .version("v1")
                        .description("독서 중 마음에 드는 문장을 촬영·수집하고 공유하는 서비스의 REST API"))
                // 모든 엔드포인트에 기본으로 Bearer 인증을 적용(문서상 표시). 공개 API는 토큰 없이도 호출된다.
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
