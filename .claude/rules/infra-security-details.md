---
paths:
  - "src/main/java/kr/modusplant/infrastructure/security/**"
---

# Security Infrastructure Conventions

Applies to `kr.modusplant.infrastructure.security` and its sub-packages.

---

## 1. Package Structure

```
security/
 ├─ config/       # Spring Security configuration — assembles the SecurityFilterChain bean and its supporting beans
 ├─ filter/       # Servlet filters inserted into the security filter chain
 ├─ handler/      # Spring Security callback handlers (login/logout success, failure, access-denied)
 ├─ models/       # Authentication principal, token, and login-request models
 ├─ enums/        # Security-specific error code enum
 ├─ exception/    # Authentication exception hierarchy
 └─ util/         # Static helper classes (logging, response writing)
```

---

## 2. Directory Patterns

**Config** (`config/`):
- A single `@Configuration` class assembles one `SecurityFilterChain` bean plus its supporting beans (password encoders, authentication manager, providers, handlers, filters, CORS source)

**Filter** (`filter/`):
- Extend `OncePerRequestFilter` for cross-cutting, always-run concerns (JWT parsing/verification, chain-wide exception translation), or `AbstractAuthenticationProcessingFilter` for a filter dedicated to intercepting one login endpoint
- Naming: `Jwt*Filter` for token-based components; plain descriptive `*Filter` otherwise (e.g. `EmailPasswordAuthenticationFilter`, `SecurityExceptionHandlingFilter`)

**Handler** (`handler/`):
- Each implements a single Spring Security handler interface; naming mirrors the interface (`*SuccessHandler`, `*FailureHandler`, `*LogoutHandler`)
- `WriteResponse*` prefix marks handlers that write a JSON body directly rather than redirecting

**Models** (`models/`):
- `Default*` prefix marks the normal-login (non-social-login) principal/token implementations (`DefaultAuthToken`, `DefaultUserDetails`)
- Login request DTOs use a plain `*Request` suffix (e.g. `NormalLoginRequest`)

**Enums** (`enums/`):
- `SecurityErrorCode` follows the shared `*ErrorCode implements ErrorCode` convention used across the codebase

**Exception** (`exception/`):
- All extend a single package-local root that itself extends Spring Security's `AuthenticationException`
- Naming describes the specific business condition (`Banned*`, `Inactive*`, `DisabledByLinking*`, `BadCredential*`, `AccountState*`)

**Util** (`util/`):
- Static-only helper classes with a private constructor; naming suffix `*Utils`

---

## 3. Custom Implementation Registry

Maps each custom component to the Spring Security type it implements or extends.

| Component Type                | Spring Security Parent                   | Custom Implementation                                        |
|-------------------------------|------------------------------------------|--------------------------------------------------------------|
| Authentication entry point    | `AuthenticationEntryPoint`               | `DefaultAuthenticationEntryPoint`                            |
| Authentication provider       | `AuthenticationProvider`                 | `DefaultAuthProvider`                                        |
| User details service          | `UserDetailsService`                     | `DefaultUserDetailsService`                                  |
| User details principal        | `UserDetails`                            | `DefaultUserDetails`                                         |
| Authentication token          | `AbstractAuthenticationToken`            | `DefaultAuthToken`                                           |
| Login processing filter       | `AbstractAuthenticationProcessingFilter` | `EmailPasswordAuthenticationFilter`                          |
| One-shot request filter       | `OncePerRequestFilter`                   | `JwtAuthenticationFilter`, `SecurityExceptionHandlingFilter` |
| Access denied handler         | `AccessDeniedHandler`                    | `DefaultAccessDeniedHandler`                                 |
| Logout handler                | `LogoutHandler`                          | `JwtClearingLogoutHandler`                                   |
| Logout success handler        | `LogoutSuccessHandler`                   | `WriteResponseLogoutSuccessHandler`                          |
| Login success handler         | `AuthenticationSuccessHandler`           | `WriteResponseLoginSuccessHandler`                           |
| Login failure handler         | `AuthenticationFailureHandler`           | `WriteResponseLoginFailureHandler`                           |
| Authentication exception root | `AuthenticationException`                | `BusinessAuthenticationException`                            |

