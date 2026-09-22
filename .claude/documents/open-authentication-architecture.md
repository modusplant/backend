# Open Authentication Architecture

Architecture reference for how this project implements the OAuth 2.0 authorization-code flow;
consumed on demand. The backend is a hand-rolled OAuth 2.0 client — no Spring Security OAuth2
support (`oauth2Login()`, `OAuth2AuthenticationToken`, `ClientRegistration`) is used anywhere.

Applies to `domains/account/social/**` (Kakao/Google login, sign-up, linking, unlinking),
`infrastructure/security/**` (filter chain reuse), `infrastructure/jwt/**` (token issuance,
temp-token, blacklist), and `domains/member`'s `MemberSocialTranslator` touchpoint. The frontend
repository (`modusplant_frontend`) originates and completes part of this flow and is described
narratively below for workflow completeness; its details are not tracked by this repo's
`reflect-code-change-into-document` skill and may drift from what's written here.

---

## 1. OAuth 2.0 Components Implemented in the Project

| RFC/OIDC component             | This project's realization                                                                                                                       | Location                                                                                                                                           |
|--------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| Authorization server           | None — Kakao and Google are external authorization servers; this project implements the client role only                                         | —                                                                                                                                                  |
| Client                         | One client per provider, selected by a factory                                                                                                   | `SocialAuthClient` (port), `SocialAuthClientFactory`/`SocialAuthClientFactoryImpl` — `domains/account/social/usecase`, `framework/outbound/client` |
| Confidential vs. public client | `GoogleAuthClient` sends `client_secret`; `KakaoAuthClient` (public client) does not                                                             | `framework/outbound/client/GoogleAuthClient`, `KakaoAuthClient`                                                                                    |
| Token endpoint interaction     | Each client posts its own authorization-code-for-token request directly to the provider's token endpoint                                         | `GoogleAuthClient`, `KakaoAuthClient`                                                                                                              |
| `redirect_uri` resolution      | Selected per request from a profile flag (`isLocal`): a `local`-profile override property when set, else the configured prod/dev URI             | `GoogleAuthClient`, `KakaoAuthClient`; profile split also visible via a dedicated `LocalSocialIdentityRestController`                              |
| ID token consumption           | Decodes the JWT payload only, no verification — see §4 for the deviation this represents                                                         | `SocialIdTokenParser` — `framework/outbound/client`                                                                                                |
| Session/token issuance         | Downstream of a successful exchange, session tokens are minted through generic, non-OAuth-specific JWT infrastructure shared with password login | `TokenService`, `JwtTokenProvider`, `JwtCookieProvider` — `infrastructure/jwt/service`                                                             |
| Pending-state credential       | A short-lived, cookie-scoped temp token issued between the OAuth exchange and signup/link completion — full anatomy in §3                        | issued via the same `infrastructure/jwt` provider/cookie components                                                                                |
| Token revocation               | Each client calls its provider's own revoke endpoint                                                                                             | `GoogleAuthClient.revokeAccess`, `KakaoAuthClient.revokeAccess`                                                                                    |
| Access-token blacklist         | Backs logout/revocation for any issued session, OAuth-originated or not; checked on every request                                                | `infrastructure/jwt/framework/outbound/redis`, read by `JwtAuthenticationFilter` (`infrastructure/security`)                                       |

PKCE, CSRF `state` verification, `nonce` verification, and provider refresh-token storage are not
implemented anywhere in this project — see §4 for the full deviation table rather than repeating
it here.

---

## 2. Project-Wide OAuth 2.0 Authorization-Code Workflow

1. The frontend builds the provider's authorization URL itself (no provider SDK) and redirects the
   user to it, computing its own `redirect_uri`.
2. The provider redirects back to the frontend with an authorization `code` (and the frontend's own
   `state` value — a UX/routing intent payload, not a security control; see §4).
3. The frontend posts the `code` to a backend entry point.
4. The backend's provider-specific client exchanges the `code` for tokens directly against the
   provider's token endpoint and parses the (unverified) ID token into a normalized user-info value.
5. The backend classifies the result against existing account state into one of three outcomes —
   the account already exists and matches (login), the account exists as a non-social account
   (needs linking), or no account exists (needs signup).
6. A needs-signup/needs-link outcome issues a temp token, opening a short pending window; a login
   outcome issues a full access/refresh token pair directly through the shared JWT infrastructure.
7. The frontend completes the pending window with a follow-up call (signup completion, link
   completion, or an abort/abandon call) that consumes the temp token.
