# 🪴ModusPlant
**DDD + Clean Architecture 기반으로 외부 환경 변화에 유연하게 대응하는 반려식물 플랫폼 서버**

### 핵심 가치
* **관심사 분리:** 계층별 엄격한 역할 분리로 인프라 및 프레임워크 변환 시 안정적 비즈니스 로직 유지
* **비용 최적화:** 비즈니스 요구사항에 최적화된 API와 기능 구현으로 개발 및 운영 효율 극대화
* **데이터 무결성:** Bean Validation과 도메인 검증, 엄격한 테스트를 통한 데이터 정합성 확보

| 팀원  | 역할 |
| ------------- |:-------------:|
| 박준희 | 일반 회원가입 + 댓글 Rest API, Spring Security 인증/인가, 전역적 예외 핸들링, 비속어 필터링 |
| 박준혁 | 회원 프로필 + 좋아요 + 신고 + 북마크 + 건의 및 버그 제보 Rest API, AWS 및 Vercel 배포/관리  |
| 송유정 | 소셜 로그인(OAuth 2.0), 게시글 + 게시글 1차/2차 카테고리 + 알림함 Rest API, JWT, 비동기 푸시알림, 클라우드 스토리지 관리 |
| 고동혁 | 회원 약관 Rest API, 타사 이메일 서비스 연계, 로깅, 모니터링, 개발 및 프로덕션 환경 운영 |


<br>

# 📝주요 기능
### 1. 인증 및 보안
* **사용자 인증 관리:** Spring Security 기반 로그인/회원가입 및 메일 기능(Mailjet) 연동을 통한 이메일 인증 구조
* **권한 인가 체계:** JWT의 Access/Refresh Token 기반 stateless 인증 시스템
* **소셜 로그인 연동:** OAuth 2.0을 통한 Google, Kakao 소셜 계정 기능
* **보안 및 정책 관리:** 회원 약관 동의 프로세스 및 사용자 프로필 관리 API

### 2. 컨텐츠 및 커뮤니티
* **게시 서비스:** 게시글 및 댓글의 CRUD 및 1차/2차 카테고리 분류 체계를 통한 구조적 데이터 관리
* **이미지 기반 소통:** 멀티파트 데이터 처리 및 Wasabi 스토리지 운용
* **사용자 활동:** 게시글/댓글 좋아요, 북마크를 통한 사용자 참여 기능

### 3. 고객 소통 및 운영
* **신고 시스템:** 서비스 가이드라인 준수를 위한 게시글 및 댓글 신고 API
* **피드백 루프:** 서비스 개선을 위한 건의사항 및 버그제보 창구 운영

<br>

# 🛠기술 스택 & 도입 이유
### 1. 백엔드 & 영속성
* **Spring Boot 3.x & Java 21:** 알림 기능의 신속한 처리를 위한 가상 스레드 도입
* **PostgreSQL 17:** 게시글 업로드용 이미지를 위한 JSONB, GIN INDEX를 통한 빠른 텍스트 검색
* **Hybrid DB 접근 방식**
    * JPA/Hibernate: 단순 DML, 영속성 맥락으로 dirty checking, SQL 생성 메서드로 편리한 쿼리 생성
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

### 설계 도면(Diagram)

<details>
<summary>1. 프로덕션 환경 아키텍처 </summary>
<br />
<img width="5887" height="5210" alt="프로덕션 아키텍처" src="https://github.com/user-attachments/assets/43603f19-95bf-48e7-8c97-7c6ef3ad6eef" />
</details>

<details>
<summary>2. 소스 코드 아키텍처 </summary>
<br />
<img width="2648" height="1490" alt="소스 코드 아키텍처" src="https://github.com/user-attachments/assets/732455f1-39a1-40d6-b563-f0b613205395" />
</details>

<details>
<summary>3. 데이터 모델링 </summary>
<br />
<img width="520" height="328" alt="게시글 테이블" src="https://github.com/user-attachments/assets/1c6a6d43-9662-44c3-a6c9-68fa268d1a87" />
<img width="517" height="175" alt="댓글 테이블" src="https://github.com/user-attachments/assets/44bee886-0dfa-401f-bc97-c630c1eb68c0" />
</details>

### 패키지 레이아웃(Code Block)

<details>
<summary>1. 전체 프로젝트 패키지 구조 </summary>
<br />

