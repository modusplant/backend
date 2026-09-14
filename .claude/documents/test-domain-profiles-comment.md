# Test Domain Profile: comment

The Group A / Group B lists below are relative to
`src/main/java/kr/modusplant/domains/comment/` and mirrored 1:1 under
`src/test/java/kr/modusplant/domains/comment/common/util/`.

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
- **Redis repository test policy:** `unit-test (mocked StringRedisTemplate)` — a pure unit
  test that Mockito-mocks `StringRedisTemplate` and the `ValueOperations` returned by
  `opsForValue()`, stubs `setIfAbsent` / `increment` / `expire`, and asserts on the composed
  key strings, the counter set value, and the TTL. No Spring context. Applies to
  `framework/outbound/redis`.
- **TestUtils shared constant paths:** `domains/comment/common/constant`, `domains/post/common/constant`, `domains/member/common/constant`
- **Group A (fields):** `domain/vo`, `domain/event`, `usecase/model`, `usecase/request`, `usecase/response`, `framework/inbound/web/cache/model`, `framework/outbound/jpa/compositekey` (builder-based but treated as a simple field type)
- **Group B (methods):** `domain/aggregate`, `framework/outbound/jpa/entity`
