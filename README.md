# 🪴ModusPlant
**DDD + Clean Architecture 기반으로 확장성을 높인 REST API 제공 반려식물 관리 및 커뮤니티 플랫폼 서버**
### 진행 기간
* 2024.12 ~ 현재
### 핵심 가치
* **관심사 분리:** DDD + Clean Architecture 적용 후 인프라 변경(JPA -> JPA + jOOQ)에도 도메인 로직 수정이 없어 핵심 비즈니스 로직의 일관성 보장
* **비용 최적화:** Amazon RDS 대신 비교적 저렴한 가격으로 대용량 스토리지를 이용할 수 있는 Wasabi를 채택하는 등 요구사항과 환경에 맞는 기능 개발
* **데이터 무결성:** Bean Validation을 통한 빠른 검증 + 도메인 모델에 검증 로직을 캡슐화

| 팀원  | 역할 |
| ------------- |:-------------:|
| 박준희 | • Spring Security 인증/인가, 전역적 예외 핸들링<br>• 비속어 필터링, 일반 회원가입/댓글 API |
| 박준혁 | • AWS 및 Vercel 배포/관리<br>• 회원 프로필/좋아요/신고/북마크/건의 및 버그 제보<br>•게시글 최신순, 정확도순 검색 API |
| 송유정 | • 소셜 로그인(OAuth 2.0), JWT 생성 및 관리<br>• 비동기 푸시알림, 클라우드 스토리지 관리<br>• 게시글/게시글 1차+2차 카테고리/알림함 API |
| 고동혁 | • 이메일 서비스 연동, 로깅, 모니터링, CI/CD 파이프라인 구축<br>• Docker 컨테이너 기반 개발 및 프로덕션 환경 운영, 회원 약관 API |

<br>

