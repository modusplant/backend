# Open Authentication Architecture

Architecture reference for how this project implements the OAuth 2.0 authorization-code flow;
consumed on demand. The backend implements the backend half of a hand-rolled OAuth 2.0 client — no
Spring Security OAuth2 support (`oauth2Login()`, `OAuth2AuthenticationToken`, `ClientRegistration`)
is used anywhere.

Applies to `domains/account/social/**` (Kakao/Google login, sign-up, linking, unlinking),
`infrastructure/security/**` (filter chain reuse), `infrastructure/jwt/**` (token issuance,
temp-token, blacklist), and `domains/member`'s `MemberSocialTranslator` touchpoint. The frontend
repository (`modusplant_frontend`) originates and completes part of this flow and is described
narratively below for workflow completeness; its details are not tracked by this repo's
`reflect-code-change-into-document` skill and may drift from what's written here.

---

## 1. OAuth 2.0 Components

### 1.1 Roles (RFC 6749 §1.1)

| Role                 | This project's realization                                                                                                                                                                                                                                   | Location                                                                                                                                                                                         |
|----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Resource owner       | The end user, who grants consent on the provider's consent screen                                                                                                                                                                                            | —                                                                                                                                                                                                |
| Client               | One client registration (`client_id`) per provider, split across two tiers: the frontend sends the authorization request and hosts the redirection endpoint; the backend performs the token request through a provider-specific client selected by a factory | `SocialAuthClient`, `SocialAuthClientFactory` — `domains/account/social/usecase/port/client`; `SocialAuthClientFactoryImpl`, `GoogleAuthClient`, `KakaoAuthClient` — `framework/outbound/client` |
| Authorization server | Kakao and Google (external); this project issues no OAuth tokens                                                                                                                                                                                             | —                                                                                                                                                                                                |
| Resource server      | Kakao API server only, reached solely to unlink the app from the user's Kakao account; no Google resource server (e.g. userinfo) is called, and the backend itself does not accept provider-issued tokens                                                    | `KakaoAuthClient.revokeAccess`                                                                                                                                                                   |

### 1.2 Protocol Endpoints (RFC 6749 §3)

| Endpoint               | Host                                                                      | Caller                                                          |
|------------------------|---------------------------------------------------------------------------|-----------------------------------------------------------------|
| Authorization endpoint | `kauth.kakao.com/oauth/authorize`, `accounts.google.com/o/oauth2/v2/auth` | Frontend (full-page redirect, no provider SDK)                  |
| Redirection endpoint   | Frontend page `{origin}/oauth/{provider}/callback`                        | Provider (redirect carrying `code` and `state`)                 |
| Token endpoint         | `oauth2.googleapis.com/token`, `kauth.kakao.com/oauth/token`              | `GoogleAuthClient.getTokenInfo`, `KakaoAuthClient.getTokenInfo` |
| Revocation endpoint    | `oauth2.googleapis.com/revoke` (Google only)                              | `GoogleAuthClient.revokeAccess`                                 |
| Unlink API (non-OAuth) | `kapi.kakao.com/v1/user/unlink` (Kakao resource server)                   | `KakaoAuthClient.revokeAccess`                                  |

---

## 2. OAuth 2.0 / OIDC Concepts and Conformance

