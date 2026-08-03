---
paths:
  - "src/main/java/kr/modusplant/infrastructure/security/**"
---

> Supplements CLAUDE.md § Architecture — the `infrastructure` package's security concern. Assumes familiarity with the top-level package roles described there.

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
- Static-only helper classes with a private constructor; naming suffix `Logger` / `*Utils`

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

**Naming note:** `DefaultAuthProvider` (a Spring Security `AuthenticationProvider` implementation) shares a short name with the unrelated `AuthProvider` enum used elsewhere for social-login provider identifiers. Same short name, different concern — do not conflate them by name alone.

---

## 4. Workflow & Filter Chain

One `SecurityFilterChain` is registered, scoped to a single API path prefix. A request passes through custom filters in this order before reaching Spring's standard chain:

1. **Chain-wide exception translation filter** — wraps every downstream filter in a try/catch. Any exception raised further down (authentication failures, business exceptions, or anything uncaught) is caught here and converted into a structured JSON error response, delegating to the custom authentication entry point for authentication-type failures.
2. **JWT authentication filter** — opportunistically authenticates every request carrying a bearer token: validates the token, checks a blacklist, and populates the security context when valid. Requests without a token, or with an invalid one, either proceed unauthenticated or raise an exception that is caught by step 1.
3. **Login processing filter** — matches only the dedicated login endpoint. Deserializes and validates the login request, then delegates credential verification to the authentication manager, which resolves to the custom authentication provider and, in turn, the custom user details service. The outcome is routed to a dedicated success or failure handler that writes the response body (and, on success, issues tokens).
4. **Standard Spring Security filters** take over afterward:
   - An authorization filter enforces path-based access rules, grouped by intent (admin-only routes, authenticated-only routes, and public/permit-all routes) rather than by literal endpoint list.
   - A logout filter, matched to a dedicated logout endpoint, delegates token revocation to the custom logout handler and response writing to the custom logout success handler.
   - The exception-translation stage falls back to the same custom entry point and access-denied handler used in step 1, so error response shape stays consistent whether an exception is caught early by the custom filter or later by the standard chain.

The chain is stateless (no HTTP session) and CSRF-disabled, consistent with a bearer-token-only API.
