# 🪴ModusPlant
**DDD + Clean Architecture 기반으로 외부 환경 변화에 유연하게 대응하는 반려식물 플랫폼 서버**
* 진행 기간: 2024.12 - 현재

| 팀원  | 역할 |
| ------------- |:-------------:|
| 박준희 | 회원가입(email/password) + 댓글 REST API, Spring Security 인증 인가, 전역적 예외 핸들링, 비속어 필터 |
| 박준혁 | content |
| 송유정 | content |
| 고동혁 | content |

### 핵심 가치
* **관심사 분리:** 계층별 엄격한 역할 분리로 인프라 및 프레임워크 변환 시 안정적 비즈니스 로직 유지
* **비용 최적화:** 비즈니스 요구사항에 최적화된 API와 기능 구현으로 개발 및 운영 효율 극대화
* **데이터 무결성:** Bean Validation과 도메인 검증, 엄격한 테스트를 통한 데이터 정합성 확보

<br>

# 📝주요 기능
### 인증 및 보안
* **사용자 인증 관리:** Spring Security 기반 로그인/회원가입 및 메일 기능(Mailjet) 연동을 통한 이메일 인증 구조
* **권한 인가 체계:** JWT의 Access/Refresh Token 기반 stateless 인증 시스템
* **소셜 로그인 연동:** OAuth 2.0을 통한 Google, Kakao 소셜 계정 기능
* **보안 및 정책 관리:** 회원 약관 동의 프로세스 및 사용자 프로필 관리 API

### 컨텐츠 및 커뮤니티
* **게시 서비스:** 게시글 및 댓글의 CRUD 및 1차/2차 카테고리 분류 체계를 통한 구조적 데이터 관리
* **이미지 기반 소통:** 멀티파트 데이터 처리 및 Wasabi 스토리지 운용
* **사용자 활동:** 게시글/댓글 좋아요, 북마크를 통한 사용자 참여 기능

### 고객 소통 및 운영
* **신고 시스템:** 서비스 가이드라인 준수를 위한 게시글 및 댓글 신고 API
* **피드백 루프:** 서비스 개선을 위한 건의사항 및 버그제보 창구 운영

<br>

# 🛠기술 스택 & 도입 이유
### 1. 백엔드 & 영속성
* **Spring Boot 3.x & Java 21:** 알림 기능의 신속한 처리를 위한 가상 스레드 도입
* **PostgreSQL 17:** 게시글 업로드용 이미지를 위한 JSONB, 전문 검색 및 GIN INDEX를 통한 빠른 텍스트 검색
* **Hybrid DB 접근 방식**
    * JPA/Hibernate: 단순 DML 작업, 영속성 맥락을 통한 dirty checking, SQL 생성 메서드로 편리한 쿼리 개발
    * jOOQ: 대규모 READ 및 영속성이 불필요한 정확한 데이터 조회
* **Flyway:** DB 변경의 히스토리 유지 및 팀원의 로컬 DB 동기화 자동화

### 2. 인프라 & 모니터링
* **Redis:** 반복적인 게시글 및 댓글 조회 성능 향상, JWT 접근 토큰 blacklisting으로 보안 강화
* **LGTM Stack (Grafana, Loki, Prometheus, Tempo):** 소규모 환경이므로 로컬 파일 시스템을 사용하는 구조 채택
* **Spring AOP & Logback:** 비즈니스 로직과 전역적 로깅 로직 분리, 느슨한 결합 유지

### 3. 보안 & 최적화
* **Spring Security & JJWT:** 인증/인가 매커니즘 단일화, 확장성 및 DB 조회 비용 절감 이점이 있는 stateless 구조 채택
* **JaCoCo:** 테스트 커버리지를 시각화하여 지속적인 코드 품질 유지
* **MapStruct:** DTO와 Domain 간 매핑 로직을 자동 생성하여 boilerplate 코드 제거

### 4. 외부 서비스
* **Mailjet:** 회원가입 및 비밀번호 변경 시 이메일의 신뢰성 확보를 위해 도입
* **OAuth 2.0 (Google/Kakao):** 신규 사용자의 로그인 편의성 위해 제공

<br>

# 🗺️아키텍처 설계
(아키텍처 구조도 필요)
(Erd 필요)

<br>

# 기술적 의사결정 및 트러블슈팅

<br>

# API 명세서
[모두의식물 API 명세서](https://www.notion.so/API-2bceed37f9c5809eace3fc0520c9285c)

<br>

# 프로젝트 실행 방법

<br>

# 협업 방식
### 깃모지
| 이모티콘 | 커밋 유형 | 의미 |
| --- | --- | --- |
| ✨ `:sparkles:` | `Feat` | 새로운 기능 추가 |
| 🐛 `:bug:` | `Fix` | 버그 해소 |
| 🚑 `:ambulance:` | `HOTFIX!` | 긴급 버그 해소 |
| 💄 `:lipstick:` | `Style` | CSS 및 UI 스타일 추가 및 수정 |
| ♻️ `:recycle:` | `Refactor` | 코드 리팩토링 |
| 🎨 `:art:` | `Format` | 코드 구조 / 서식 변경 (코드 자체 변경 없음) |
| 💥 `:boom:`  | `BREAKING!` | 거대한 변경 |
| 🔥 `:fire:` | `Remove` | 파일 삭제 |
| 🥅 `:goal_net:` | `Catch` | 잘못 작성된 코드 수정 |
| ➕ `:heavy_plus_sign:` | `Dependency` | 의존성 추가 |
| ➖ `:heavy_minus_sign:` | `Dependency` | 의존성 제거 |
| 📝 `:memo:` | `Docs` | 문서 추가 및 수정 |
| ⏪️ `:rewind:` | `Revert` | 변경 철회 |
| 💬 `:speech_balloon:` | `Comment` | 주석 추가 및 수정 |
| 🚚 `:truck:` | `Rename` | 파일 또는 폴더명 수정 및 이동 |
| ✅ `:white_check_mark:`  | `Test` | 테스트 코드 추가 및 수정 |
| 🔧 `:wrench:` | `Chore` | 잡다한 과업 |
| 🎉 `:tada:` | `Begin` | 프로젝트 시작 |

### Commit 형식
 ```
<Jira 브랜치 식별자> <깃모지> <제목>

내용
내용 ...
```

```
MP-89 :sparkles: Feat: SecurityResponseUtils 추가

- filter chain 내에서 일관된 응답 charset을 유지하기 위해 추가함
```
