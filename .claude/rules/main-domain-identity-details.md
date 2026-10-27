---
paths:
  - "src/main/java/kr/modusplant/domains/account/identity/**"
---

# Identity Domain Conventions

Applies to `kr.modusplant.domains.account.identity` and its sub-packages.

---

## 1. Package Structure

```
identity/
 ├─ usecase/
 │   ├─ port/repository/              # Repository port interfaces
 │   └─ response/                     # Response DTOs (Java records)
 ├─ adapter/
 │   └─ controller/                   # Business orchestration
 └─ framework/
     ├─ inbound/web/rest/             # @RestController (HTTP entry point)
     └─ outbound/
         ├─ jooq/                     # DSLContext-based port implementations
         └─ jpa/
             ├─ entity/               # @Entity classes for the member-auth table
             └─ repository/           # Spring Data JPA interfaces
```

---

## 2. usecase/ Patterns

**Repository Ports** (`usecase/port/repository/`):
- Parameters use the account-shared `AccountId` VO; a read port may return a response DTO directly, with `null` for an absent row

**Response DTOs** (`usecase/response/`):
- Field types: String, `AuthProvider`, LocalDate

---

## 3. adapter/ Patterns

**Controller** (`adapter/controller/`) — `@Service @RequiredArgsConstructor`:
- Receives a raw `UUID`, converts it to `AccountId`, calls the repository port, returns the response DTO
- Throws `NotFoundValueException` with an `AccountErrorCode` when the port returns `null`

---

## 4. framework/ Patterns

**REST Controller** (`framework/inbound/web/rest/`) — `@RestController @RequestMapping("/api") @RequiredArgsConstructor @Validated`:
- Validates path variables and wraps responses in `DataResponse`
- Member-scoped endpoints carry `@PreAuthorize("isAuthenticated()")`, take the caller via `@AuthenticationPrincipal(expression = "uuid")` with `@Parameter(hidden = true)`, and call `SecurityAssertionUtils.requireSelf(callerUuid, memberUuid)` before delegating to the adapter Controller
- Endpoints handled by the Spring Security filter chain (e.g. logout) are declared as empty dummy methods documented with Javadoc

**jOOQ Repository** (`framework/outbound/jooq/`) — `@Repository @RequiredArgsConstructor`; implements the usecase port:
- Holds generated table references as fields (e.g. `SiteMemberAuth.SITE_MEMBER_AUTH`, `SiteMember.SITE_MEMBER`)
- Joins the member-auth and member tables on their shared UUID and maps the record straight into the response DTO inside `fetchOne(...)`, converting the stored provider string with `AuthProvider.valueOf`

**JPA Entity** (`framework/outbound/jpa/entity/`) — `@Entity @EntityListeners(AuditingEntityListener.class) @Table @NoArgsConstructor(AccessLevel.PROTECTED) @Getter @ToString(onlyExplicitlyIncluded = true)`:
- Table and audit-column names come from `TableName` / `TableColumnName` constants
- Primary key shared with the member entity: `@MapsId` + `@OneToOne(fetch = LAZY, optional = false)` with `foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)`
- `@ToString.Include` only on non-sensitive fields, so email and password stay out of `toString()`
- Optimistic locking via `@Version versionNumber`
- Exposes conditional-GET helpers (an ETag source built from the UUID and version number, a last-modified timestamp truncated to seconds)
- Exposes intent-named `update*` mutation methods rather than public setters
- `equals`/`hashCode` over the associated member via Apache Commons `EqualsBuilder`/`HashCodeBuilder`
- Manual static nested builder alongside the Lombok-generated accessors, including a copy-from-existing-instance method

**JPA Repository** (`framework/outbound/jpa/repository/`) — `@Repository @Primary`; extends the shared `LastModifiedAtRepository` and `UuidPrimaryKeyRepository` base interfaces plus `JpaRepository`:
- Derived query methods keyed by email, provider, provider ID, and member entity (`findBy*` returning `Optional`/`List`, `existsBy*` returning `boolean`)
