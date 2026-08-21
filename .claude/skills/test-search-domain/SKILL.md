---
name: test-search-domain
description: This file provides strict guidance on creating, modifying, and deleting tests for the search domain.
disable-model-invocation: true
disallowed-tools: Write(/src/main/**) Edit(/src/main/**)
---

# Target Classes

- Primary: classes created, modified, or deleted since the previous session, if it belongs to the search domain.
- Fallback - 1: If no search-domain classes were modified in the previous session, run `git status --porcelain | awk '{print $NF}' | grep '\.java$'` and find the search-domain classes within its output.
- Fallback - 2: if that also yields nothing, run `git diff --name-only HEAD~1 HEAD | grep '\.java$' | awk -F/ '{print $NF}' | sed 's/\.java$//'` and find the search-domain classes within its output.
- Termination: if no result was found, terminate the skill immediately.

# Excluded Classes

Regardless of the scope above, never generate tests for:
  - Enum classes
  - Exception classes
  - Classes that contain only constructors

# Test Architecture & Strategy

- **Pure Unit Test:** Unit tests must maintain a pure POJO state. **Do not use** Spring Context (`@SpringBootTest`, `@WebMvcTest`) or Mockito Extension (`@ExtendWith(MockitoExtension.class)`). But, there are some exceptions:
    - @src/main/java/kr/modusplant/domains/search/framework/outbound/jpa/repository (Essentially, necessary to verify interactions with the database)
- **jOOQ Repository Integration Test:** Unlike the other exclusion rules above, classes under @src/main/java/kr/modusplant/domains/search/framework/outbound/jooq/repository are covered by an integration test, not excluded. Use `@SpringBootTest` with a real Spring context and real seeded test data (via a test data helper) — do not mock `DSLContext`. Only genuinely time-dependent behavior (e.g. `LocalDateTime.now()`) should be controlled via `Mockito.mockStatic`.
- **Mocking Strategy:** All dependent classes, except for the explicit instance containing the method under test, must be mocked using inline mocking via `Mockito.mock()`. Do not use `@Mock` or `@InjectMocks` annotations.
- **REST Controller Unit Test:**: Covers unit tests that verify method calls, return values, and exceptions by injecting mock dependency into the controller instance, without using MockMvc.

!`cat ${CLAUDE_PROJECT_DIR}/.claude/documents/test-method-convention.md`

# Test Body Convention (BDD Style)

Strictly adhere to the `given-when-then` pattern using `BDDMockito`.

- **given:** Define one-time objects required for testing and setup stubbing using `given()`.
- **when:** Execute the specific method under test (assign the return value to a variable if present).
- **then:** - Use `Mockito.verify()` to verify that the mock object's methods were called with correct arguments.
    - For exception testing, thoroughly verify that the exception code (`SearchErrorCode`) returned by `getErrorCode()` matches the expected value.

# Test Utility (`TestUtils`) Convention

To prevent code duplication, highly encourage reusing or creating Test Utility classes when instantiating domain objects or models.

## Path Mapping Rule
When a class in `src/main/java/kr/modusplant/domains/search/[SUB_PATH]/[ClassName].java` is needed for testing, find or create its utility at:
`src/test/java/kr/modusplant/domains/search/common/util/[SUB_PATH]/[ClassName]TestUtils.java`

## Common Constraints for TestUtils
- **Type:** Must be an `interface` (not a class).
- **Naming:** `[Target Class Name] + TestUtils`
- **Parameter Sources:** Actively reuse existing constant fields from the following paths for object generation parameters. If missing, create them:
  - @src/test/java/kr/modusplant/domains/search/common/constant
  - @src/test/java/kr/modusplant/domains/post/common/constant
  - @src/test/java/kr/modusplant/domains/comment/common/constant

## Categorized Strategy

### Group A: Immutable/Data Objects (Fields)
- **Target Paths:** (Only look up if needed for testing)
  - @src/test/java/kr/modusplant/domains/search/domain/vo (including `vo/nullobject`)
  - @src/test/java/kr/modusplant/domains/search/usecase/model/read
  - @src/test/java/kr/modusplant/domains/search/usecase/record
  - @src/test/java/kr/modusplant/domains/search/usecase/response
- **Rule:** Define as `public static final` fields.
- **Naming:** `test + [Target Class Name]`
- **Example:**
  ```java
  public interface PostAbuseReportApproveRecordTestUtils {
    PostAbuseReportApproveRecord testPostAbuseReportApproveRecord =
            new PostAbuseReportApproveRecord(TEST_POST_ULID);
  }

### Group B: Mutable/Stateful Objects (Methods)
- **Target Paths:** (Only look up if needed for testing)
  - @src/test/java/kr/modusplant/domains/search/domain/aggregate
  - @src/test/java/kr/modusplant/domains/search/domain/entity
- **Rule:** Define as `default` methods to allow flexible creation or parameter alteration.
- **Naming:** `create + [Target Class Name]`
  - *Note:* If multiple methods with different parameters are required to return various states, append parameter characteristics to the method name (e.g., `createSearchPostWithId`, `createSearchPostWithKeyword`).
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

# Post-Generation Verification

Once test codes are generated, they must be verified and validated by running them.