**Naming note:** `DefaultAuthProvider` (a Spring Security `AuthenticationProvider` implementation) shares a short name with the unrelated `AuthProvider` enum used elsewhere for social-login provider identifiers.

---

## 4. Workflow & Filter Chain

### 4.1 Chain Scope & Filter Order

`SecurityConfig.defaultChain` registers the single `SecurityFilterChain`, matched by `securityMatcher("/api/**")`. The chain is stateless (no HTTP session), CSRF-disabled, and applies CORS through the `@Qualifier("corsConfigurationSource")` bean.

A matched request passes through these filters in order:

| # | Filter                              | Role                                                                                                                                                                                             |
|---|-------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | `CorsFilter`                        | Applies the profile-specific CORS configuration                                                                                                                                                  |
| 2 | `SecurityExceptionHandlingFilter`   | Wraps every downstream filter in a try/catch: `AuthenticationException` → `DefaultAuthenticationEntryPoint`; `BusinessException` → its error code; any other exception → `AUTHENTICATION_FAILED` |
| 3 | `LogoutFilter`                      | Matches `/api/auth/logout`; runs the logout handlers and ends the chain                                                                                                                          |
| 4 | `JwtAuthenticationFilter`           | Authenticates any request carrying an `Authorization` header                                                                                                                                     |
| 5 | `EmailPasswordAuthenticationFilter` | Matches `POST /api/auth/login`; performs normal login and ends the chain                                                                                                                         |
| 6 | `AnonymousAuthenticationFilter`     | Sets an anonymous authentication when the security context is still empty                                                                                                                        |
| 7 | `ExceptionTranslationFilter`        | Translates `AuthorizationFilter` denials into `DefaultAuthenticationEntryPoint` (anonymous caller) or `DefaultAccessDeniedHandler` (authenticated caller)                                        |
| 8 | `AuthorizationFilter`               | Applies the rules in §4.3                                                                                                                                                                        |

### 4.2 Request Paths

**Outside `/api/**`**
- Not matched by `securityMatcher`, so no filter of this chain runs.

**Login success** (`POST /api/auth/login`)
- `SecurityExceptionHandlingFilter` → `LogoutFilter` (no match) → `JwtAuthenticationFilter` → `EmailPasswordAuthenticationFilter` → `AuthenticationManager` → `DefaultAuthProvider` → `DefaultUserDetailsService` → `WriteResponseLoginSuccessHandler`
- `EmailPasswordAuthenticationFilter` deserializes and validates `NormalLoginRequest`, then passes an unauthenticated `DefaultAuthToken` to the manager. `DefaultUserDetailsService` loads `DefaultUserDetails` by email; `DefaultAuthProvider` checks the password (BCrypt), the linked social provider, and the banned/active state, then returns an authenticated `DefaultAuthToken`.
- `WriteResponseLoginSuccessHandler` updates the member's last login time, issues a token pair through `TokenService`, writes the access token in the JSON body and the refresh token as a `Set-Cookie` header. The chain ends here.

**Login failure** (`POST /api/auth/login`)
- `EmailPasswordAuthenticationFilter` → (`AuthenticationManager` → `DefaultAuthProvider` → `DefaultUserDetailsService`) → `WriteResponseLoginFailureHandler`
- Any `AuthenticationException` raised during the attempt reaches `WriteResponseLoginFailureHandler`, which writes the error code of a `BusinessAuthenticationException` or `AUTHENTICATION_FAILED` otherwise:
  - Request validation: `BAD_PASSWORD_FORMAT`, `BAD_EMAIL_FORMAT`, or `AUTHENTICATION_FAILED`
  - Lookup in `DefaultUserDetailsService`: `AccountStateException` (member auth or member not found)
  - Checks in `DefaultAuthProvider`: `BAD_PASSWORD`, `FORBIDDEN_GOOGLE_LINKED_ACCOUNT` / `FORBIDDEN_KAKAO_LINKED_ACCOUNT`, `BannedException`, `InactiveException`
