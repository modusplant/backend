---
paths:
  - "src/test/**"
---

# Test Architecture Details

Applies to every test class under @src/test/.

This rule is the complete baseline for every test in the tree. A task-specific workflow may
supply a narrower, pre-resolved set of paths and names for one area; when it does, those values
win for that run, and nothing here depends on such a workflow being present.

---

## 1. Pure Unit Test Baseline

Unit tests must maintain a pure POJO state. **Do not use** Spring Context (`@SpringBootTest`,
`@WebMvcTest`) or Mockito Extension (`@ExtendWith(MockitoExtension.class)`), except where a
task-specific workflow supplies explicit path exceptions for the area under test. Absent such
exceptions, every class — services, adapters, controllers, mappers, value objects, entities — is
a pure POJO test.

---

## 2. Mocking Strategy

All dependent classes, except for the explicit instance containing the method under test, must
be mocked using inline mocking via `Mockito.mock()`. Do not use `@Mock` or `@InjectMocks`
annotations.

---

## 3. REST Controller Unit Test

Covers unit tests that verify method calls, return values, and exceptions by injecting mock
dependencies into the controller instance, without using MockMvc.

---

## 4. Test Method Naming Convention

- **Format:** `testMethodName_givenCondition_willDoAction`
- **Conciseness:** Should not be overly verbose. Use clear and simple language that gets to the heart of the matter.
- **Will-Clause Rules:**
  - **Case 1: No return value (Void):** `..._willProcessAction` (e.g., `willReportAbuse`, `willVerifyRequest`)
  - **Case 2: Return value exists:** `..._willReturnResponse` or `..._willReturnReadModel` (Specify the concrete return type name)
    - The return descriptor may be a concrete type name (`..._willReturnString`,
      `..._willReturnOptional`, `..._willReturnTokenPair`) or a primitive value the method
      returns (`..._willReturnTrue`, `..._willReturnFalse`). Do not use a vague noun that is
      neither (`..._willReturnInfo`, `..._willReturnRepresentative`).
  - **Case 3: Exception occurs:** `..._willThrowException` (Fixed format)

### Grouping Constructs

`@Nested` classes (and any other grouping construct) organize scenarios; they never relax the
rules for the leaf tests. Every `@Test` / `@ParameterizedTest` method inside a `@Nested` class
follows the method-naming, display-name, and body conventions above exactly as a top-level test
method does.

---

## 5. Test Method Display Name Convention

Korean is exceptionally allowed within this section's display-name values, and nowhere else.

- **Coherence:** Must share the same context with the method name. Should not include any additional information beyond what the method name implies.
- **Interpretation Rules For The Will-Clause on Method Names:**
  - **Case 1:** `..._willProcessAction` -> `활동 수행`
  - **Case 2:** `..._willThrowException` -> `예외 반환`
  - **Case 3:** `..._willReturn<Type>` -> `<Type> 반환`

---

## 6. Test Body Convention (BDD Style)

Strictly adhere to the `given-when-then` pattern using `BDDMockito`.

- **given:** Define one-time objects required for testing and setup stubbing using `given()`.
- **when:** Execute the specific method under test (assign the return value to a variable if present).
- **then:**
    - Use `Mockito.verify()` to verify that the mock object's methods were called with correct arguments.
    - For exception testing, thoroughly verify that the exception code returned by `getErrorCode()`
      — the `*ErrorCode` enum the class under test actually throws — matches the expected value.
      If the thrown exception is a raw `RuntimeException` or a JDK exception with no
      `getErrorCode()` contract, assert the exception type and, where it carries meaning, its
      message instead.

---

## 7. Test Utility (`TestUtils`) Convention

To prevent code duplication, highly encourage reusing or creating Test Utility classes when
instantiating domain objects or models.

### Path Mapping Rule

A test helper lives under the *area root* of the class it supports, mirroring the main-source
sub-path (persistence / framework-plumbing segments such as `framework/outbound/jpa` or `models`
may be flattened to their leaf, e.g. `entity`):

