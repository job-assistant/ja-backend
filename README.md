# ja-backend

> **Job Assistant** 프로젝트의 백엔드 API 서버입니다.

## 프로젝트 소개

Job Assistant는 취업 준비생을 위한 AI 기반 면접 준비 플랫폼입니다. 지원 회사별로 이력서, 회사소개, 모집공고(JD)를 하나의 세트로 관리하고, 이를 RAG(Retrieval-Augmented Generation) 컨텍스트로 활용하여 맞춤형 면접 질문 생성, 답변 피드백, 모의 면접을 통합 제공합니다.

`ja-backend`는 회사 세트 관리, 질문 CRUD, 모의 면접 세션, Google OAuth2 인증 등 핵심 비즈니스 로직을 담당하는 Spring Boot REST API 서버입니다. AI 관련 기능(임베딩, 질문 생성, STT 등)은 `ja-agent`와 API로 통신합니다.

## 관련 레포지토리

| 레포 | 설명 |
|------|------|
| [ja-infra](https://github.com/job-assistant/ja-infra) | Docker Compose, PostgreSQL+pgvector, Redis |
| [ja-agent](https://github.com/job-assistant/ja-agent) | FastAPI + LangGraph AI 에이전트 |
| [ja-frontend](https://github.com/job-assistant/ja-frontend) | React + TypeScript 프론트엔드 |

## 기술 스택

- **Java 21** + **Spring Boot 4.0.5**
- **Spring Security** + **Google OAuth2** + **JWT**
- **Spring Data JPA** + **PostgreSQL** (HikariCP)
- **Spring Data Redis** — Refresh Token, 연습 제한 TTL 관리
- **MapStruct** — DTO 매핑
- **springdoc-openapi 3.x** — Swagger UI

## 주요 기능

- **Google OAuth2 로그인** — Spring Security 기반 소셜 로그인, JWT Access/Refresh Token 발급
- **회사 세트 관리** — 지원 회사별 이력서(PDF), 회사소개, 모집공고(JD) 통합 관리
- **질문 관리** — 카테고리별(인성/컬처핏/프로젝트/역질문) 질문/답변 CRUD, O/X 마킹
- **모의 면접** — 면접 세션 관리, STT 답변 저장, 결과 리포트
- **AI 연동** — ja-agent API 호출로 임베딩, 질문 생성, 답변 평가, 키워드 추출 처리

## 프로젝트 구조

```
src/main/java/com/jobassistant/jabackend/
├── JaBackendApplication.java
├── core/                              # 공통 프레임워크 레이어
│   ├── base/
│   │   ├── component/
│   │   │   ├── AbstractController.java
│   │   │   └── AbstractService.java
│   │   ├── dto/
│   │   │   ├── BaseReqDto.java
│   │   │   └── BaseRespDto.java
│   │   ├── entity/
│   │   │   └── BaseEntity.java        # JPA Auditing (createdAt, updatedAt)
│   │   ├── exception/
│   │   │   └── BizException.java
│   │   └── vo/
│   │       ├── BizRespVo.java         # 공통 API 응답 래퍼
│   │       ├── BizErrorVo.java
│   │       ├── BizPageableDto.java
│   │       ├── GenericListVo.java
│   │       ├── Pagination.java
│   │       └── ReqContextVo.java
│   ├── config/
│   │   └── WebMvcConfig.java
│   ├── constants/
│   │   ├── MessageConstants.java
│   │   └── RequestAttributeKey.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── constants/
│   │       └── ErrorCode.java
│   ├── interceptor/
│   │   └── RequestContextInterceptor.java
│   ├── jpa/
│   │   ├── auditorAware/
│   │   │   └── AuditorAwareImpl.java
│   │   └── config/
│   │       ├── JpaAuditConfig.java
│   │       └── JpaConfig.java
│   ├── mapStruct/
│   │   └── MapStructConfig.java
│   ├── security/
│   │   ├── SecurityProperties.java
│   │   └── config/
│   │       └── SecurityConfig.java
│   ├── swagger/
│   │   ├── SwaggerInfoProperties.java
│   │   └── config/
│   │       └── SwaggerConfig.java
│   └── utils/
│       ├── CookieUtils.java
│       ├── IpUtils.java
│       ├── ReqContextUtils.java
│       ├── ResponseUtils.java
│       ├── SecurityUtils.java
│       ├── ServletUtils.java
│       └── UuidUtils.java
└── app/                               # 비즈니스 도메인 레이어
    ├── user/                          # 사용자 도메인
    └── infra/                         # 인프라 서비스
```

```
src/main/resources/
├── application.yml
├── application-local.yml
├── application-local.example.yml      # 로컬 설정 예시
└── message/
    └── messages.properties            # 에러 메시지 (한국어)
```

## 아키텍처

```
┌─────────────────────────────────────────┐
│          REST Controllers               │  ← AbstractController 상속
├─────────────────────────────────────────┤
│          Service Layer                  │  ← AbstractService 상속
├─────────────────────────────────────────┤
│        Repository (Spring Data JPA)     │
├─────────────────────────────────────────┤
│           PostgreSQL                    │
└─────────────────────────────────────────┘
         ↕ Redis (Token / TTL)
         ↕ ja-agent (AI API)
```

**공통 응답 포맷** — 모든 API 응답은 `BizRespVo<T>`로 래핑됩니다.

```json
{
  "statusCode": 200,
  "resultCode": "SUCCESS",
  "message": null,
  "body": { },
  "requestId": "uuid",
  "timestamp": "2026-01-01T00:00:00"
}
```

## 로컬 실행

### 사전 요구사항

- Java 21
- [ja-infra](https://github.com/job-assistant/ja-infra) — PostgreSQL, Redis 실행 필요

### 설정

```bash
cp src/main/resources/application-local.example.yml src/main/resources/application-local.yml
```

`application-local.yml`에서 Google OAuth2 클라이언트 정보를 입력합니다.

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
```

### 실행

```bash
./gradlew bootRun
```

서버 기본 포트: `8088`

### API 문서

서버 실행 후 Swagger UI에서 확인할 수 있습니다.

```
http://localhost:8088/swagger-ui.html
```

## 환경 변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `SPRING_PROFILES_ACTIVE` | `local` | 실행 프로파일 |

로컬 개발 시 PostgreSQL 접속 정보는 `application-local.yml`에서 설정합니다.

| 항목 | 기본값 |
|------|--------|
| PostgreSQL Host | `localhost:15433` |
| Database | `job` |
| Redis Host | `localhost:16379` |
