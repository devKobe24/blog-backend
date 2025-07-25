# 블로그 백엔드 API

Spring Boot 기반의 블로그 백엔드 API 서버입니다.

## 🚀 주요 기능

- **사용자 인증**: JWT 토큰 기반 인증 시스템
- **게시물 관리**: CRUD 기능과 페이징, 검색 지원
- **댓글 시스템**: 게시물별 댓글 작성, 수정, 삭제
- **카테고리 관리**: 게시물 분류를 위한 카테고리 시스템
- **태그 시스템**: 게시물 태깅 및 검색 기능
- **API 문서화**: Swagger/OpenAPI 3.0 기반 자동 문서화

## 🛠 기술 스택

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security**
- **Spring Data JPA**
- **MySQL 8.0**
- **JWT**
- **Swagger/OpenAPI 3.0**
- **Gradle**
- **Docker**

## 📋 요구사항

- Java 17 이상
- Gradle 7.0 이상
- MySQL 8.0 이상
- Docker (선택사항)

## 🚀 빠른 시작

### 1. 저장소 클론

```bash
git clone https://github.com/devKobe24/blog-backend.git
cd blog-backend
```

### 2. 데이터베이스 설정

```bash
# Docker를 사용한 MySQL 실행
docker-compose up -d mysql

# 또는 로컬 MySQL 설정
# application-local.yml 파일에서 데이터베이스 설정을 확인하세요
```

### 3. 애플리케이션 실행

```bash
# 개발 환경 실행
./run-dev.sh

# 또는 로컬 환경 실행
./run-local.sh

# 또는 프로덕션 환경 실행
./run-prod.sh
```

### 4. API 문서 확인

애플리케이션이 실행되면 다음 URL에서 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API 문서**: [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

## 📁 프로젝트 구조

```
src/main/java/com/blog/
├── BlogBackendApplication.java     # 메인 애플리케이션 클래스
├── config/                        # 설정 클래스들
│   ├── SecurityConfig.java        # Spring Security 설정
│   └── SwaggerConfig.java         # Swagger 설정
├── controller/                    # REST API 컨트롤러
│   ├── UserController.java        # 사용자 인증 API
│   ├── PostController.java        # 게시물 API
│   ├── CommentController.java     # 댓글 API
│   ├── CategoryController.java    # 카테고리 API
│   └── TagController.java         # 태그 API
├── dto/                          # 데이터 전송 객체
│   ├── request/                  # 요청 DTO
│   └── response/                 # 응답 DTO
├── entity/                       # JPA 엔티티
│   ├── User.java
│   ├── Post.java
│   ├── Comment.java
│   ├── Category.java
│   └── Tag.java
├── repository/                   # 데이터 접근 계층
├── service/                      # 비즈니스 로직 계층
├── security/                     # 보안 관련 클래스
└── exception/                    # 예외 처리 클래스
```

## 🔐 인증

API는 JWT 토큰 기반 인증을 사용합니다.

### 인증이 필요한 API

대부분의 API는 JWT 토큰 인증이 필요합니다. 토큰은 HTTP 헤더에 포함해야 합니다:

```
Authorization: Bearer <your-jwt-token>
```

### 인증이 불필요한 API

- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인
- `GET /api/posts` - 게시물 목록 조회
- `GET /api/posts/{postId}` - 게시물 상세 조회
- `GET /api/categories` - 카테고리 목록 조회
- `GET /api/tags` - 태그 목록 조회

## 📚 API 문서

자세한 API 문서는 다음을 참조하세요:

- **[API_DOCUMENTATION.md](./API_DOCUMENTATION.md)** - 완전한 API 문서
- **Swagger UI** - http://localhost:8080/swagger-ui.html

## 🔧 환경 설정

### 프로파일별 설정

- **local**: 로컬 개발 환경
- **dev**: 개발 서버 환경
- **prod**: 프로덕션 환경
- **docker**: Docker 환경
- **aws**: AWS 환경

### 환경 변수

주요 환경 변수는 `application.yml` 파일에서 설정할 수 있습니다:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_db
    username: your_username
    password: your_password
  
  jpa:
    hibernate:
      ddl-auto: update
    
jwt:
  secret: your_jwt_secret_key
  expiration: 86400000  # 24시간
```

## 🐳 Docker 지원

### Docker Compose로 전체 스택 실행

```bash
# 전체 스택 실행 (MySQL + 애플리케이션)
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 중지
docker-compose down
```

### Docker 이미지 빌드

```bash
# 이미지 빌드
docker build -t blog-backend .

# 컨테이너 실행
docker run -p 8080:8080 blog-backend
```

## 🧪 테스트

```bash
# 단위 테스트 실행
./gradlew test

# 통합 테스트 실행
./gradlew integrationTest
```

## 📝 라이선스

이 프로젝트는 **Custom License** 하에 배포되며, 상업적 사용을 금지합니다.

전체 라이선스 조건은 [LICENSE](./LICENSE) 파일을 참조하세요.

상업적 라이선스 문의: **dev.skyachieve91@gmail.com**

## 👨‍💻 개발자

- **개발자**: Minseong Kang
- **이메일**: devkobe24@gmail.com
- **GitHub**: https://github.com/devKobe24

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 다음으로 연락해주세요:

- **이메일**: devkobe24@gmail.com
- **GitHub Issues**: [Issues 페이지](https://github.com/devKobe24/blog-backend/issues)
