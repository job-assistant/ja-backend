# Company API 명세서

> Base URL: `http://localhost:8080`
> 모든 요청은 인증(JWT Access Token)이 필요합니다.
> Authorization 헤더: `Authorization: Bearer {accessToken}`

---

## 공통 응답 포맷

모든 API는 아래 구조로 응답합니다.

```json
{
  "statusCode": "OK",
  "resultCode": 200,
  "message": "성공",
  "body": { ... },
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-04-08 12:00:00"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `statusCode` | string | HTTP 상태 텍스트 (`"OK"`, `"CREATED"` 등) |
| `resultCode` | number | HTTP 상태 코드 (`200`, `201`, `404` 등) |
| `message` | string | 응답 메시지 |
| `body` | object \| array \| null | 실제 응답 데이터 |
| `requestId` | string (UUID) | 요청 고유 ID |
| `timestamp` | string | 응답 시각 (`yyyy-MM-dd HH:mm:ss`, KST) |

### 리스트 응답 body 구조

```json
{
  "body": {
    "list": [ ... ]
  }
}
```

### 에러 응답 예시

```json
{
  "statusCode": "NOT_FOUND",
  "resultCode": 404,
  "message": "회사를 찾을 수 없습니다.",
  "body": null,
  "requestId": "...",
  "timestamp": "2026-04-08 12:00:00"
}
```

---

## Status Enum

지원서 진행 상태 값입니다. 요청/응답 모두 아래 문자열 그대로 사용합니다.

| 값 | 설명 |
|----|------|
| `"applied"` | 지원 완료 |
| `"document"` | 서류 전형 |
| `"first"` | 1차 면접 |
| `"second"` | 2차 면접 |
| `"final"` | 최종 면접 |
| `"passed"` | 최종 합격 |
| `"failed"` | 불합격 |

---

## 공통 타입 정의 (TypeScript)

```typescript
type Status = "applied" | "document" | "first" | "second" | "final" | "passed" | "failed";

interface ApiResponse<T> {
  statusCode: string;
  resultCode: number;
  message: string;
  body: T;
  requestId: string;
  timestamp: string; // "yyyy-MM-dd HH:mm:ss"
}

interface ListBody<T> {
  list: T[];
}

// 회사 목록 응답 아이템
interface CompanySummary {
  id: number;
  name: string;
  position: string | null;
  status: Status;
  isDefault: boolean;
}

// 회사 상세 응답
interface CompanyDetail {
  id: number;
  name: string;
  position: string | null;
  status: Status;
  interviewDate: string | null; // "yyyy-MM-dd"
  isDefault: boolean;
  createdAt: string; // "yyyy-MM-dd HH:mm:ss"
  updatedAt: string; // "yyyy-MM-dd HH:mm:ss"
}
```

---

## API 목록

| # | Method | URL | 설명 |
|---|--------|-----|------|
| 1 | GET | `/api/companies` | 회사 목록 조회 (검색/필터) |
| 2 | GET | `/api/companies/{companyId}` | 회사 상세 조회 |
| 3 | POST | `/api/companies` | 회사 등록 |
| 4 | PUT | `/api/companies/{companyId}` | 회사 수정 |
| 5 | PATCH | `/api/companies/{companyId}/default` | 기본 회사 설정 |
| 6 | DELETE | `/api/companies/{companyId}` | 회사 삭제 |

---

## 1. 회사 목록 조회

```
GET /api/companies
```

### Query Parameters

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `name` | string | 선택 | 회사명 부분 검색 (대소문자 무시) |
| `status` | Status | 선택 | 진행 상태 필터 |

### 요청 예시

```
GET /api/companies?name=카카오&status=first
Authorization: Bearer {accessToken}
```

### 응답 예시 `200 OK`

```json
{
  "statusCode": "OK",
  "resultCode": 200,
  "message": "성공",
  "body": {
    "list": [
      {
        "id": 1,
        "name": "카카오",
        "position": "백엔드 개발자",
        "status": "first",
        "isDefault": true
      },
      {
        "id": 2,
        "name": "카카오페이",
        "position": "서버 개발자",
        "status": "first",
        "isDefault": false
      }
    ]
  },
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-04-08 12:00:00"
}
```

### TypeScript 타입

```typescript
type GetCompaniesResponse = ApiResponse<ListBody<CompanySummary>>;

