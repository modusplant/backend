# ModusPlant
* 모든 식물 집사들을 위한 반려식물 커뮤니티 플랫폼
* 진행 기간: 2024.12 - 2026.03
* 팀 구성

| 팀원  | 역할 |
| ------------- |:-------------:|
| 박준희 | 회원가입(email/password) + 댓글 REST API, Spring Security 인증 인가, 전역적 예외 핸들링, 비속어 필터 |
| 박준혁 | content |
| 송유정 | content |
| 고동혁 | content |

# 주요 기능
### 보안 및 인증
* Spring Security & JWT 기반 stateless한 인증 체계, 경량의 SecurityContext 구축


* 멀티파트 파일 업로드 및 Wasabi 스토리지 연동
* Spring Security & JWT 기반의 stateless 인증 체계
* JPA/Hibernate 기반 간단한 CRUD 작업 + jOOQ 기반 대규모 Read 작업 및 통계 쿼리
* Mailjet 기반 이메일 인증 시스템

# 기술 스택
### 1. Backend & Security
* Framework: Spring Boot 3.x
* Security: Spring Security, JJWT
* API Docs: SpringDoc OpenAPI (Swagger)

### 2. Data Storage & Persistence
* Main DB: PostgreSQL
* Caching: Redis
* Persistence: JPA/Hibernate, jOOQ, Flyway

### 3. Infrastructure & DevOps
* Cloud: Spring Cloud AWS
* Monitoring: Grafana, Loki, Prometheus, Tempo
* Logging: Spring Aop, Logback

### 4. Test & Quality
* Test: JUnit5, AssertJ, Mockito, Hamcrest
* coverage: JaCoCo
* Others: Lombok, MapStruct

### 5. External Services & Collaboration
* Third-party: Mailjet, Kakao/Google OAuth 2.0
* Tools: Git, Jira, Confluence, Slack, Postman

# 아키텍처 설계
(아키텍처 구조도 필요)
(Erd 필요)

# 기술적 의사결정 및 트러블슈팅

# API 명세서
[모두의식물 API 명세서](https://www.notion.so/API-2bceed37f9c5809eace3fc0520c9285c)

# 프로젝트 실행 방법

# 협업 방식