| Concept                                                                | Spec reference                                | This implementation                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           | Status                           |
|------------------------------------------------------------------------|-----------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------|
| Client authentication                                                  | RFC 6749 §2.3                                 | Google token requests send `client_secret`; Kakao token requests send only `client_id` (no client authentication)                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | Implemented (provider-dependent) |
| `redirect_uri`                                                         | RFC 6749 §3.1.2, §4.1.3                       | The frontend derives it from the page origin (`{origin}/oauth/{provider}/callback`); the backend sends its configured `redirect_uri` on exchange and the provider enforces the match. The backend value is the `local-redirect-uri` override when the caller runs in local mode and the property is set, else `redirect-uri`; local mode is fixed per REST controller (`LocalSocialIdentityRestController` only, which the frontend targets via its `/api/v1/local/...` endpoints when `NEXT_PUBLIC_ENVIRONMENT=local`) or derived from the active `local` profile (`MemberSocialTranslator`) | Delegated to provider            |
| Scope                                                                  | RFC 6749 §3.3, OIDC Core §3.1.2.1             | Set by the frontend and not enforced server-side. Google: `email profile` without the `openid` value OIDC requires, although the backend depends on an `id_token`. Kakao: no `scope` parameter, so ID-token issuance depends on OpenID Connect being activated for the Kakao app                                                                                                                                                                                                                                                                                                              | Not conformant (Google)          |
| Authorization code grant                                               | RFC 6749 §4.1                                 | Frontend obtains the authorization code; backend exchanges it server-side                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | Implemented                      |
| Authorization error response                                           | RFC 6749 §4.1.2.1                             | The frontend callback page handles only `error=access_denied` (returns to login); other `error` values leave the page idle                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | Partially implemented            |
| Implicit grant                                                         | RFC 6749 §4.2 (legacy)                        | Not used; the flow is authorization-code only                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | Not applicable                   |
| Token response                                                         | RFC 6749 §5.1                                 | Only `access_token` and `id_token` are mapped (`SocialToken`); `token_type`, `expires_in`, `refresh_token`, and `scope` are discarded                                                                                                                                                                                                                                                                                                                                                                                                                                                         | Partially implemented            |
| Token error response                                                   | RFC 6749 §5.2                                 | On HTTP 400/401/500, `error`/`error_description` are parsed (`OAuthErrorResponse`) and rethrown as `OAuthRequestFailException`                                                                                                                                                                                                                                                                                                                                                                                                                                                                | Implemented                      |
| Provider refresh token                                                 | RFC 6749 §6                                   | Any provider refresh token in the token response is discarded; none is stored or used                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | Not implemented                  |
| CSRF/replay protections (`state`, PKCE, `nonce`)                       | RFC 6749 §10.12, RFC 7636, OIDC Core §3.1.2.1 | The frontend's `state` is a Base64-encoded JSON intent (`LOGIN`, `LINK`, `UNLINK`, `UNLINK_AND_SIGNOUT`, `SIGNOUT`) used only to route the callback, with no signature, randomness, or stored-value check (an unparsable value falls back to `LOGIN`); PKCE and `nonce` are absent on both frontend and backend                                                                                                                                                                                                                                                                               | Not implemented                  |
| Bearer access-token usage                                              | RFC 6750 §2.1                                 | The provider access token is sent as `Authorization: Bearer` only to the Kakao unlink API                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | Implemented (Kakao only)         |
| Token revocation                                                       | RFC 7009                                      | Google: revocation endpoint. Kakao: the unlink API, which disconnects the app from the user's account rather than revoking a single token                                                                                                                                                                                                                                                                                                                                                                                                                                                     | Implemented (Google only)        |
| ID token validation (signature, `iss`, `aud`, `exp`, `email_verified`) | OIDC Core §3.1.3.7, §5.1                      | `SocialIdTokenParser` decodes the ID token payload and reads `sub`, `email`, and the nickname claim (`nickname` for Kakao, `name` for Google) without verifying the signature or these fields                                                                                                                                                                                                                                                                                                                                                                                                 | Not implemented                  |
| `sub` as stable identifier                                             | OIDC Core §5.7                                | `sub` is stored as the provider identifier with a provider-specific length check, but existing accounts are looked up by the (unverified) `email` claim; `sub` is compared only after the email match                                                                                                                                                                                                                                                                                                                                                                                         | Partially implemented            |

---

## 3. Project-Wide OAuth 2.0 Authorization-Code Workflow

