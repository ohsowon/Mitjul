# CLAUDE.md — 밑줄 (mitjul)

> 이 파일은 Claude Code(바이브코딩)가 프로젝트 전반을 이해하고 일관된 방식으로
> 코드를 작성하도록 돕는 가이드 문서입니다. 새 기능을 요청하기 전에 이 문서를
> 먼저 참고하고, 아래 규칙과 구조를 지켜서 작업해 주세요.

---

## 1. 프로젝트 개요

**밑줄(mitjul)**은 독서 중 마음에 드는 문장을 카메라로 촬영하면 OCR로 텍스트화하여
수집·보관하고, 다른 사람들과 공유할 수 있는 서비스입니다.

### 개발자의 학습 목표 (중요)
이 프로젝트는 아래 4가지 역량을 익히는 것이 **핵심 목적**입니다. 코드 설계와
설명은 항상 이 목표에 도움이 되는 방향으로 진행합니다.

1. **RESTful API 설계 및 Spring Boot 백엔드 개발** — REST 규약을 지킨 엔드포인트 설계
2. **프론트엔드 ↔ 백엔드 연동 방법** — CORS, DTO 계약, 인증 토큰 흐름, API 문서화
   (프론트엔드 UI 개발 자체가 목표가 아니라, "백엔드가 프론트와 어떻게 통신하는가"를 익힘)
3. **다양한 외부 API 연동** — OCR API, 도서 검색 API, 클라우드 스토리지 등
4. **Docker 사용 및 배포 경험** — 컨테이너화, docker-compose, 클라우드 배포, CI/CD

> Claude에게: 새 기능을 구현할 때 위 목표 중 어떤 것과 연결되는지 짧게 언급해 주세요.

---

## 2. 핵심 기능 (MVP)

| # | 기능 | 설명 |
|---|------|------|
| 1 | 문장 캡쳐 & OCR | 촬영 이미지 업로드 → OCR API 호출 → 텍스트 반환 → 사용자 교정 후 저장 |
| 2 | 문장 저장소 | 문장 본문, 페이지, 원본 이미지, 책 정보, 공유 여부, 수집일시 CRUD |
| 3 | 도서 정보 연동 | 제목/ISBN으로 외부 도서 API 조회 → 저자·출판사·표지 자동 입력 |
| 4 | 커뮤니티 | 공개(isPublic) 문장 피드 + 책 제목 검색으로 타인의 수집 문장 열람 |
| 5 | 인증 | 회원가입/로그인, JWT 기반 인증·인가 |

### 향후 확장 (후순위, MVP에는 미포함)
- ISBN 바코드 스캔, 문장 메모, 하이라이트 영역 지정
- 문장 리마인더, 연말 결산, 이미지 카드 공유
- 좋아요/랭킹, 팔로우, 컬렉션
- 문장 임베딩 기반 유사 문장 추천

---

## 3. 기술 스택

- **언어/프레임워크**: Java 21, Spring Boot 3.x
- **주요 의존성**: Spring Web, Spring Data JPA, Spring Security, Validation, Lombok
- **DB**: 개발 = H2 / 운영 = PostgreSQL (또는 MySQL)
- **인증**: JWT (jjwt)
- **API 문서화**: springdoc-openapi (Swagger UI)
- **외부 연동**: OCR API(예: Google Cloud Vision / CLOVA OCR), 도서 검색 API(예: 알라딘/네이버), 오브젝트 스토리지(S3 또는 로컬)
- **빌드**: Gradle
- **인프라**: Docker, docker-compose, GitHub Actions

> 스택은 학습 목표에 맞춰 선택했습니다. 특정 라이브러리를 바꾸고 싶을 땐 이 섹션을 먼저 갱신하세요.

---

## 4. 도메인 모델

핵심 엔티티와 관계:

```
User 1 ── N Quote N ── 1 Book
                │
                └── N QuoteImage (원본 촬영 이미지)
```

