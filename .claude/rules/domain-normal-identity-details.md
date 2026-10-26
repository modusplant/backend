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
 ├─ domain/
 │   ├─ vo/                                  # Value Objects
 │   └─ exception/                           # Domain-local exception classes
 │       └─ enums/                           # Error code enums
 ├─ usecase/
 │   ├─ port/repository/                     # Create/Read/Update repository port interfaces
 │   ├─ port/mapper/                         # Request → domain mapper interface
 │   └─ request/                             # Inbound REST DTOs (Bean Validation)
 ├─ adapter/
 │   ├─ controller/                          # Business orchestration
 │   └─ mapper/                              # Mapper port implementation
 └─ framework/
     ├─ inbound/web/rest/                    # @RestController (HTTP entry point)
     └─ outbound/persistence/
         ├─ jooq/                            # DSLContext-based read port implementation
         └─ jpa/
             ├─ mapper/                      # MapStruct Domain → JPA Entity mappers
             └─ repository/                  # *RepositoryJpaAdapter (write port implementation)
```

---

## 2. domain/ Patterns

**Value Object** (`domain/vo/`) — `@Getter @AllArgsConstructor(AccessLevel.PRIVATE)`:
- `static create(...)` factory; a VO with its own format rules exposes a `public static validateSource(String)` called by `create`, which throws a domain-local exception for null/blank input and for a mismatch with a `Regex` constant
- A composite VO built from shared kernel VOs (`Email`, `Password`, `Nickname`) exposes separate factories for raw strings (`createWithString`) and for already-built VOs (`createWithDomain`)
- An aggregate-style VO (e.g. sign-up data) takes every raw input in one `create(...)` factory and builds each constituent VO itself
- `equals`/`hashCode` via Apache Commons `EqualsBuilder`/`HashCodeBuilder`, over the identifying fields only

**Domain Exceptions** (`domain/exception/`):
- Domain-local exception classes extending the shared `BusinessException`, each with an `(ErrorCode)` and an `(ErrorCode, String message)` constructor

**Error Codes** (`domain/exception/enums/`):
- `*ErrorCode implements ErrorCode` enum with three fields: `httpStatus(int)`, `code(String)`, `message(String)`; HTTP status taken from `HttpStatus.*.value()`

---

## 3. usecase/ Patterns

**Repository Ports** (`usecase/port/repository/`):
- Split by operation into `*CreateRepository`, `*ReadRepository`, and `*UpdateRepository`, implemented by different framework technologies (jOOQ for reads, JPA for writes)
- Parameters use domain VOs (account-shared `AccountId`, shared kernel `Email`/`Password`/`Nickname`, domain VOs); return types are domain VOs, `AuthProvider`, `boolean`, or a raw `String` for a stored password hash

**Mapper Port** (`usecase/port/mapper/`):
- Declares request DTO → domain VO construction

**Request DTOs** (`usecase/request/`):
- Java records carrying Bean Validation annotations (`@NotBlank`, `@Pattern`) and Swagger `@Schema`; patterns reference `Regex.REGEX_*` constants
- Override `toString()` to mask personal data (`MaskUtils.maskEmail`, `MaskUtils.maskAnyString`)

---

## 4. adapter/ Patterns

**Controller** (`adapter/controller/`) — `@Service`, explicit constructor injecting `@Qualifier("bcryptPasswordEncoder") PasswordEncoder`; write methods are `@Transactional`:
- Receives request DTOs and a raw `UUID`, converts them to domain VOs, calls the repository ports
- Sign-up rejects an existing email with a conflict error code chosen by the stored `AuthProvider` of that account, then rejects a duplicate nickname, before saving the mapped sign-up data
- Email/password modification verifies, in order, that the account exists, (for email) that its provider is `BASIC`, and that the submitted current value matches the stored one (the password through `PasswordEncoder.matches`), before calling the update port
- Throws shared exceptions (`ExistsEntityException`, `NotFoundEntityException`, `InvalidValueException`) or domain-local ones, parameterized with error codes from this or other domains (`NormalIdentityErrorCode`, `SecurityErrorCode`, `KernelErrorCode`, `GeneralErrorCode`)

**Mapper** (`adapter/mapper/`) — `@Component`; implements the mapper port, building the domain VO from the raw request values

---

## 5. framework/ Patterns

**REST Controller** (`framework/inbound/web/rest/`) — `@RestController @Slf4j @RequiredArgsConstructor @Validated`:
- Each mapping carries its full path (no class-level `@RequestMapping`); request bodies use `@RequestBody @Valid`; responses wrap in `DataResponse`
- Member-scoped endpoints carry `@PreAuthorize("isAuthenticated()")`, take the caller via `@AuthenticationPrincipal(expression = "uuid")` with `@Parameter(hidden = true)`, and call `SecurityAssertionUtils.requireSelf(callerUuid, memberUuid)` before delegating to the adapter Controller
- Endpoints handled by a Spring Security filter (e.g. email-password login) are declared as empty dummy methods documented with Javadoc
- Swagger: `@Tag`, `@Operation`, `@Parameter`, `@Schema`

**jOOQ Repository** (`framework/outbound/persistence/jooq/`) — `@Repository`, explicit constructor; implements the read port with `DSLContext`:
- Holds generated table references as fields (`SiteMemberAuth.SITE_MEMBER_AUTH`, `SiteMember.SITE_MEMBER`)
- Single-column lookups via `fetchOne(...)`, converting the result into a domain VO or `AuthProvider` and returning `null` when the row is absent; existence checks via `dsl.fetchExists(dsl.selectOne()...)`

**JPA Mapper** (`framework/outbound/persistence/jpa/mapper/`) — MapStruct `@Mapper` interfaces, one per target JPA entity:
- Every method uses `@BeanMapping(ignoreByDefault = true)` and maps each field explicitly with `@Mapping`, reading VO values through `source = "...value"` paths
- Takes the already-saved member entity as a source parameter for entities that reference it; fixed values use `expression = "java( ... )"` (e.g. `AuthProvider.BASIC`)

**Repository Adapter** (`framework/outbound/persistence/jpa/repository/`) — `@Repository`, explicit constructor injecting `@Qualifier("bcryptPasswordEncoder") PasswordEncoder`; implements the create and update ports:
- Sign-up saves the member entity first, then the member-auth, member-term, and member-profile entities built from it through the JPA mappers; the raw password is encoded here and passed to the member-auth mapper
- Updates load the member and its member-auth entity (throwing `NotFoundEntityException` when absent), apply the entity's intent-named `update*` methods (encoding a new password first), then save