interface GetCompaniesParams {
  name?: string;
  status?: Status;
}
```

---

## 2. 회사 상세 조회

```
GET /api/companies/{companyId}
```

### Path Parameters

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `companyId` | number | 회사 ID |

### 요청 예시

```
GET /api/companies/1
Authorization: Bearer {accessToken}
```

### 응답 예시 `200 OK`

```json
{
  "statusCode": "OK",
  "resultCode": 200,
  "message": "성공",
  "body": {
    "id": 1,
    "name": "카카오",
    "position": "백엔드 개발자",
    "status": "first",
    "interviewDate": "2026-04-15",
    "isDefault": true,
    "createdAt": "2026-04-01 10:00:00",
    "updatedAt": "2026-04-05 15:30:00"
  },
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-04-08 12:00:00"
}
```

### 에러

| 상태 코드 | 설명 |
|-----------|------|
| `404 Not Found` | 회사가 존재하지 않거나 본인 소유가 아닌 경우 |

### TypeScript 타입

```typescript
type GetCompanyResponse = ApiResponse<CompanyDetail>;
```

---

## 3. 회사 등록

```
POST /api/companies
```

### Request Body (`application/json`)

| 필드 | 타입 | 필수 | 유효성 | 설명 |
|------|------|------|--------|------|
| `name` | string | **필수** | 최대 100자, 빈 값 불가 | 회사명 |
| `position` | string \| null | 선택 | 최대 100자 | 지원 직무 |
| `status` | Status | **필수** | Status Enum 값 | 진행 상태 |
| `interviewDate` | string \| null | 선택 | `"yyyy-MM-dd"` 형식 | 면접 일자 |
| `isDefault` | boolean | **필수** | - | 기본 회사 여부 |

### 요청 예시

```json
POST /api/companies
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "네이버",
  "position": "백엔드 개발자",
  "status": "applied",
  "interviewDate": null,
  "isDefault": false
}
```

### 응답 예시 `201 Created`

```json
{
  "statusCode": "CREATED",
  "resultCode": 201,
  "message": "성공",
  "body": {
    "id": 3,
    "name": "네이버",
    "position": "백엔드 개발자",
    "status": "applied",
    "interviewDate": null,
    "isDefault": false,
    "createdAt": "2026-04-08 12:00:00",
    "updatedAt": "2026-04-08 12:00:00"
  },
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-04-08 12:00:00"
}
```

### TypeScript 타입

```typescript
interface CreateCompanyRequest {
  name: string;
  position?: string | null;
  status: Status;
  interviewDate?: string | null; // "yyyy-MM-dd"
  isDefault: boolean;
}

type CreateCompanyResponse = ApiResponse<CompanyDetail>;
```

---

## 4. 회사 수정

```
PUT /api/companies/{companyId}
```

### Path Parameters

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `companyId` | number | 회사 ID |

### Request Body (`application/json`)

| 필드 | 타입 | 필수 | 유효성 | 설명 |
|------|------|------|--------|------|
| `name` | string | **필수** | 최대 100자, 빈 값 불가 | 회사명 |
| `position` | string \| null | 선택 | 최대 100자 | 지원 직무 |
| `status` | Status | **필수** | Status Enum 값 | 진행 상태 |
| `interviewDate` | string \| null | 선택 | `"yyyy-MM-dd"` 형식 | 면접 일자 |

> `isDefault`는 수정 불가 — 기본 회사 변경은 `PATCH /default` 사용

### 요청 예시

```json
PUT /api/companies/3
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "네이버",
  "position": "서버 개발자",
  "status": "document",
  "interviewDate": null
}
```

### 응답 예시 `200 OK`

```json
{
  "statusCode": "OK",
  "resultCode": 200,
  "message": "성공",
  "body": {
    "id": 3,
    "name": "네이버",
    "position": "서버 개발자",
    "status": "document",
    "interviewDate": null,
    "isDefault": false,
    "createdAt": "2026-04-08 12:00:00",
    "updatedAt": "2026-04-08 13:00:00"
  },
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-04-08 13:00:00"
}
```

### 에러

| 상태 코드 | 설명 |
|-----------|------|
| `404 Not Found` | 회사가 존재하지 않거나 본인 소유가 아닌 경우 |

### TypeScript 타입

```typescript
interface UpdateCompanyRequest {
  name: string;
  position?: string | null;
  status: Status;
  interviewDate?: string | null; // "yyyy-MM-dd"
}