| Class under test (`src/main/java/kr/modusplant/...`) | Area root                | TestUtils (`src/test/java/kr/modusplant/...`)                             |
|------------------------------------------------------|--------------------------|---------------------------------------------------------------------------|
| `domains/[DOMAIN]/[SUB_PATH]/[ClassName].java`       | `domains/[DOMAIN]`       | `domains/[DOMAIN]/common/util/[SUB_PATH]/[ClassName]TestUtils.java`       |
| `infrastructure/[AREA]/[SUB_PATH]/[ClassName].java`  | `infrastructure/[AREA]`  | `infrastructure/[AREA]/common/util/[SUB_PATH]/[ClassName]TestUtils.java`  |
| `shared/[AREA]/[SUB_PATH]/[ClassName].java`          | `shared/[AREA]`          | `shared/[AREA]/common/util/[SUB_PATH]/[ClassName]TestUtils.java`          |
| `shared/framework/[LIB]/[SUB_PATH]/[ClassName].java` | `shared/framework/[LIB]` | `shared/framework/[LIB]/common/util/[SUB_PATH]/[ClassName]TestUtils.java` |

The area root is the first package level that owns a `common/` folder: one segment under
`domains/` and `infrastructure/`; one under `shared/`, except `shared/framework/[LIB]`. 
Shared parameter constants follow the same mapping into `.../common/constant/`.

### Common Constraints for TestUtils
- **Type:** Must be an `interface`.
- **Naming:** `[Target Class Name] + TestUtils`
- **Parameter Sources:** Actively reuse existing constant fields from the area's own
  `common/constant` path, and any other `common/constant` path the test legitimately depends on.
  If missing, create them.

### Categorized Strategy

The Group A / Group B split below is a **classification rule**, not a path list — apply it to
whatever subpackages a given area actually has. A package's role does not change without a
matching source refactor, so this split is stable across runs. Where a task-specific workflow
maintains a pre-computed path list for an area, use it; otherwise derive the split directly from
the actual package layout.

#### Group A: Immutable/Data Objects (Fields)
- **Applies to:** value objects, domain events, read models, records (request/response/model/
  jOOQ-record/composite-key — anything constructed once and not mutated afterward, even if it
  exposes a builder), and similar data carriers.
- **Rule:** Define as `public static final` fields.
- **Naming:** `test + [Target Class Name]`
- **Example:**
  ```java
  public interface PostAbuseReportApproveRecordTestUtils {
    PostAbuseReportApproveRecord testPostAbuseReportApproveRecord =
            new PostAbuseReportApproveRecord(TEST_POST_ULID);
  }
  ```

#### Group B: Mutable/Stateful Objects (Methods)
- **Applies to:** aggregates, entities (domain-level and JPA), and other classes with
  intent-named mutation methods or lifecycle callbacks.
- **Rule:** Define as `default` methods to allow flexible creation or parameter alteration.
- **Naming:** `create + [Target Class Name]`
  - *Note:* If multiple methods with different parameters are required to return various
    states, append parameter characteristics to the method name (e.g., `createMemberWithId`,
    `createSearchPostWithKeyword`).
- **Example:**
  ```java
  public interface PostAbuseReportDashboardEntityTestUtils extends PostEntityTestUtils {
      default PostAbuseReportDashboardEntityBuilder createPostAbuseReportDashboardUncheckedEntityBuilder() {
          return PostAbuseReportDashboardEntity.builder()
                  .status(AbuseReportStatus.UNCHECKED)
                  .firstReportedAt(TEST_REPORT_CREATED_AT)
                  .lastReportedAt(TEST_REPORT_CREATED_AT);
      }

      default PostAbuseReportDashboardEntityBuilder createPostAbuseReportDashboardDismissedEntityBuilder() {
          return PostAbuseReportDashboardEntity.builder()
                  .status(AbuseReportStatus.DISMISSED)
                  .firstReportedAt(TEST_REPORT_CREATED_AT)
                  .lastReportedAt(TEST_REPORT_DISMISSED_AT);
      }

      // ...
  }
  ```

---

## 8. Post-Generation Verification

Once test codes are generated, they must be verified and validated by running them.
