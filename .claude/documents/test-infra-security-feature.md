# Test Infra Security - Feature

The Group A list below is relative to `src/main/java/kr/modusplant/infrastructure/security/` and
mirrored 1:1 under `src/test/java/kr/modusplant/infrastructure/security/common/util/`.

- **ErrorCode class:** `SecurityErrorCode`
- **Excluded-classes additions:** `config/SecurityConfig` (pure bean-assembly `@Configuration`
  class; no `infrastructure/config/*Config` class in this codebase carries a dedicated test)
- **Pure-Unit-Test path exceptions:** `component/**` uses `@SpringBootTest` +
  `@AutoConfigureMockMvc` + `@MockitoBean` — there is no pure-POJO way to exercise the assembled
  `SecurityFilterChain` (JWT filter, exception translation, login/logout, authorization) end to
  end. Evidence: `AuthorizationFlowTest`, `NormalLoginAuthenticationFlowTest`,
  `NormalLogoutFlowTest`.
- **Servlet mock pattern:** filters/handlers/entry point outside `component/**` stay pure-POJO —
  `HttpServletRequest`/`HttpServletResponse`/`FilterChain` are inline-mocked via `Mockito.mock()`;
  where a written response body must be asserted, stub `response.getWriter()` to return a
  `PrintWriter` wrapping a `StringWriter` and assert on the captured JSON.
- **TestUtils shared constant paths:** `domains/member/common/constant`
- **Group A (fields):** `models` (`NormalLoginRequest`, `DefaultUserDetails`, `DefaultAuthToken` —
  each constructed once via constructor/builder and never mutated afterward)
