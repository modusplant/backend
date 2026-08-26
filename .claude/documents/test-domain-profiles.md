# Test Domain Profiles

Each section's Group A / Group B lists are relative to
`src/main/java/kr/modusplant/domains/[domain]/` and mirrored 1:1 under
`src/test/java/kr/modusplant/domains/[domain]/common/util/`.

## search

- **ErrorCode class:** `SearchErrorCode`
- **Excluded-classes additions:** none beyond the common baseline (Enum, Exception, constructor-only)
- **Pure-Unit-Test path exceptions:** `framework/outbound/jpa/repository` (necessary to verify DB interaction)
- **jOOQ repository test policy:** `integration-test` — Spring context (`@SpringBootTest`) with a
  real `DSLContext` and seeded test data (via a test data helper); do not mock `DSLContext`. Only
  genuinely time-dependent behavior (e.g. `LocalDateTime.now()`) is controlled via
  `Mockito.mockStatic`. Applies to `framework/outbound/jooq/repository`.
  Evidence: `src/test/java/kr/modusplant/domains/search/framework/outbound/jooq/repository/SearchPostRepositoryJooqAdapterIntegrationTest.java`.
- **TestUtils shared constant paths:** `domains/search/common/constant`, `domains/post/common/constant`, `domains/comment/common/constant`
- **Group A (fields):** `domain/vo` (incl. `vo/nullobject`), `usecase/model/read`, `usecase/record`, `usecase/response`
- **Group B (methods):** `domain/aggregate`, `domain/entity`

## member

- **ErrorCode class:** `MemberErrorCode`
- **Excluded-classes additions:** jOOQ repository classes (excluded entirely, see policy below)
- **Pure-Unit-Test path exceptions:** `framework/outbound/jpa/entity` (if absolutely necessary to
  save data directly during testing), `framework/outbound/jpa/repository` (necessary to verify DB interaction)
- **jOOQ repository test policy:** `excluded` — no test is written for jOOQ repository classes in
  this domain. Evidence: no test file exists under
  `src/test/java/kr/modusplant/domains/member/framework/outbound/jooq/`; only a `jooq/record`
  TestUtils exists (`ActivitySubjectCommentIdRecordTestUtils.java`), which is a Group A record, not a repository test.
- **TestUtils shared constant paths:** `domains/member/common/constant`, `domains/post/common/constant`, `domains/comment/common/constant`
- **Group A (fields):** `domain/event`, `domain/vo` (incl. `vo/nullobject`), `framework/outbound/jooq/record`, `framework/outbound/jpa/compositekey`, `framework/outbound/jpa/entity/record` (records only), `usecase/model/read`, `usecase/record`, `usecase/request`, `usecase/response`
- **Group B (methods):** `domain/aggregate`, `domain/entity` (incl. `entity/nullobject`), `framework/outbound/jpa/entity` (excluding `entity/record`)

## comment

- **ErrorCode class:** `CommentErrorCode`
- **Excluded-classes additions:** none beyond the common baseline (Enum, Exception, constructor-only)
- **Pure-Unit-Test path exceptions:** `framework/outbound/jpa/entity` (uses `@RepositoryOnlyContext`
  + `TestEntityManager` to verify `@PrePersist` defaulting and JPA-managed behavior),
  `framework/outbound/jpa/repository` (necessary to verify DB interaction via `@RepositoryOnlyContext`)
- **jOOQ repository test policy:** `unit-test (jOOQ MockConnection/MockDataProvider)` — a pure
  unit test that builds its own `DSLContext` over jOOQ's `MockConnection`/`MockDataProvider`
  (`org.jooq.tools.jdbc`), asserting on bound parameters and returned `Result`s. No Spring
  context, and `DSLContext` itself is never Mockito-mocked. Applies to `framework/outbound/jooq/repository`.
  Evidence: `src/test/java/kr/modusplant/domains/comment/framework/outbound/jooq/CommentJooqRepositoryTest.java`
- **TestUtils shared constant paths:** `domains/comment/common/constant`, `domains/post/common/constant`, `domains/member/common/constant`
- **Group A (fields):** `domain/vo`, `domain/event`, `usecase/model`, `usecase/request`, `usecase/response`, `framework/inbound/web/cache/model`, `framework/outbound/jpa/compositekey` (builder-based but treated as a simple field type, matching member's own compositekey classification)
- **Group B (methods):** `domain/aggregate`, `framework/outbound/jpa/entity`