# Pure Unit Test Baseline

Unit tests must maintain a pure POJO state. **Do not use** Spring Context (`@SpringBootTest`,
`@WebMvcTest`) or Mockito Extension (`@ExtendWith(MockitoExtension.class)`), except for the
paths explicitly listed as exceptions in the target domain's profile.

# Mocking Strategy

All dependent classes, except for the explicit instance containing the method under test, must
be mocked using inline mocking via `Mockito.mock()`. Do not use `@Mock` or `@InjectMocks`
annotations.

# REST Controller Unit Test

Covers unit tests that verify method calls, return values, and exceptions by injecting mock
dependencies into the controller instance, without using MockMvc.

# Test Method Naming Convention

- **Format:** `testMethodName_givenCondition_willDoAction`
- **Conciseness:** Should not be overly verbose. Use clear and simple language that gets to the heart of the matter.
- **Will-Clause Rules:**
  - **Case 1: No return value (Void):** `..._willProcessAction` (e.g., `willReportAbuse`, `willVerifyRequest`)
  - **Case 2: Return value exists:** `..._willReturnResponse` or `..._willReturnReadModel` (Specify the concrete return type name)
  - **Case 3: Exception occurs:** `..._willThrowException` (Fixed format)

# Test Method Display Name Convention (Exceptionally Allowed to Use Korean Only Within This Sector)

- **Coherence:** Must share the same context with the method name. Should not include any additional information beyond what the method name implies.
- **Interpretation Rules For The Will-Clause on Method Names:**
  - **Case 1:** `..._willProcessAction` -> `활동 수행`
  - **Case 2:** `..._willReturnResponse` -> `응답 반환`, `..._willReturnReadModel` -> `읽기 모델 반환`
  - **Case 3:** `..._willThrowException` -> `예외 반환` (Fixed format)

# Test Body Convention (BDD Style)

Strictly adhere to the `given-when-then` pattern using `BDDMockito`.

- **given:** Define one-time objects required for testing and setup stubbing using `given()`.
- **when:** Execute the specific method under test (assign the return value to a variable if present).
- **then:**
    - Use `Mockito.verify()` to verify that the mock object's methods were called with correct arguments.
    - For exception testing, thoroughly verify that the exception code (the target domain's
      `[Domain]ErrorCode`, named in its profile) returned by `getErrorCode()` matches the expected value.

# Test Utility (`TestUtils`) Convention

To prevent code duplication, highly encourage reusing or creating Test Utility classes when
instantiating domain objects or models.

## Path Mapping Rule
When a class in `src/main/java/kr/modusplant/domains/[DOMAIN]/[SUB_PATH]/[ClassName].java` is
needed for testing, find or create its utility at:
`src/test/java/kr/modusplant/domains/[DOMAIN]/common/util/[SUB_PATH]/[ClassName]TestUtils.java`

## Common Constraints for TestUtils
- **Type:** Must be an `interface`.
- **Naming:** `[Target Class Name] + TestUtils`
- **Parameter Sources:** Actively reuse existing constant fields from the target domain's own
  `common/constant` path and any cross-domain `common/constant` paths listed in its profile. If
  missing, create them.

## Categorized Strategy

The Group A / Group B split below is a **classification rule**, not a path list — apply it to
whatever subpackages a given domain actually has. Each domain's own
`test-domain-profiles-[domain].md` file records the resulting concrete path list once, so it
doesn't need to be re-derived on every run; a package's role does not change without a matching
source refactor.

### Group A: Immutable/Data Objects (Fields)
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

### Group B: Mutable/Stateful Objects (Methods)
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

# Post-Generation Verification

Once test codes are generated, they must be verified and validated by running them.