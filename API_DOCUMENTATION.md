# 블로그 백엔드 API 문서

## 개요

이 문서는 블로그 백엔드 API의 사용법을 설명합니다. API는 RESTful 원칙을 따르며, JWT 토큰 기반 인증을 사용합니다.

## 기본 정보

- **Base URL**: `http://localhost:8080` (개발 환경)
- **API 버전**: 1.0.0
- **인증 방식**: JWT Bearer Token
- **Content-Type**: `application/json`

## 인증

대부분의 API는 JWT 토큰 인증이 필요합니다. 토큰은 HTTP 헤더에 다음과 같이 포함해야 합니다:

```
Authorization: Bearer <your-jwt-token>
```

## API 엔드포인트

### 1. 사용자 인증 API (`/api/auth`)

#### 1.1 회원가입
- **URL**: `POST /api/auth/signup`
- **설명**: 새로운 사용자를 등록합니다.
- **인증**: 불필요
- **요청 본문**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "nickname": "string"
}
```
- **응답**:
```json
{
  "id": 1,
  "username": "string",
  "email": "string",
  "nickname": "string",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### 1.2 로그인
- **URL**: `POST /api/auth/login`
- **설명**: 사용자 로그인을 수행합니다.
- **인증**: 불필요
- **요청 본문**:
```json
{
  "username": "string",
  "password": "string"
}
```
- **응답**:
```json
{
  "token": "string",
  "user": {
    "id": 1,
    "username": "string",
    "email": "string",
    "nickname": "string",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

#### 1.3 내 정보 조회
- **URL**: `GET /api/auth/me`
- **설명**: 현재 로그인한 사용자의 정보를 조회합니다.
- **인증**: 필요
- **응답**:
```json
{
  "id": 1,
  "username": "string",
  "email": "string",
  "nickname": "string",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### 1.4 프로필 수정
- **URL**: `PUT /api/auth/profile`
- **설명**: 현재 사용자의 프로필을 수정합니다.
- **인증**: 필요
- **요청 본문**:
```json
{
  "nickname": "string",
  "email": "string"
}
```
- **응답**:
```json
{
  "id": 1,
  "username": "string",
  "email": "string",
  "nickname": "string",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### 1.5 비밀번호 변경
- **URL**: `PUT /api/auth/password`
- **설명**: 현재 사용자의 비밀번호를 변경합니다.
- **인증**: 필요
- **요청 본문**:
```json
{
  "currentPassword": "string",
  "newPassword": "string"
}
```
- **응답**: 200 OK (본문 없음)

### 2. 게시물 API (`/api/posts`)

#### 2.1 게시물 생성
- **URL**: `POST /api/posts`
- **설명**: 새로운 게시물을 생성합니다.
- **인증**: 필요
- **요청 본문**:
```json
{
  "title": "string",
  "content": "string",
  "categoryId": 1,
  "tagIds": [1, 2, 3]
}
```
- **응답**: 201 Created
```json
{
  "id": 1,
  "title": "string",
  "content": "string",
  "author": {
    "id": 1,
    "username": "string",
    "nickname": "string"
  },
  "category": {
    "id": 1,
    "name": "string"
  },
  "tags": [
    {
      "id": 1,
      "name": "string"
    }
  ],
  "viewCount": 0,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### 2.2 게시물 수정
- **URL**: `PUT /api/posts/{postId}`
- **설명**: 기존 게시물을 수정합니다.
- **인증**: 필요 (작성자만)
- **요청 본문**:
```json
{
  "title": "string",
  "content": "string",
  "categoryId": 1,
  "tagIds": [1, 2, 3]
}
```
- **응답**: 200 OK (게시물 정보)

#### 2.3 게시물 삭제
- **URL**: `DELETE /api/posts/{postId}`
- **설명**: 게시물을 삭제합니다.
- **인증**: 필요 (작성자만)
- **응답**: 204 No Content

#### 2.4 게시물 단건 조회
- **URL**: `GET /api/posts/{postId}`
- **설명**: 게시물을 조회합니다. 조회수가 증가합니다.
- **인증**: 불필요
- **응답**: 200 OK (게시물 상세 정보)

#### 2.5 게시물 목록 조회
- **URL**: `GET /api/posts`
- **설명**: 페이징을 지원하는 게시물 목록을 조회합니다.
- **인증**: 불필요
- **쿼리 파라미터**:
  - `page`: 페이지 번호 (기본값: 0)
  - `size`: 페이지 크기 (기본값: 10)
- **응답**: 200 OK
```json
{
  "content": [
    {
      "id": 1,
      "title": "string",
      "excerpt": "string",
      "author": {
        "id": 1,
        "nickname": "string"
      },
      "category": {
        "id": 1,
        "name": "string"
      },
      "tags": [
        {
          "id": 1,
          "name": "string"
        }
      ],
      "viewCount": 0,
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

#### 2.6 게시물 검색
- **URL**: `GET /api/posts/search`
- **설명**: 조건에 맞는 게시물을 검색합니다.
- **인증**: 불필요
- **쿼리 파라미터**:
  - `keyword`: 검색 키워드
  - `categoryId`: 카테고리 ID
  - `tagIds`: 태그 ID 목록
  - `page`: 페이지 번호
  - `size`: 페이지 크기
- **응답**: 200 OK (페이징된 게시물 목록)

### 3. 댓글 API (`/api/comments`)

#### 3.1 댓글 생성
- **URL**: `POST /api/comments/posts/{postId}`
- **설명**: 게시물에 댓글을 작성합니다.
- **인증**: 필요
- **요청 본문**:
```json
{
  "content": "string"
}
```
- **응답**: 201 Created
```json
{
  "id": 1,
  "content": "string",
  "author": {
    "id": 1,
    "nickname": "string"
  },
  "postId": 1,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### 3.2 댓글 수정
- **URL**: `PUT /api/comments/{commentId}`
- **설명**: 댓글을 수정합니다.
- **인증**: 필요 (작성자만)
- **요청 본문**:
```json
{
  "content": "string"
}
```
- **응답**: 200 OK (수정된 댓글 정보)

#### 3.3 댓글 삭제
- **URL**: `DELETE /api/comments/{commentId}`
- **설명**: 댓글을 삭제합니다 (소프트 삭제).
- **인증**: 필요 (작성자만)
- **응답**: 204 No Content

#### 3.4 게시물별 댓글 목록 조회
- **URL**: `GET /api/comments/posts/{postId}`
- **설명**: 게시물의 댓글 목록을 페이징하여 조회합니다.
- **인증**: 불필요
- **쿼리 파라미터**:
  - `page`: 페이지 번호 (기본값: 0)
  - `size`: 페이지 크기 (기본값: 10)
- **응답**: 200 OK (페이징된 댓글 목록)

#### 3.5 댓글 상세 조회
- **URL**: `GET /api/comments/{commentId}/detail`
- **설명**: 댓글의 상세 정보를 조회합니다.
- **인증**: 불필요
- **응답**: 200 OK (댓글 상세 정보)

### 4. 카테고리 API (`/api/categories`)

#### 4.1 카테고리 생성
- **URL**: `POST /api/categories`
- **설명**: 새로운 카테고리를 생성합니다 (관리자 전용).
- **인증**: 필요 (관리자 권한)
- **요청 본문**:
```json
{
  "name": "string",
  "description": "string"
}
```
- **응답**: 201 Created
```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### 4.2 카테고리 수정
- **URL**: `PUT /api/categories/{categoryId}`
- **설명**: 카테고리를 수정합니다 (관리자 전용).
- **인증**: 필요 (관리자 권한)
- **요청 본문**:
```json
{
  "name": "string",
  "description": "string"
}
```
- **응답**: 200 OK (수정된 카테고리 정보)

#### 4.3 카테고리 삭제
- **URL**: `DELETE /api/categories/{categoryId}`
- **설명**: 카테고리를 삭제합니다 (관리자 전용).
- **인증**: 필요 (관리자 권한)
- **응답**: 204 No Content

#### 4.4 전체 카테고리 목록 조회
- **URL**: `GET /api/categories`
- **설명**: 전체 카테고리 목록을 조회합니다.
- **인증**: 불필요
- **응답**: 200 OK
```json
[
  {
    "id": 1,
    "name": "string",
    "postCount": 10
  }
]
```

#### 4.5 카테고리 상세 정보 조회
- **URL**: `GET /api/categories/{categoryId}`
- **설명**: 카테고리의 상세 정보를 조회합니다.
- **인증**: 불필요
- **응답**: 200 OK
```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "postCount": 10,
  "recentPosts": [
    {
      "id": 1,
      "title": "string",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

#### 4.6 카테고리 검색
- **URL**: `GET /api/categories/search`
- **설명**: 키워드로 카테고리를 검색합니다.
- **인증**: 불필요
- **쿼리 파라미터**:
  - `keyword`: 검색 키워드
- **응답**: 200 OK (검색된 카테고리 목록)

#### 4.7 카테고리 존재 여부 확인
- **URL**: `GET /api/categories/{categoryId}/exists`
- **설명**: 카테고리의 존재 여부를 확인합니다.
- **인증**: 불필요
- **응답**: 200 OK (boolean)

#### 4.8 카테고리별 포스트 수 조회
- **URL**: `GET /api/categories/{categoryId}/post-count`
- **설명**: 카테고리에 속한 포스트 수를 조회합니다.
- **인증**: 불필요
- **응답**: 200 OK (integer)

### 5. 태그 API (`/api/tags`)

#### 5.1 태그 생성
- **URL**: `POST /api/tags`
- **설명**: 새로운 태그를 생성합니다 (관리자 전용).
- **인증**: 필요 (관리자 권한)
- **요청 본문**:
```json
{
  "name": "string",
  "description": "string"
}
```
- **응답**: 201 Created
```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### 5.2 태그 수정
- **URL**: `PUT /api/tags/{tagId}`
- **설명**: 태그를 수정합니다 (관리자 전용).
- **인증**: 필요 (관리자 권한)
- **요청 본문**:
```json
{
  "name": "string",
  "description": "string"
}
```
- **응답**: 200 OK (수정된 태그 정보)

#### 5.3 태그 삭제
- **URL**: `DELETE /api/tags/{tagId}`
- **설명**: 태그를 삭제합니다 (관리자 전용).
- **인증**: 필요 (관리자 권한)
- **응답**: 204 No Content

#### 5.4 전체 태그 목록 조회
- **URL**: `GET /api/tags`
- **설명**: 전체 태그 목록을 조회합니다.
- **인증**: 불필요
- **응답**: 200 OK
```json
[
  {
    "id": 1,
    "name": "string",
    "postCount": 10
  }
]
```

#### 5.5 태그 상세 정보 조회
- **URL**: `GET /api/tags/{tagId}`
- **설명**: 태그의 상세 정보를 조회합니다.
- **인증**: 불필요
- **응답**: 200 OK
```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "postCount": 10,
  "recentPosts": [
    {
      "id": 1,
      "title": "string",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

#### 5.6 태그 검색
- **URL**: `GET /api/tags/search`
- **설명**: 키워드로 태그를 검색합니다 (페이징 지원).
- **인증**: 불필요
- **쿼리 파라미터**:
  - `keyword`: 검색 키워드
  - `page`: 페이지 번호 (기본값: 0)
  - `size`: 페이지 크기 (기본값: 10)
- **응답**: 200 OK (페이징된 태그 목록)

#### 5.7 태그 존재 여부 확인
- **URL**: `GET /api/tags/{tagId}/exists`
- **설명**: 태그의 존재 여부를 확인합니다.
- **인증**: 불필요
- **응답**: 200 OK (boolean)

#### 5.8 태그별 포스트 수 조회
- **URL**: `GET /api/tags/{tagId}/post-count`
- **설명**: 태그가 사용된 포스트 수를 조회합니다.
- **인증**: 불필요
- **응답**: 200 OK (integer)

#### 5.9 인기 태그 목록 조회
- **URL**: `GET /api/tags/popular`
- **설명**: 포스트 수 기준으로 인기 태그 목록을 조회합니다.
- **인증**: 불필요
- **쿼리 파라미터**:
  - `limit`: 조회할 태그 수 (기본값: 10)
- **응답**: 200 OK (인기 태그 목록)

#### 5.10 태그 자동완성
- **URL**: `GET /api/tags/autocomplete`
- **설명**: 키워드에 맞는 태그 자동완성 목록을 제공합니다.
- **인증**: 불필요
- **쿼리 파라미터**:
  - `keyword`: 검색 키워드
  - `limit`: 조회할 태그 수 (기본값: 5)
- **응답**: 200 OK (자동완성 태그 목록)

## HTTP 상태 코드

- **200 OK**: 요청이 성공적으로 처리됨
- **201 Created**: 리소스가 성공적으로 생성됨
- **204 No Content**: 요청이 성공했지만 응답 본문이 없음
- **400 Bad Request**: 잘못된 요청
- **401 Unauthorized**: 인증이 필요함
- **403 Forbidden**: 권한이 없음
- **404 Not Found**: 리소스를 찾을 수 없음
- **500 Internal Server Error**: 서버 내부 오류

## 에러 응답 형식

```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "에러 메시지",
  "path": "/api/posts"
}
```

## Swagger UI

API 문서는 Swagger UI를 통해 확인할 수 있습니다:

- **개발 환경**: `http://localhost:8080/swagger-ui.html`
- **프로덕션 환경**: `https://api.blog.com/swagger-ui.html`

## 예제 사용법

### 1. 사용자 등록 및 로그인

```bash
# 1. 회원가입
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "nickname": "테스트 사용자"
  }'

# 2. 로그인
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 2. 게시물 작성

```bash
# JWT 토큰을 사용하여 게시물 작성
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "첫 번째 게시물",
    "content": "안녕하세요! 첫 번째 게시물입니다.",
    "categoryId": 1,
    "tagIds": [1, 2]
  }'
```

### 3. 게시물 목록 조회

```bash
# 게시물 목록 조회 (페이징)
curl -X GET "http://localhost:8080/api/posts?page=0&size=10"
```

## 개발 환경 설정

### 필수 요구사항

- Java 17 이상
- Gradle 7.0 이상
- MySQL 8.0 이상

### 실행 방법

1. **데이터베이스 설정**
   ```bash
   # MySQL 실행
   docker-compose up -d mysql
   ```

2. **애플리케이션 실행**
   ```bash
   # 개발 환경 실행
   ./run-dev.sh
   
   # 또는 로컬 환경 실행
   ./run-local.sh
   ```

3. **API 문서 확인**
   ```
   http://localhost:8080/swagger-ui.html
   ```

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 연락처

- **개발자**: Minseong Kang(devKobe24)
- **이메일**: dev.skyachieve91@gmail.com
- **GitHub**: https://github.com/devKobe24
