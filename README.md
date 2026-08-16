# 밑줄 (mitjul)

> 독서 중 마음에 드는 문장을 촬영하면 OCR로 텍스트화해 수집·보관하고,
> 다른 사람들과 공유하는 문장 아카이빙 서비스

이미지 속 문장을 텍스트로 추출해 저장하고, 도서 정보와 함께 관리하며,
공개한 문장은 커뮤니티에서 다른 사람들과 나눌 수 있습니다.

---

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| 언어 / 프레임워크 | Java 21, Spring Boot 3.5.16 |
| 빌드 | Gradle (Wrapper) |
| 데이터베이스 | H2 (개발) / PostgreSQL 16 (운영) |
| ORM | Spring Data JPA (Hibernate) |
| 인증 | Spring Security + JWT (jjwt 0.12.6) |
| API 문서 | springdoc-openapi 2.8.17 (Swagger UI) |
| 인프라 | Docker, Docker Compose, GitHub Actions |
| 배포 | AWS EC2 + RDS (PostgreSQL) |

---

## 주요 기능

- **인증** — 회원가입 / 로그인, JWT 기반 stateless 인증
- **문장 저장소** — 문장 본문·페이지·이미지·공개 여부 CRUD (본인 소유만 접근)
- **OCR** — 촬영 이미지 업로드 후 텍스트 자동 추출
- **도서 연동** — 제목·ISBN 기반 도서 검색, ISBN 기준 전역 유니크 관리
- **커뮤니티** — 공개 문장 피드 열람 및 책 제목 검색

> 외부 API(OCR·도서 검색·스토리지)는 인터페이스로 추상화되어 있으며,
> 실제 API 키 없이도 동작하는 Mock 구현을 함께 제공합니다.

---

## 아키텍처

레이어드 아키텍처(Controller → Service → Repository)에 도메인별 패키지를 결합했습니다.

```
src/main/java/com/mitjul/
├─ MitjulApplication.java
├─ global/              # 공통 관심사
│  ├─ config/             # Security, OpenAPI, Web(CORS), JPA Auditing
│  ├─ common/             # ApiResponse, BaseEntity
│  ├─ exception/          # 전역 예외 처리, ErrorCode
│  └─ security/           # JWT 필터·provider
├─ domain/              # 도메인별 controller/service/repository/entity/dto
│  ├─ user/               # 회원가입·로그인·내 정보
│  ├─ book/               # 도서 검색·find-or-create
│  ├─ quote/              # 문장 CRUD
│  ├─ community/          # 공개 문장 피드·검색
│  └─ ocr/                # OCR 텍스트 추출
└─ infra/               # 외부 연동 클라이언트 (인터페이스 + Mock 구현)
   ├─ ocr/
   ├─ booksearch/
   └─ storage/
```

### 도메인 모델

```
User 1 ── N Quote N ── 1 Book
```

- **User** — 이메일·비밀번호(BCrypt)·닉네임
- **Book** — ISBN 기준 전역 유니크. 있으면 재사용, 없으면 생성
- **Quote** — 문장 본문·페이지·이미지·공개 여부, User·Book과 연관

---

## API 엔드포인트

| 메서드 | 경로 | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/v1/auth/signup` | 회원가입 | - |
| POST | `/api/v1/auth/login` | 로그인 (JWT 발급) | - |
| GET | `/api/v1/users/me` | 내 정보 조회 | ✔ |
| POST | `/api/v1/ocr` | 이미지 업로드 → 텍스트 추출 | ✔ |
| GET | `/api/v1/books?query=` | 도서 검색 | ✔ |
| POST | `/api/v1/quotes` | 문장 저장 | ✔ |
| GET | `/api/v1/quotes` | 내 문장 목록 (페이징) | ✔ |
| GET | `/api/v1/quotes/{id}` | 문장 상세 | ✔ |
| PATCH | `/api/v1/quotes/{id}` | 문장 수정 (공개 여부 등) | ✔ |
| DELETE | `/api/v1/quotes/{id}` | 문장 삭제 | ✔ |
| GET | `/api/v1/community/quotes?book=` | 공개 문장 검색·피드 | - |

인증이 필요한 요청은 `Authorization: Bearer <token>` 헤더를 사용합니다.
모든 응답은 `ApiResponse<T>` 래퍼로 통일되어 있습니다.

전체 명세는 실행 후 Swagger UI에서 확인할 수 있습니다.

```
http://localhost:8080/swagger-ui.html
```

---

## 시작하기

### 요구 사항

- JDK 21
- (선택) Docker, Docker Compose

### 로컬 실행 (H2 인메모리)

```bash
./gradlew bootRun
```

Windows PowerShell:

```bash
.\gradlew.bat bootRun
```

별도 프로필을 지정하지 않으면 `dev` 프로필로 실행됩니다.

| 항목 | 주소 |
|---|---|
| 애플리케이션 | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| H2 콘솔 | http://localhost:8080/h2-console |

H2 콘솔 — JDBC URL: `jdbc:h2:mem:mitjul`, User: `sa`, Password: (없음)

### 빌드 / 테스트

```bash
./gradlew build
```

### Docker Compose (앱 + PostgreSQL)

```bash
docker-compose up --build
```

---

## 환경 변수

필요한 환경 변수 목록은 [`.env.example`](.env.example)을 참고하세요.
값을 채워 `.env`로 복사해 사용하며, 비밀값은 커밋하지 않습니다.

```bash
cp .env.example .env
```

---

## CI / CD

- **CI** — `main` 브랜치 push·PR 시 GitHub Actions에서 빌드·테스트 자동 실행
- **CD** — `main` 브랜치 push 시 빌드 산출물을 AWS EC2에 전송, RDS(PostgreSQL)와 연결된 컨테이너로 자동 재배포

---

## 라이선스

이 프로젝트는 학습 목적으로 제작되었습니다.