- **User**: id, email, password(암호화), nickname, createdAt
- **Book**: id, isbn(UNIQUE), title, author, publisher, coverUrl
  - ⚠️ 커뮤니티 검색이 핵심이므로 Book은 **ISBN 기준 전역 유니크**로 관리.
    같은 책이 사용자마다 중복 생성되지 않도록 "있으면 재사용, 없으면 생성" 로직 사용.
- **Quote**: id, content, page, imageUrl, isPublic, createdAt, user_id(FK), book_id(FK)
- **QuoteImage** (선택): 원본 이미지 여러 장 보관 시

---

## 5. REST API 설계 원칙

- 리소스는 **복수 명사**, 계층 구조로 표현: `/api/v1/quotes`, `/api/v1/books/{id}/quotes`
- HTTP 메서드로 행위 표현: `GET`(조회) `POST`(생성) `PUT/PATCH`(수정) `DELETE`(삭제)
- 응답은 일관된 래퍼(`ApiResponse<T>`) + 적절한 상태 코드(200/201/400/401/404 등)
- 에러는 `@RestControllerAdvice` 전역 예외 처리로 통일된 형식 반환
- 요청/응답에는 **엔티티가 아니라 DTO**를 사용 (Request/Response DTO 분리)
- 목록 조회는 페이징(`Pageable`) 적용

### 주요 엔드포인트 (초안)
```
POST   /api/v1/auth/signup          회원가입
POST   /api/v1/auth/login           로그인 → JWT 발급
POST   /api/v1/ocr                  이미지 업로드 → 텍스트 추출
GET    /api/v1/books?query=         도서 검색(외부 API 프록시)
GET    /api/v1/quotes               내 문장 목록
POST   /api/v1/quotes               문장 저장
GET    /api/v1/quotes/{id}          문장 상세
PATCH  /api/v1/quotes/{id}          문장 수정(공유여부 등)
DELETE /api/v1/quotes/{id}          문장 삭제
GET    /api/v1/community/quotes?book=  공개 문장 검색(커뮤니티)
```

---

## 6. 폴더 구조

레이어드 아키텍처 + 도메인별 패키지 구성:

```
mitjul/
├─ src/
│  ├─ main/
│  │  ├─ java/com/mitjul/
│  │  │  ├─ MitjulApplication.java
│  │  │  ├─ global/                  # 공통 설정·유틸
│  │  │  │  ├─ config/               # SecurityConfig, SwaggerConfig, WebConfig(CORS)
│  │  │  │  ├─ common/               # ApiResponse, BaseEntity
│  │  │  │  ├─ exception/            # 전역 예외 처리, 커스텀 예외
│  │  │  │  └─ security/             # JWT 필터·provider·유틸
│  │  │  ├─ domain/
│  │  │  │  ├─ user/
│  │  │  │  │  ├─ controller/
│  │  │  │  │  ├─ service/
│  │  │  │  │  ├─ repository/
│  │  │  │  │  ├─ entity/
│  │  │  │  │  └─ dto/
│  │  │  │  ├─ book/                 # (동일 하위 구조)
│  │  │  │  ├─ quote/
│  │  │  │  └─ community/
│  │  │  └─ infra/                   # 외부 연동
│  │  │     ├─ ocr/                  # OCR API 클라이언트
│  │  │     ├─ booksearch/           # 도서 검색 API 클라이언트
│  │  │     └─ storage/              # 이미지 스토리지(S3/로컬) 클라이언트
│  │  └─ resources/
│  │     ├─ application.yml          # 공통 설정
│  │     ├─ application-dev.yml      # 로컬(H2)
│  │     └─ application-prod.yml     # 운영(PostgreSQL) — 비밀값은 환경변수
│  └─ test/java/com/mitjul/       # 단위/통합 테스트
├─ docker/
│  ├─ Dockerfile
│  └─ docker-compose.yml             # app + db (+ 필요 시 스토리지)
├─ .github/workflows/ci.yml          # 빌드·테스트 CI
├─ build.gradle
├─ .gitignore
├─ .env.example                      # 필요한 환경변수 목록(값은 비움)
└─ README.md
```

