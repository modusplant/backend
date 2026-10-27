---
paths:
  - "src/test/java/kr/modusplant/infrastructure/security/**"
---

# Security Infrastructure Test Conventions

Applies to every test class under `src/test/java/kr/modusplant/infrastructure/security/`.

---

## 1. Exception Assertions

Exception assertions check `getErrorCode()` against `SecurityErrorCode`.

---

## 2. Excluded Classes

- `config/SecurityConfig`

---

## 3. Pure Unit Test Path Exceptions

- `component/**`: uses `@SpringBootTest` + `@AutoConfigureMockMvc` + `@MockitoBean`. Evidence: `AuthorizationFlowTest`,
  `NormalLoginAuthenticationFlowTest`, `NormalLogoutFlowTest`.

---

## 4. Servlet Mock Pattern

Filters, handlers, and the entry point outside `component/**` stay pure-POJO:
`HttpServletRequest`/`HttpServletResponse`/`FilterChain` are inline-mocked via `Mockito.mock()`. Where a written
response body must be asserted, stub `response.getWriter()` to return a `PrintWriter` wrapping a `StringWriter` and
assert on the captured JSON.

---

## 5. Endpoint Authorization Coverage

`EndpointAuthorizationCoverageTest` (at the area test root) is a pure reflection/classpath-scan test with no Spring
context, so `@Profile` is not evaluated and a `LOCAL_PUBLIC_ENDPOINTS` entry counts as public regardless of the active
profile.

---

## 6. TestUtils

Paths below are relative to `src/main/java/kr/modusplant/infrastructure/security/` and mirrored 1:1 under
`src/test/java/kr/modusplant/infrastructure/security/common/util/`.

- **Shared constant paths:** `domains/member/common/constant`
- **Group A (fields):** `models` (`NormalLoginRequest`, `DefaultUserDetails`, `DefaultAuthToken`)