- Exceptions that are not `AuthenticationException` (an unreadable request body, or a failure inside `WriteResponseLoginSuccessHandler`) propagate to `SecurityExceptionHandlingFilter` instead.

**Logout** (`/api/auth/logout`)
- `SecurityExceptionHandlingFilter` → `LogoutFilter` → `JwtClearingLogoutHandler` → `WriteResponseLogoutSuccessHandler`
- `LogoutFilter` runs before `JwtAuthenticationFilter`, so the access token is not validated on this path; `JwtClearingLogoutHandler` reads the `refreshToken` cookie and the `Authorization` header directly, removes the refresh token, and blacklists the access token. `WriteResponseLogoutSuccessHandler` writes a success body. The chain ends here.
- A missing cookie or header raises `BadCredentialException(BAD_CREDENTIALS)`, and a header without the `Bearer ` prefix raises `BadCredentialException(INVALID_TOKEN_FORMAT)` before the refresh token is removed; both go → `SecurityExceptionHandlingFilter` → `DefaultAuthenticationEntryPoint`.

**Request with an `Authorization` header** (any other endpoint)
- Valid token: `JwtAuthenticationFilter` → … → `AuthorizationFilter` → controller. `JwtAuthenticationFilter` builds `DefaultUserDetails` from the token claims (subject UUID, nickname, role) and stores an authenticated `DefaultAuthToken` in the security context.
- Invalid token: `JwtAuthenticationFilter` → `SecurityExceptionHandlingFilter` → `DefaultAuthenticationEntryPoint`. A header without the `Bearer ` prefix raises `INVALID_TOKEN_FORMAT`, a token that fails validation raises `EXPIRED_TOKEN`, and a blacklisted token raises `BLACKLISTED_TOKEN`, all as `BadCredentialException`. This applies to permit-all endpoints as well.

**No token, permit-all endpoint**
- `JwtAuthenticationFilter` (skipped) → `AnonymousAuthenticationFilter` → `AuthorizationFilter` (permits) → controller.

**No token, protected endpoint**
- `AnonymousAuthenticationFilter` → `AuthorizationFilter` (denies) → `ExceptionTranslationFilter` → `DefaultAuthenticationEntryPoint`, which writes `AUTHENTICATION_FAILED`.

**Authenticated caller without `ADMIN` on `/api/admin/**`**
- `JwtAuthenticationFilter` → `AuthorizationFilter` (denies) → `ExceptionTranslationFilter` → `DefaultAccessDeniedHandler`, which writes `ACCESS_DENIED`.

**Exceptions after the chain**
- Exceptions thrown from controllers and below are handled by `GlobalExceptionHandler` (`infrastructure/advice`), not by the security handlers above.

### 4.3 Authorization Rules

`AuthorizationFilter` evaluates these rules in order:
1. `/api/admin/**` → `hasAuthority('ADMIN')`
2. The permit-all map, keyed by `HttpMethod`: `SecurityConfig.PUBLIC_ENDPOINTS`, merged with `SecurityConfig.LOCAL_PUBLIC_ENDPOINTS` when the `local` profile is active
3. `anyRequest().authenticated()`

`SecurityExceptionHandlingFilter` and `ExceptionTranslationFilter` share `DefaultAuthenticationEntryPoint`; it and `DefaultAccessDeniedHandler` both write a `DataResponse`.

---

## 5. Self-Ownership Checks

`SecurityAssertionUtils.requireSelf(authenticatedUuid, targetUuid)` is a static helper (throws `NotAccessibleException` with `AUTHORIZATION_FAILED` on mismatch) that REST controllers call directly to enforce that a caller only accesses their own resource, complementing the path- and method-level authorization described above.
