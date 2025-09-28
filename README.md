### 즉행GMG_BE 백엔드

여행 일정/콘텐츠/리뷰를 중심으로 한 Spring Boot 기반 백엔드 서비스입니다. OAuth2 로그인(Google/Kakao)과 JWT 기반 인증을 제공하며, 공공데이터 API 동기화, 지역/여행/리뷰 도메인 기능을 포함합니다.

---

### 기술 스택
- **Language/Runtime**: Java 17
- **Framework**: Spring Boot 3.x (Web, Data JPA, OAuth2 Client)
- **Auth**: OAuth2(Google, Kakao), JWT
- **DB**: MySQL
- **Docs**: springdoc-openapi (Swagger UI)
- **Build**: Gradle
- **ETC**: Lombok, Scheduling(@EnableScheduling)

---

### 프로젝트 구조(주요 패키지)
- `com.gmg.jeukhaeng`
  - `JeukhaengApplication` (메인 진입점, 스케줄링 활성화)
  - `auth` (보안/인증)
    - `config`: `SecurityConfig`, `CorsConfig`
    - `filter`: `JwtAuthenticationFilter`
    - `handler`: `OAuth2LoginSuccessHandler`
    - `util`: `JwtUtil`
    - `controller`: `AuthController`
    - `entity`/`repository`/`service`: 리프레시 토큰 저장/관리
  - `content` (공공데이터 연동)
    - `api`: `ContentApiClient`
    - `batch`: `ContentBatchScheduler`
    - `controller`: `ContentController`
    - `entity`/`repository`/`dto`
  - `area` (지역/시군구별 콘텐츠)
    - `controller`: `AreaController`
    - `service`, `dto`, `entity`
  - `trip` (여행 일정)
    - `controller`: `TripController`
    - `service`, `dto`, `entity`, `repository`
  - `review` (리뷰/별점)
    - `controller`: `ReviewController`
    - `service`, `dto`, `entity`, `repository`
  - `user` (사용자 관리)
    - `service`: `UserService`, `dto`, `entity`, `repository`
  - `config`: `SwaggerConfig`

---

### 설정 파일 개요
- `src/main/resources/application.yml`
  - `spring.datasource`: MySQL 연결 정보(`.env`에서 주입)
  - `spring.jpa.hibernate.ddl-auto`: `update`
  - `spring.security.oauth2.client`: Google/Kakao OAuth2 설정
  - `jwt`: `secret`, `access-token-expiration`, `refresh-token-expiration`
  - `frontend.redirect-base-url`: 로그인 성공 등 리다이렉트 기준 URL
  - `api.encoded-service-key`: 공공데이터 인증키
  - `test.account.password`: 테스트 계정 비밀번호

---

### 인증/인가 개요
- **OAuth2 로그인**: `/oauth2/authorization/{google|kakao}`로 인증 시작, 성공 시 `OAuth2LoginSuccessHandler`에서 토큰 발급/처리
- **JWT 인증 필터**: `JwtAuthenticationFilter`가 `Authorization: Bearer <token>`를 읽어 사용자 인증 컨텍스트 설정
- **보안 설정**: `SecurityConfig`
  - 허용: `/swagger-ui/**`, `/v3/api-docs/**`, `/oauth2/authorization/**`, `/api/auth/test-login`
  - 그 외 요청: 인증 필요(STATELESS)
- **토큰 관리**: `JwtUtil`(액세스/리프레시 생성·검증), `RefreshTokenService`/`RefreshTokenRepository`

---

### 주요 API 요약
- `GET /api/auth/me` 내 정보 조회
- `POST /api/auth/refresh` 리프레시 토큰으로 액세스 토큰 재발급
- `POST /api/auth/logout` 리프레시 토큰 삭제(로그아웃)
- `POST /api/auth/test-login` 테스트 계정 로그인(JWT 페어 반환)

- `POST /api/contents/sync` 공공데이터 동기화 배치 트리거(주의: 운영/실서버에서만 사용 권장)

- `GET /api/areas` 지역/시군구별 콘텐츠 페이지 조회
- `GET /api/areas/all` 모든 지역/시군구 목록 조회

- `POST /api/trips` 여행 일정 생성
- `GET /api/trips` 내 여행 일정 목록
- `GET /api/trips/{tripId}` 여행 일정 상세
- `GET /api/trips/me/planned` 생성한 여행 전체
- `GET /api/trips/me/planned/{tripId}` 생성한 여행 상세
- `GET /api/trips/me/completed` 다녀온 여행 전체
- `GET /api/trips/me/completed/{tripId}` 다녀온 여행 상세
- `PATCH /api/trips/{tripId}/complete` 다녀왔어요 버튼

- `POST /api/trips/{tripId}/reviews` 특정 여행 내 콘텐츠 리뷰 작성/수정
- `GET /api/trips/{tripId}/my-reviews` 내가 남긴 리뷰 목록
- `GET /api/contents/{contentId}/reviews` 특정 콘텐츠의 리뷰와 요약

---

### Swagger(OpenAPI)
- 실행 후 Swagger UI: `/swagger-ui/index.html`
- JWT 보안 스키마가 설정되어 있어, 우측 상단 Authorize에서 Bearer 토큰 입력 가능
