---
paths:
  - "src/main/java/kr/modusplant/domains/account/identity/**"
---

# Identity Domain Conventions

## 1. Package Structure

```
identity/
├ usecase/
│├ port/repository/
│└ response/
├ adapter/
│└ controller/
└ framework/
 ├ inbound/web/rest/
 └ outbound/
  ├ jooq/
  └ jpa/
   ├ entity/ # Member-auth table
   └ repository/
```

---

## 2. usecase/ Patterns

**Repository Ports**:
- Parameters use the account-shared `AccountId` VO; a read port may return a response DTO directly, with `null` for an
  absent row

---

## 3. adapter/ Patterns

**Controller**:
- Throws `NotFoundValueException` with an `AccountErrorCode` when the port returns `null`

---

## 4. framework/ Patterns

**jOOQ Repository**:
- Builds the auth-info view by joining the member-auth and member tables on their shared UUID, converting the stored
  provider string with `AuthProvider.valueOf`

**JPA Entity**:
- The member-auth entity shares the member entity's primary key: `@MapsId` + `@OneToOne(fetch = LAZY, optional = false)`
- Exposes conditional-GET helpers: an ETag source built from the UUID and the `@Version` number, and a last-modified
  timestamp truncated to seconds

**JPA Repository**:
- Lookups are keyed by email, provider, provider ID, and member entity