## 📌 목차
- [주요 기능](#features)
- [아키텍처 설계](#architecture)
- [기술 스택](#tech-stack)
- [트러블슈팅](#troubleshooting)
- [API 명세서](#api)
- [협업 방식](#collaboration)

<br>

<a id="features"></a>
# 📝 주요 기능
### 1. 인증 및 사용자 관리
* 회원가입(email/password), 소셜 로그인, 이메일 인증
* 사용자 프로필 관리, 비밀번호 재설정

### 2. 커뮤니티 기능
* 게시글 및 댓글 작성/조회/수정/삭제
* 게시글 검색 및 대표사진 지정, 게시글 이미지 업로드
* 카테고리/최신순/좋아요/북마크에 따른 게시글 조회
* 게시글/작성자에 따른 댓글 조회
* 좋아요 및 북마크

### 3. 운영 및 피드백
* 게시글 및 댓글 신고
* 건의사항 및 버그 제보

<br>

<a id="architecture"></a>
# 🗺️ 아키텍처 설계

### 1. 설계 방향
* **경량의 domain 계층:** domain 계층은 상태 관리와 검증 로직만 포함하며, 비즈니스 흐름 제어 및 외부 API 오케스트레이션은 adapter 계층에서 처리
* **단순화한 usecase 계층:** 프로젝트 복잡도 고려 후 유즈 케이스는 인터페이스 중심으로 설계하고, 불필요한 클래스 생성을 지양
* **프레임워크 격리:** Spring Data JPA, Redis 등 외부 의존성을 framework 계층으로 분리하여 domain 계층의 순수성 유지

### 3. 설계 도면(Diagram)

<img width="2648" height="1490" alt="소스 코드 아키텍처" src="https://github.com/user-attachments/assets/732455f1-39a1-40d6-b563-f0b613205395">

<details>
<summary> <strong>프로덕션 환경 아키텍처</strong> </summary>
<br />
<img width="5887" height="5210" alt="프로덕션 아키텍처" src="https://github.com/user-attachments/assets/43603f19-95bf-48e7-8c97-7c6ef3ad6eef" />
</details>

<details>
<summary> <strong>데이터 모델링</strong> </summary>
<br />
<img width="520" height="328" alt="게시글 테이블" src="https://github.com/user-attachments/assets/1c6a6d43-9662-44c3-a6c9-68fa268d1a87" />
<img width="517" height="175" alt="댓글 테이블" src="https://github.com/user-attachments/assets/44bee886-0dfa-401f-bc97-c630c1eb68c0" />
</details>

### 4. 패키지 레이아웃(Code Block)

```
📂 modusplant
  │ 📜 ModusplantApplication.java
  ├─📂 domains          # 📋 핵심 비즈니스 로직 및 도메인 모델
  │  ├─📂 account       # 계정 (Email, Identity, Social, Normal)
  │  ├─📂 comment       # 댓글
  │  └─ 📂 ...          # 회원, 알림, 게시글, 약관
  ├─📂 framework        # ✈️ 외부 SW 연동 (jOOQ, Spring Data JPA, Spring Data Redis 등)
  ├─📂 infrastructure   # 🔨 공통의 관심사 및 인프라 (AOP, Config, Event, Security 등)
  └─📂 shared           # 🧺 전역 객체 Set (Constant, Exception, Kernel 등)
```

<details>
<summary> <strong>도메인 내부 구조</strong> </summary>
<br />

- domain -> 외부 계층 의존 ❌  
- usecase -> domain 의존 ⭕  
- adapter -> usecase 의존 ⭕  
- framework -> adapter 의존 ⭕

```
📂 [domain_name]
 ├─📂 adapter   # Interface Adapter: 오케스트레이션 (Controller)
 ├─📂 domain    # Business Logic: 핵심 비즈니스 로직 (Aggregate, Entity, VO)
 ├─📂 framework # Framework: 기술 구현체 (REST Controller, Repository)
 └─📂 usecase   # Specification: 애플리케이션 사양 (Port, Request / Response, Read / Write Model)
```

</details>

<br>

<a id="tech-stack"></a>
# 🛠 기술 스택
### 1. 백엔드 & 영속성
* **Spring Boot 3.x & Java 21:** 알림 기능의 비동기 처리에 가상 스레드 도입
* **PostgreSQL 17:** GIN 인덱스로 비정형 데이터(JSONB) 검색 성능 개선
* **Hybrid 접근 방식**
    * JPA / Hibernate: 영속성 컨텍스트 기반 단순 DML 작업, dirty checking으로 데이터 무결성 확보
    * jOOQ: 영속성이 불필요한 대규모 READ, 복잡한 집계 쿼리 최적화
* **Flyway:** DB 스키마 변경 히스토리 관리 및 CI / CD 파이프라인과 결합하여 환경 간 스키마 동기화

### 2. 인프라 & 모니터링
* **Redis:** 캐싱을 통한 조회 성능 개선 및 토큰 blacklist 관리로 보안 강화
* **LGTM Stack (Grafana, Loki, Prometheus, Tempo):** 소규모에 적합한 로그, 매트릭, 트레이싱 통합 관찰 환경 구축
* **Spring AOP & Logback:** 코드 수준에서 로깅을 핵심 로직과 분리하여 중앙집중적으로 관리

### 3. 보안 & 품질
* **Spring Security & JWT:** Stateless 인증 구조로 서버 응답성 확보 및 DB 조회 비용 절감
* **JaCoCo:** 테스트 커버리지 기반 코드 품질 관리
* **MapStruct:** boilerplate 매핑 로직 제거, 매핑 과정 단순화

### 4. 외부 서비스
* **Mailjet:** 인증 코드로 사용자 이메일 정보의 신뢰성 확보
* **OAuth 2.0 (Google/Kakao):** 사용자 로그인 편의성 제공 및 가입 장벽 낮춤

<br>

<a id="troubleshooting"></a>
# 💡 트러블슈팅

### ⭐ DDD + Clean Architecture를 통해 인프라/프레임워크로부터 비즈니스 로직 분리
* **문제**: 기능이 확장됨에 따라 클래스 간 의존성 파악을 위한 불필요한 소통 발생, 불명확한 구조로 유지보수 어려움
* **의사 결정**: 비즈니스 로직 + 인프라/프레임워크 + 그 사이를 매개하는 계층으로 아키텍처 리팩토링
* **성과**: 도메인들 간 명확한 Boundary 설정, Jira 백로그 기준 신규 기능 개발 기간 약 30% 단축

<details>
<summary> <strong>Flyway로 DB 형상 관리</strong> </summary>

- **문제**: 수동 DDL 공유로 환경 간 스키마 불일치 발생, 변경 히스토리 추적 어려움
- **의사 결정**: Flyway 도입으로 실행 시 스키마 자동 마이그레이션
- **성과**: 환경 간 스키마 정합성 문제 및 스키마 불일치 문제 방지

</details>

<details>
<summary> <strong>JPA의 N+1 문제 근절 및 대용량 데이터 조회 최적화</strong> </summary>

- **문제**: 여러 테이블이 JOIN되는 대량의 데이터 조회 시 JPA의 N+1이 발생하여 성능 저하
- **해결**
    - 단순 CRUD는 JPA/Hibernate로 데이터 무결성 확보
    - 복잡한 통계와 Read 작업은 jOOQ로 Type-Safe하게 수행 및 성능 최적화
- **성과**: JPA의 N+1문제 근절 및 쿼리 가독성 개선

</details>

<br>

<a id="api"></a>
# 📕 API 명세서

### 설계 원칙
* 일관된 응답 구조: status, code, data
* 에러 코드 기반 클라이언트 처리 가능 구조
* 버전 관리되는 REST URI (/api/v1/...)

### API 명세서
[모두의식물 API 명세서](https://resonant-tortellini-b95.notion.site/API-f0f2e2fc4ece8308bc998140c1335d60)

### 댓글 추가 API 예시

<details>
<summary> 요청 </summary>

- postId: 댓글이 속한 게시글 식별자 (ULID)
- path: 계층형 댓글 구조를 표현하는 경로 값 (숫자.숫자.숫자...)
- content: 댓글 내용

```json
{
  "postId": "string"
  "path": "string"
  "content": "string"
}
```

</details>

<details>
<summary> 응답 </summary>

- status: HTTP 상태 코드
- code: 클라이언트 분기 처리를 위한 응답 코드
- message: 사용자 또는 디버깅을 위한 메시지

```json
{
  "status": 200,
  "code": "generic_success",
  "message": ""
}
```

```json
{
  "status": 400,
  "code": "empty_post_id",
  "message": "게시글의 식별자 값이 비었습니다"
}
```

</details>

<br>

<a id="collaboration"></a>
# 🖊️ 협업 방식

### 협업 구조 & 효과
* Jira 기반 과업(백로그) 관리 + Confluence 기반 문서 아카이빙
* Gitmoji 기반 커밋 메시지 형식 통일 -> 코드 변경 이력 파악 시간 감소
* PR 기반 코드 리뷰로 토의 문화 정착 -> 코드 품질 향상

### 브랜치 전략
* Jira 과업 단위로 브랜치 생성 (ex. MP-123)
* 작업 완료 후 PR -> 코드 리뷰 -> develop 병합

### 커밋 컨벤션

<details>
<summary>Gitmoji 목록</summary>

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

</details>

```
MP-289 :bug: Fix: 게시글 여러 번 수정 시 발생하는 낙관적 락 에러 해결

- 낙관적 락 충돌 문제를 방지하기 위해 전체 매핑 후 저장 방식을 제거하고 개별 필드 업데이트로 변경
- 엔티티 업데이트 메서드 추가
```
