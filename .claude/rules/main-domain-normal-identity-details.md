---
paths:
  - "src/main/java/kr/modusplant/domains/account/normal/**"
---

# Normal Identity Domain Conventions

Applies to `kr.modusplant.domains.account.normal` and its sub-packages.

---

## 1. Package Structure

```
normal/
├ domain/
│├ vo/
│└ exception/enums/
├ usecase/
│├ port/repository/ # Create/Read/Update ports
│├ port/mapper/ # Request → domain mapper interface
│└ request/
├ adapter/
│├ controller/
│└ mapper/
└ framework/
 ├ inbound/web/rest/
 └ outbound/persistence/
  ├ jooq/ # Read port implementation
  └ jpa/
   ├ mapper/ # MapStruct Domain → JPA Entity mappers
   └ repository/ # *RepositoryJpaAdapter (write port implementation)
```

---

## 2. domain/ Patterns

**Value Object** (`domain/vo/`):
- A VO with its own format rules exposes a `public static validateSource(String)` called by `create`, which throws the
  shared `EmptyValueException` for null/blank input and the shared `InvalidValueException` for a mismatch with a `Regex`
  constant, each with a `NormalIdentityErrorCode` and the field name
- A composite VO built from shared kernel VOs (`Email`, `Password`, `Nickname`) exposes separate factories for raw
  strings (`createWithString`) and for already-built VOs (`createWithDomain`)
- An aggregate-style VO (e.g. sign-up data) takes every raw input in one `create(...)` factory and builds each
  constituent VO itself
- Equality covers the identifying fields only

---

## 3. usecase/ Patterns

**Repository Ports** (`usecase/port/repository/`):
- Split by operation into `*CreateRepository`, `*ReadRepository`, and `*UpdateRepository`, implemented by different
  framework technologies (jOOQ for reads, JPA for writes)
- Parameters use domain VOs (account-shared `AccountId`, shared kernel `Email`/`Password`/`Nickname`, domain VOs);
  return types are domain VOs, `AuthProvider`, `boolean`, or a raw `String` for a stored password hash

**Mapper Port** (`usecase/port/mapper/`):
- Declares request DTO → domain VO construction

---

## 4. adapter/ Patterns

**Controller** (`adapter/controller/`) — injects `@Qualifier("bcryptPasswordEncoder") PasswordEncoder` through an
explicit constructor; write methods are `@Transactional`:
- Sign-up rejects an existing email with a conflict error code chosen by the stored `AuthProvider` of that account, then
  rejects a duplicate nickname, before saving the mapped sign-up data
- Email/password modification verifies, in order, that the account exists, (for email) that its provider is `BASIC`, and
  that the submitted current value matches the stored one (the password through `PasswordEncoder.matches`), before
  calling the update port
- Throws shared exceptions (`ExistsEntityException`, `ExistsValueException`, `NotFoundEntityException`,
  `InvalidValueException`), parameterized with error codes from this or other domains (`NormalIdentityErrorCode`,
  `SecurityErrorCode`, `KernelErrorCode`, `GeneralErrorCode`)

---

## 5. framework/ Patterns

**REST Controller** (`framework/inbound/web/rest/`):
- Each mapping carries its full path (no class-level `@RequestMapping`)

**jOOQ Repository** (`framework/outbound/persistence/jooq/`):
- Single-column lookups convert the result into a domain VO or `AuthProvider` and return `null` when the row is absent

**JPA Mapper** (`framework/outbound/persistence/jpa/mapper/`) — MapStruct `@Mapper` interfaces, one per target JPA
entity:
- Every method uses `@BeanMapping(ignoreByDefault = true)` and maps each field explicitly with `@Mapping`, reading VO
  values through `source = "...value"` paths
- Takes the already-saved member entity as a source parameter for entities that reference it; fixed values use
  `expression = "java( ... )"` (e.g. `AuthProvider.BASIC`)

**Repository Adapter** (`framework/outbound/persistence/jpa/repository/`) — injects
`@Qualifier("bcryptPasswordEncoder") PasswordEncoder` through an explicit constructor; implements the create and update
ports:
- Sign-up saves the member entity first, then the member-auth, member-term, and member-profile entities built from it
  through the JPA mappers; the raw password is encoded here and passed to the member-auth mapper
- Updates load the member and its member-auth entity (throwing `NotFoundEntityException` when absent), apply the
  entity's intent-named `update*` methods (encoding a new password first), then save
