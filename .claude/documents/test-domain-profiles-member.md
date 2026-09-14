# Test Domain Profile: member

The Group A / Group B lists below are relative to
`src/main/java/kr/modusplant/domains/member/` and mirrored 1:1 under
`src/test/java/kr/modusplant/domains/member/common/util/`.

- **ErrorCode class:** `MemberErrorCode`
- **Excluded-classes additions:** jOOQ repository classes (excluded entirely, see policy below)
- **Pure-Unit-Test path exceptions:** `framework/outbound/jpa/entity` (if absolutely necessary to
  save data directly during testing), `framework/outbound/jpa/repository` (necessary to verify DB interaction)
- **jOOQ repository test policy:** `excluded` — no test is written for jOOQ repository classes in
  this domain. Evidence: no test file exists under
  `src/test/java/kr/modusplant/domains/member/framework/outbound/jooq/`; only a `jooq/record`
  TestUtils exists (`ActivitySubjectCommentIdRecordTestUtils.java`), which is a Group A record, not a repository test.
- **TestUtils shared constant paths:** `domains/member/common/constant`, `domains/post/common/constant`, `domains/comment/common/constant`
- **Group A (fields):** `domain/event`, `domain/vo` (incl. `vo/nullobject`), `framework/outbound/jooq/record`, `framework/outbound/jpa/compositekey`, `framework/outbound/jpa/entity/record` (records only), `usecase/model/read`, `usecase/record`, `usecase/request`, `usecase/response`
- **Group B (methods):** `domain/aggregate`, `domain/entity` (incl. `entity/nullobject`), `framework/outbound/jpa/entity` (excluding `entity/record`)