```
📂 modusplant
  │ 📜 ModusplantApplication.java
  ├─📂 domains          # 📋 핵심 비즈니스 로직 및 도메인 모델
  │  ├─📂 account       # 계정 (Email, Social, Normal)
  │  ├─📂 post          # 게시글
  │  └─ 📂 ...          # member, comment, term
  ├─📂 framework        # ✈️ 외부 기술 스택 연동 (JPA, jOOQ, Redis 등)
  ├─📂 infrastructure   # 🔨 애플리케이션 공통 기반 (Security, AOP, Config 등)
  └─📂 shared           # 🧺 전역 공통 모듈 및 유틸리티 (Kernel, Exception 등)
```

</details>

<details>
<summary>2. 도메인 내부 구조 </summary>
<br />

```
📂 [domain_name]
 ├─📂 adapter    # Interface Adapter: 외부 시스템 연결, 데이터 매핑 및 오케스트레이션
 ├─📂 domain     # Enterprise Business Rules: 핵심 비즈니스 로직 (Aggregate, Entity, VO)
 ├─📂 framework  # Frameworks & Drivers: 기술 구현체 (Service, RestController, Persistence)
 └─📂 usecase    # Application Business Rules: 앱 사양 정의 (Port, DTO, Model)
```

</details>

<details>
<summary>3. 설계 원칙 및 의도 </summary>
<br />

- **가벼운 도메인 객체:** 도메인 객체는 상태(State)와 자기 검증에 집중하며, 비즈니스 행위 및 오케스트레이션을 adapter 계층에 위임하여 모델의 순수성을 유지
- **실용적인 UseCase 계층:** 프로젝트의 낮은 복잡도를 고려하여 모든 로직을 클래스로 강제하지 않음. 대신 시스템 사양(Port)과 데이터 규격(DTO/Model)을 정의하는 인터페이스의 역할에 집중
- **프레임워크 격리:** 특정 기술 스택(Spring, Redis 등)에 의존하는 기능은 framework 계층으로 격리하여 외부 기술의 변화가 비즈니스에 미치는 영향 최소화

</details>

<br>

# 💡기술적 의사결정 및 트러블슈팅

### 기술적 의사결정

<details>
<summary> DDD + Clean Architecture를 통한 비즈니스와 인프라/프레임워크 분리 </summary>

- **문제**: 기능이 확장됨에 따라 클래스 간 의존성 파악을 위한 불필요한 소통 발생, 불명확한 구조로 유지보수 어려움
- **의사 결정**: 비즈니스 로직 + 인프라/프레임워크 + 그 사이를 매개하는 계층형 아키텍처로 리팩토링
- **성과**: 도메인들 간 명확한 경계 설정, Jira 백로그 기준 신규 기능 개발 기간 약 30% 단축

</details>

<details>
<summary> Flyway로 DB 형상 관리 </summary>

- **문제**: Slack을 통한 수동 DDL 공유로 팀원의 로컬 환경 간 스키마 불일치 및 히스토리 관리의 부재
- **의사 결정**: Flyway 도입으로 애플리케이션 실행 시 스키마 자동 동기화
- **성과**: DB 변경 히스토리를 통해 롤백 포인트 확보 및 배포 환경 간 정합성 문제 원천 차단

</details>

### 트러블슈팅

<details>
<summary> JPA의 N+1 문제 근절 및 대량 데이터 조회 최적화 </summary>

- **문제**: 여러 테이블이 JOIN되는 대량의 데이터 조회 시 JPA의 N+1 발생, 성능 저하
- **해결**
    - 단순 CRUD는 JPA/Hibernate로 데이터 무결성 확보
    - 복잡한 통계와 Read 작업은 jOOQ로 Type-Safe하게 수행 및 성능 최적화
- **성과**: JPA의 N+1문제 근절 및 컴파일 타임 에러 확인 가능

</details>

<br>

# 📕API 명세서
### 링크
[모두의식물 API 명세서](https://resonant-tortellini-b95.notion.site/API-f0f2e2fc4ece8308bc998140c1335d60)

### API 명세 구조

| 기능 | 우선순위 | MVP | URI | Method | 진행 상태 | 담당자 | 페이지(다중선택) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 댓글 추가 | 높음 | `1차` | /api/v1/communication/comments | `POST` | 로컬 환경 테스트 완료 | 준희 박 | 소통 컨텐츠 상세 페이지 |

### 요청 및 응답 형식

<details>
<summary> 요청 구조 </summary>

```json
{
  "postId": "string", // ulid
  "path": "string", // 숫자.숫자.숫자...
  "content": "string" // 댓글 내용
}
```

</details>

<details>
<summary> 응답 구조 </summary>

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

# 🖊️협업 방식
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