8. Independently of the login-time flow, an already-authenticated user can link or unlink a social
   account from account settings; that path is Bearer-token gated rather than temp-token gated —
   a second, separate entry point into the same linking/unlinking capability.
9. Every sensitive account-mutation path (login-time linking, settings-based linking, unlinking)
   re-authenticates with the provider by obtaining a fresh authorization code, rather than reusing
   any previously stored token.
10. Account withdrawal is the only path that reaches this flow from outside
    `domains/account/social`: `domains/member`'s `MemberSocialTranslator` delegates to the social
    domain's own controller to revoke the provider token, rather than performing its own exchange.

---

## 3. Cross-Cutting Technical Considerations

- **Shared JWT infrastructure**: OAuth-issued sessions use the exact same token provider, cookie
  provider, and signing key/issuer/audience as password-based login — there is no OAuth-specific
  token format.
- **Temp-token anatomy**: carries `email`, `providerId`, `socialProvider`, and `socialAccessToken`
  claims; 30-minute lifetime; cookie is `HttpOnly`/`Secure`/`SameSite=Lax`, scoped to
  `Path=/api/v1/auth`; carries no explicit token-type claim, and is never sent as an `Authorization`
  header, so the request-level JWT filter never evaluates it as an access token.
- **Access-token blacklist**: Redis-backed, checked on every authenticated request regardless of
  whether the session originated from OAuth or password login.
- **Filter chain integration**: OAuth endpoints are plain public or Bearer-authenticated entries in
  the security filter chain's endpoint configuration — there is no OAuth-specific filter logic.
- **Provider asymmetry**: Google is a confidential client (sends `client_secret` on token exchange);
  Kakao is a public client (does not).
- **Frontend token handling**: the frontend stores its access token in a frontend-managed,
  non-httpOnly cookie read out of the JSON response body (not a cookie set by the backend); the
  refresh token is a backend-managed, httpOnly cookie the frontend never reads directly, only
  relies on the browser to send automatically.
- **Two linking entry points**: login-time linking (temp-token gated, triggered when an existing
  non-social account matches the OAuth identity) and account-settings linking (Bearer gated,
  triggered by an already-authenticated user) are separate code paths into the same underlying
  linking capability.
- **Password login disabled once linked** (product policy, not OAuth-spec-driven): once an account
  is linked to a social provider, password-based login for that account is rejected server-side.

---

## 4. Notable Deviations from the OAuth 2.0 / OIDC Specification

| Topic                                                                  | Spec reference                                | This implementation                                                                                                                                                                      | Status                           |
|------------------------------------------------------------------------|-----------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------|
| Authorization code grant                                               | RFC 6749 §4.1                                 | Frontend obtains the authorization code; backend exchanges it server-side                                                                                                                | Implemented                      |
| Client authentication                                                  | RFC 6749 §2.3                                 | Google sends `client_secret`; Kakao (public client) sends none                                                                                                                           | Implemented (provider-dependent) |
| `redirect_uri` validation                                              | RFC 6749 §4.1.3                               | Backend sends its own configured `redirect_uri` on exchange; the provider enforces the match                                                                                             | Delegated to provider            |
| CSRF/replay protections (`state`, PKCE, `nonce`)                       | RFC 6749 §10.12, RFC 7636, OIDC Core §3.1.2.1 | The frontend sends a `state` value, but it is a UX/routing intent payload with no signature, randomness, or stored-value check; PKCE and `nonce` are absent on both frontend and backend | Not implemented                  |
| Scope                                                                  | RFC 6749 §3.3                                 | Google scope (`email profile`) is fixed by the frontend; not enforced server-side                                                                                                        | Delegated to frontend            |
| ID token validation (signature, `iss`, `aud`, `exp`, `email_verified`) | OIDC Core §3.1.3.7, §5.1                      | The backend decodes the ID token payload and reads claims without verifying the signature or these fields                                                                                | Not implemented                  |
| `sub` as stable identifier                                             | OIDC Core §5.7                                | Used as the provider identifier, with a provider-specific format check                                                                                                                   | Implemented                      |
| Token revocation                                                       | RFC 7009                                      | Each provider client calls the provider's revoke endpoint                                                                                                                                | Implemented                      |
| Provider refresh token                                                 | RFC 6749 §6                                   | No provider refresh token is requested or stored                                                                                                                                         | Not implemented                  |
| Implicit grant                                                         | RFC 6749 §4.2 (legacy)                        | Not used; the flow is authorization-code only                                                                                                                                            | Not applicable                   |
