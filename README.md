# 밑줄 (mitjul)

독서 중 마음에 드는 문장을 카메라로 촬영하면 OCR로 텍스트화하여 수집·보관하고,
다른 사람들과 공유할 수 있는 서비스입니다.

> 프로젝트 규칙·설계 원칙은 [CLAUDE.md](CLAUDE.md)를 참고하세요.

---

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| 언어 / 프레임워크 | Java 21, Spring Boot 3.5.16 |
| 빌드 | Gradle 9.5.1 (Wrapper) |
| DB | 개발 H2(인메모리) / 운영 PostgreSQL |
| 인증 | JWT (예정) |
| API 문서 | springdoc-openapi (예정) |
| 인프라 | Docker, docker-compose, GitHub Actions (예정) |

---

## 실행 방법

### 로컬 실행 (dev 프로필, H2)

```bash
./gradlew bootRun
```

Windows PowerShell에서는:

```bash
.\gradlew.bat bootRun
```

프로필을 지정하지 않으면 `dev`로 뜹니다 (`application.yml`의 `spring.profiles.default`).

| 항목 | 주소 |
|---|---|
| 애플리케이션 | http://localhost:8080 |
| H2 콘솔 | http://localhost:8080/h2-console |

H2 콘솔 접속 정보 — JDBC URL: `jdbc:h2:mem:mitjul`, User: `sa`, Password: (없음)

### 빌드 / 테스트

```bash
./gradlew build
```

### 운영 프로필 실행

`.env.example`을 참고해 환경변수를 설정한 뒤 실행합니다. 비밀값은 코드나 git에 두지 않습니다.

```bash
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun
```

---

## 프로젝트 구조

```
src/main/java/com/mitjul/
├─ MitjulApplication.java
├─ global/          # 공통 설정·유틸
│  ├─ config/       # SecurityConfig, SwaggerConfig, WebConfig(CORS)
│  ├─ common/       # ApiResponse, BaseEntity
│  ├─ exception/    # 전역 예외 처리
│  └─ security/     # JWT 필터·provider
├─ domain/          # 도메인별 controller/service/repository/entity/dto
│  ├─ user/
│  ├─ book/
│  ├─ quote/
│  └─ community/
└─ infra/           # 외부 연동 클라이언트
   ├─ ocr/
   ├─ booksearch/
   └─ storage/
```

---

## 진행 상황

CLAUDE.md §10의 마일스톤 기준입니다.

| # | 마일스톤 | 상태 |
|---|---|---|
| 1 | 프로젝트 초기화 + 도메인 엔티티/ERD + H2 연결 | 🔨 초기화·H2 완료 / 엔티티 진행 예정 |
| 2 | 인증(회원가입·로그인·JWT) + Security 설정 | ⬜ |
| 3 | Quote CRUD REST API + 전역 예외 처리 + Swagger | ⬜ |
| 4 | 이미지 업로드 + OCR API 연동 | ⬜ |
| 5 | 도서 검색 API 연동 + Book 통합 로직 | ⬜ |
| 6 | 커뮤니티(공개 문장 검색·피드) | ⬜ |
| 7 | CORS·프론트 연동 검증 | ⬜ |
| 8 | Dockerfile + docker-compose 컨테이너화 | ⬜ |
| 9 | 클라우드 배포 + CI/CD | ⬜ |
| 10 | 확장 기능 | ⬜ |

`spring-boot-starter-security`, `springdoc-openapi`, `jjwt` 의존성은 해당 마일스톤에서 추가합니다
(`build.gradle` 하단 주석 참고).