type UpdateCompanyResponse = ApiResponse<CompanyDetail>;
```

---

## 5. 기본 회사 설정

```
PATCH /api/companies/{companyId}/default
```

지정한 회사를 기본 회사로 설정합니다.
**기존에 기본 회사로 설정된 다른 회사는 자동으로 해제됩니다.**

### Path Parameters

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `companyId` | number | 기본으로 설정할 회사 ID |

### 요청 예시

```
PATCH /api/companies/1/default
Authorization: Bearer {accessToken}
```

### 응답 예시 `200 OK`

```json
{
  "statusCode": "OK",
  "resultCode": 200,
  "message": "성공",
  "body": null,
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-04-08 12:00:00"
}
```

### 에러

| 상태 코드 | 설명 |
|-----------|------|
| `404 Not Found` | 회사가 존재하지 않거나 본인 소유가 아닌 경우 |

### TypeScript 타입

```typescript
type SetDefaultCompanyResponse = ApiResponse<null>;
```

---

## 6. 회사 삭제

```
DELETE /api/companies/{companyId}
```

### Path Parameters

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `companyId` | number | 삭제할 회사 ID |

### 요청 예시

```
DELETE /api/companies/3
Authorization: Bearer {accessToken}
```

### 응답 예시 `200 OK`

```json
{
  "statusCode": "OK",
  "resultCode": 200,
  "message": "성공",
  "body": null,
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-04-08 12:00:00"
}
```

### 에러

| 상태 코드 | 설명 |
|-----------|------|
| `404 Not Found` | 회사가 존재하지 않거나 본인 소유가 아닌 경우 |

### TypeScript 타입

```typescript
type DeleteCompanyResponse = ApiResponse<null>;
```

---

## 전체 TypeScript 타입 정의 (복사용)

```typescript
// ─── 공통 ───────────────────────────────────────────────
type Status = "applied" | "document" | "first" | "second" | "final" | "passed" | "failed";

interface ApiResponse<T> {
  statusCode: string;
  resultCode: number;
  message: string;
  body: T;
  requestId: string;
  timestamp: string;
}

interface ListBody<T> {
  list: T[];
}

// ─── 응답 DTO ────────────────────────────────────────────
interface CompanySummary {
  id: number;
  name: string;
  position: string | null;
  status: Status;
  isDefault: boolean;
}

interface CompanyDetail {
  id: number;
  name: string;
  position: string | null;
  status: Status;
  interviewDate: string | null; // "yyyy-MM-dd"
  isDefault: boolean;
  createdAt: string;            // "yyyy-MM-dd HH:mm:ss"
  updatedAt: string;            // "yyyy-MM-dd HH:mm:ss"
}

// ─── 요청 DTO ────────────────────────────────────────────
interface CreateCompanyRequest {
  name: string;
  position?: string | null;
  status: Status;
  interviewDate?: string | null;
  isDefault: boolean;
}

interface UpdateCompanyRequest {
  name: string;
  position?: string | null;
  status: Status;
  interviewDate?: string | null;
}

interface GetCompaniesParams {
  name?: string;
  status?: Status;
}

// ─── API 응답 타입 ───────────────────────────────────────
type GetCompaniesResponse    = ApiResponse<ListBody<CompanySummary>>;
type GetCompanyResponse      = ApiResponse<CompanyDetail>;
type CreateCompanyResponse   = ApiResponse<CompanyDetail>;
type UpdateCompanyResponse   = ApiResponse<CompanyDetail>;
type SetDefaultResponse      = ApiResponse<null>;
type DeleteCompanyResponse   = ApiResponse<null>;
```

---

## 날짜/시간 포맷 정리

| 필드 | 포맷 | 예시 |
|------|------|------|
| `interviewDate` | `yyyy-MM-dd` | `"2026-04-15"` |
| `createdAt` / `updatedAt` | `yyyy-MM-dd HH:mm:ss` | `"2026-04-08 12:00:00"` |
| `timestamp` (공통 응답) | `yyyy-MM-dd HH:mm:ss` | `"2026-04-08 12:00:00"` (KST) |