1. The frontend builds the provider's authorization URL itself, encoding the user's intent into
   `state`, and sends the browser there with a full-page redirect.
2. The provider redirects back to the frontend callback page with an authorization `code` and the
   `state` value.
3. The callback page decodes the intent and posts the `code` to the matching backend entry point:
   `LOGIN` → social login; `LINK` → settings linking; `UNLINK`/`UNLINK_AND_SIGNOUT` → settings
   unlinking; `SIGNOUT` → the code is held until the withdrawal request is submitted.
4. The backend's provider-specific client exchanges the `code` for tokens at the provider's token
   endpoint and parses the ID token into a normalized user-info value.
5. The backend classifies the result against existing account state into one of three outcomes —
   the account already exists and matches (login), the account exists as a non-social account
   (needs linking), or no account exists (needs signup). An account registered with the other
   provider is rejected after revoking the fresh provider access token.
6. A needs-signup/needs-link outcome issues a temp token, opening a short pending window; a login
   outcome issues a session access/refresh token pair directly.
7. The frontend routes to its social-signup page (`/signup/social`) and completes the pending window
   with a follow-up call that consumes the temp token:
   signup completion (`POST /api/v1/auth/social-signup`), link completion
   (`POST /api/v1/auth/social-link`), or abort (`DELETE /api/v1/auth/social-connect`, which revokes
   with the provider access token carried in the temp token). None of these obtains a new
   authorization code.
8. Independently of the login-time flow, an already-authenticated user can link or unlink a social
   account from account settings (`/api/v1/members/social/{provider}`); that path is Bearer-token
   gated rather than temp-token gated — a second, separate entry point into the same linking
   capability.
9. Settings-based linking/unlinking and account withdrawal each require a fresh authorization code,
   exchanged at request time, rather than reusing any previously obtained provider token.
10. Account withdrawal is the only path that reaches this flow from outside
    `domains/account/social`: the withdrawal request carries the code obtained with the `SIGNOUT`
    intent, and `domains/member`'s `MemberSocialTranslator` calls the social domain's
    `SocialIdentityLinkController` to exchange the fresh code for a provider access token, then to
    revoke it.

---

## 4. Project-Specific Mechanisms

- **Session issuance**: OAuth-originated sessions are minted by `TokenService`
  (`infrastructure/jwt/service`) through `JwtTokenProvider` and `JwtCookieProvider`
  (`infrastructure/jwt/provider`) — the same token format, signing key, issuer, and audience as
  password-based login.
- **Temp-token anatomy**: a JWS whose `sub` is the provider identifier, carrying `email`,
  `providerId`, `socialProvider`, and `socialAccessToken` claims (signed, not encrypted); 30-minute
  lifetime; cookie is `HttpOnly`/`Secure`/`SameSite=Lax`, scoped to `Path=/api/v1/auth`; carries no
  explicit token-type claim, and is never sent as an `Authorization` header, so the request-level
  JWT filter never evaluates it as an access token.
- **Access-token blacklist**: Redis-backed (`infrastructure/jwt/framework/outbound/redis`), checked
  by `JwtAuthenticationFilter` (`infrastructure/security`) on every authenticated request regardless
  of whether the session originated from OAuth or password login.
- **Filter chain integration**: OAuth endpoints are plain public or Bearer-authenticated entries in
  the security filter chain's endpoint configuration — there is no OAuth-specific filter logic.
- **Frontend token handling**: the frontend stores its access token in a frontend-managed,
  non-httpOnly cookie read out of the JSON response body (not a cookie set by the backend); the
  refresh token is a backend-managed, httpOnly cookie the frontend never reads directly, only
  relies on the browser to send automatically. The frontend server proxies `/api/*` to the backend
  and its requests use `credentials: 'include'`, so backend-set cookies (refresh token, temp token)
  are first-party to the browser.
- **Password login disabled once linked** (product policy): once an account is linked to a social
  provider, password-based login for that account is rejected server-side.
