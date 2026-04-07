# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# 빌드
./gradlew build

# 실행 (local 프로파일)
./gradlew bootRun

# 테스트 전체
./gradlew test

# 단일 테스트 클래스
./gradlew test --tests "com.jobassistant.jabackend.SomeTest"

# 단일 테스트 메서드
./gradlew test --tests "com.jobassistant.jabackend.SomeTest.methodName"
```

## 로컬 실행 전 필수 준비

1. `application-local.yml` 생성:
   ```bash
   cp src/main/resources/application-local.example.yml src/main/resources/application-local.yml
   ```
2. `application-local.yml`에 Google OAuth2 `client-id` / `client-secret` 입력
3. [ja-infra](https://github.com/job-assistant/ja-infra)로 PostgreSQL(15433), Redis(16379) 실행 필요
4. 서버 포트: `8080` / Swagger: `http://localhost:8080/swagger-ui.html`

스키마는 DDL-auto `validate` 모드 → **Hibernate가 스키마를 생성하지 않으므로** DB 스키마를 먼저 직접 적용해야 함.

## 패키지 구조

```
com.jobassistant.jabackend/
├── core/                  # 공통 프레임워크 (변경 빈도 낮음)
│   ├── base/              # 추상 클래스, 공통 VO/DTO, BaseEntity
│   ├── security/          # SecurityConfig, SecurityProperties (CORS, 공개 URI)
│   ├── exception/         # GlobalExceptionHandler, ErrorCode, BizException
│   ├── jpa/               # JPA Auditing 설정
│   ├── mapStruct/         # MapStruct 전역 설정
│   ├── swagger/           # Swagger/OpenAPI 설정
│   └── utils/             # CookieUtils, IpUtils, SecurityUtils 등
└── app/                   # 비즈니스 도메인 (domain/ 패키지 없음, 모두 app/ 아래)
    ├── user/              # 사용자 도메인
    │   └── entity/        # User.java
    └── company/           # 회사 도메인 (복수형 아님 — companies X, company O)
        ├── entity/        # Company.java
        └── constant/      # Status.java (enum 별도 분리)
```

**도메인 추가 시 반드시 `app/{단수형도메인}/` 하위에 생성.** `domain/` 패키지나 복수형 경로(`companies/`)는 사용하지 않음.

## 아키텍처 핵심 규칙

### 레이어 구조
- **Controller** → `AbstractController` 상속, 응답은 반드시 `makeResponse()` / `makeCreatedResponse()` 사용
- **Service** → `AbstractService` 상속
- **Entity** → `BaseEntity` 상속 (`created_at`, `updated_at` JPA Auditing 자동 처리)

### 공통 응답 포맷
모든 API 응답은 `BizRespVo<T>`로 래핑됨:
```json
{ "statusCode": "OK", "resultCode": 200, "message": "...", "body": {}, "requestId": "uuid", "timestamp": "..." }
```
리스트 응답은 `GenericListVo<T>`로 한 번 더 감싸서 `BizRespVo<GenericListVo<T>>` 형태로 반환.

### Entity 작성 규칙
- `@NoArgsConstructor(access = PROTECTED)` + `@Builder` 패턴 사용
- 상태 변경은 엔티티 내 `update*()` 메서드로 캡슐화
- DB 컬럼명과 Java 필드명이 다를 경우 `@Column(name = "...")`으로 명시

### 외부 연동
- **ja-agent** (FastAPI, `http://localhost:8000`): AI 기능(임베딩, 질문 생성, STT) 담당. `job.agent.base-url`로 설정
- **Redis**: Refresh Token 및 연습 제한 TTL 관리
- **JWT**: `job.jwt.secret` / `access-token-ttl` / `refresh-token-ttl` 설정

### Security
- Stateless (JWT 기반), 세션 미사용
- 공개 URI 목록은 `application-local.yml`의 `security.public-uris`로 관리 (Swagger, OAuth2 엔드포인트 포함)
- CORS 허용 Origin도 동일 파일의 `security.cors.allowed-origins`로 관리
