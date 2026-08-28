# Test Domain Profile: search

The Group A / Group B lists below are relative to
`src/main/java/kr/modusplant/domains/search/` and mirrored 1:1 under
`src/test/java/kr/modusplant/domains/search/common/util/`.

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