---

## 7. 코드 컨벤션

- **DTO ↔ Entity 변환**은 각 도메인 안에서 처리(정적 팩토리 메서드 `from()`/`toEntity()` 권장)
- 비즈니스 로직은 **Service 계층**에만, Controller는 얇게 유지
- 트랜잭션 경계는 Service의 public 메서드에 `@Transactional`
- 외부 API 호출 코드는 반드시 `infra/` 아래 클라이언트로 캡슐화(도메인이 직접 호출 금지)
- 매직 넘버·문자열은 상수 또는 Enum으로
- 네이밍: 클래스 PascalCase, 메서드/변수 camelCase, 상수 UPPER_SNAKE_CASE

### 보안·비밀값
- API 키, DB 비밀번호 등은 **절대 코드/깃에 커밋 금지**. 환경변수 또는 `application-prod.yml`(gitignore) 사용
- `.env.example`에는 키 이름만 남기고 값은 비워둘 것
- 비밀번호는 반드시 해시(BCrypt) 저장

---

## 8. 프론트엔드 연동 규칙 (학습 목표 2)

- **CORS**: `WebConfig`에서 허용 origin·메서드·헤더 명시적으로 설정
- **인증 흐름**: 로그인 시 JWT 발급 → 클라이언트가 `Authorization: Bearer <token>` 헤더로 전송
- **API 계약**: 모든 엔드포인트는 Swagger(`/swagger-ui.html`)로 문서화하여 프론트가 참조
- **응답 규격 고정**: 성공·실패 모두 `ApiResponse` 형태로 통일해 프론트 파싱을 단순화
- 파일 업로드(촬영 이미지)는 `multipart/form-data`로 처리

---

## 9. Docker & 배포 (학습 목표 4)

- **Dockerfile**: 멀티스테이지 빌드(Gradle 빌드 → JRE 실행 이미지)로 경량화
- **docker-compose**: `app` + `db` 컨테이너를 함께 기동, 환경변수로 프로필 주입
- 로컬에서 `docker-compose up`만으로 전체 스택이 뜨는 상태를 목표로
- 배포: 이미지 빌드 → 클라우드(EC2 등) 배포, DB는 관리형(RDS) 권장
- **CI/CD**: GitHub Actions로 push 시 빌드·테스트 자동화, 이후 배포 단계 확장

---

## 10. 개발 순서 (권장 마일스톤)

1. 프로젝트 초기화 + 도메인 엔티티/ERD + H2 연결
2. 인증(회원가입·로그인·JWT) + Security 설정
3. Quote CRUD REST API + 전역 예외 처리 + Swagger
4. 이미지 업로드 + OCR API 연동 (`infra/ocr`)
5. 도서 검색 API 연동 (`infra/booksearch`) + Book 통합 로직
6. 커뮤니티(공개 문장 검색·피드)
7. CORS·프론트 연동 검증
8. Dockerfile + docker-compose 로 컨테이너화
9. 클라우드 배포 + CI/CD
10. 확장 기능(§2) 순차 적용

> 각 마일스톤은 독립적으로 동작·테스트 가능한 단위로 커밋합니다.

---

## 11. Claude에게 주는 작업 지침

- 새 기능은 위 폴더 구조와 컨벤션을 지켜서 구현하세요.
- 코드를 생성할 때 **왜 그렇게 설계했는지** 간단히 설명해, 학습에 도움이 되게 하세요.
- 외부 API 연동 시 실제 키가 없어도 동작을 확인할 수 있게, 인터페이스로 추상화하고
  목(mock) 구현을 함께 제공하세요.
- 한 번에 너무 많이 만들지 말고, 마일스톤 단위로 나눠 제안하세요.
- 보안·비밀값 규칙(§7)을 항상 준수하세요.
