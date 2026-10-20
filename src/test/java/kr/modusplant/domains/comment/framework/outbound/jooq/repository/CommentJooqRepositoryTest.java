package kr.modusplant.domains.comment.framework.outbound.jooq.repository;

import kr.modusplant.domains.comment.common.util.domain.vo.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.CommentPageSizeTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.PostIdTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.ReplyToCommentPathTestUtils;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentJooqRepositoryTest implements
        PostIdTestUtils, AuthorTestUtils, CommentPathTestUtils,
        CommentContentTestUtils, ReplyToCommentPathTestUtils, CommentPageSizeTestUtils {

    private final LocalDateTime testDateTime = LocalDateTime.parse("2025-10-16 14:30:45", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    private CommentJooqRepository createRepository(MockDataProvider provider) {
        MockConnection connection = new MockConnection(provider);
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);
        return new CommentJooqRepository(dsl);
    }

    @Test
    @DisplayName("게시글의 식별자와 댓글 경로로 댓글 엔티티가 존재하는지 확인")
    void testIsCommentExists_willReturnTrue() {
        // given
        MockDataProvider provider = ctx -> {
            Object[] bindings = ctx.bindings();

            if (bindings[0].equals(testPostId.getValue()) && bindings[1].equals(testCommentPath.getValue())) {
                DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
                Field<Boolean> existsField = DSL.field("exists", Boolean.class);
                Result<Record1<Boolean>> result = dsl.newResult(existsField);
                result.add(dsl.newRecord(existsField).values(true));

                return new MockResult[] { new MockResult(0, result)};
            }
            return new MockResult[] { new MockResult(0, null) };
        };
        CommentJooqRepository repository = createRepository(provider);

        // when
        boolean result = repository.isCommentExists(testPostId, testCommentPath);

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("게시글의 식별자와 댓글 경로로 삭제된 댓글인지 확인 시 True 반환")
    void testIsCommentDeleted_givenDeletedComment_willReturnTrue() {
        // given
        MockDataProvider provider = ctx -> {
            Object[] bindings = ctx.bindings();
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Field<Boolean> existsField = DSL.field("exists", Boolean.class);
            Result<Record1<Boolean>> result = dsl.newResult(existsField);
            result.add(dsl.newRecord(existsField).values(
                    bindings[0].equals(testPostId.getValue())
                            && bindings[1].equals(testCommentPath.getValue())
                            && bindings[2].equals(true)));
            return new MockResult[] { new MockResult(0, result) };
        };
        CommentJooqRepository repository = createRepository(provider);

        // when
        boolean result = repository.isCommentDeleted(testPostId, testCommentPath);

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("게시글의 식별자와 댓글 경로로 삭제되지 않은 댓글인지 확인 시 False 반환")
    void testIsCommentDeleted_givenNotDeletedComment_willReturnFalse() {
        // given
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Field<Boolean> existsField = DSL.field("exists", Boolean.class);
            Result<Record1<Boolean>> result = dsl.newResult(existsField);
            result.add(dsl.newRecord(existsField).values(false));
            return new MockResult[] { new MockResult(0, result) };
        };
        CommentJooqRepository repository = createRepository(provider);

        // when
        boolean result = repository.isCommentDeleted(testPostId, testCommentPath);

        // then
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("게시글의 식별자로 댓글 읽기 모델 목록 가져오기")
    void testFindByPost_givenValidPostId_willReturnCommentOfPostReadModelList() {
        // given: column order must match CommentJooqRepository#findByPost's select list
        // (profileImage, nickname, path, content, likeCount, isLiked, createdAt, isDeleted, editedAt, replyToPath, replyToNickname)
        Field<String> profileImage = DSL.field("profile_image", String.class);
        Field<String> nickname = DSL.field("nickname", String.class);
        Field<String> path = DSL.field("path", String.class);
        Field<String> content = DSL.field("content", String.class);
        Field<Integer> likeCount = DSL.field("like_count", Integer.class);
        Field<Boolean> isLiked = DSL.field("is_liked", Boolean.class);
        Field<LocalDateTime> createdAt = DSL.field("created_at", LocalDateTime.class);
        Field<Boolean> isDeleted = DSL.field("is_deleted", Boolean.class);
        Field<LocalDateTime> editedAt = DSL.field("edited_at", LocalDateTime.class);
        Field<String> replyToPath = DSL.field("reply_to_path", String.class);
        Field<String> replyToNickname = DSL.field("reply_to_nickname", String.class);

        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Result<Record11<String, String, String, String, Integer, Boolean, LocalDateTime, Boolean, LocalDateTime, String, String>> result =
                    dsl.newResult(profileImage, nickname, path, content, likeCount, isLiked, createdAt, isDeleted, editedAt,
                            replyToPath, replyToNickname);
            result.add(dsl.newRecord(profileImage, nickname, path, content, likeCount, isLiked, createdAt, isDeleted, editedAt,
                            replyToPath, replyToNickname)
                    .values("profile.png", "nickname", testCommentPath.getValue(), testCommentContent.getValue(),
                            3, true, testDateTime, false, testDateTime,
                            testReplyToCommentPath.getValue(), "replyToNickname"));
            return new MockResult[] { new MockResult(1, result) };
        };
        CommentJooqRepository repository = createRepository(provider);

        // when
        List<CommentOfPostReadModel> result = repository.findByPost(testPostId, testAuthorWithUuid);

        // then
        assertThat(result).hasSize(1);
        CommentOfPostReadModel readModel = result.getFirst();
        assertThat(readModel.profileImage()).isEqualTo("profile.png");
        assertThat(readModel.nickname()).isEqualTo("nickname");
        assertThat(readModel.path()).isEqualTo(testCommentPath.getValue());
        assertThat(readModel.content()).isEqualTo(testCommentContent.getValue());
        assertThat(readModel.likeCount()).isEqualTo(3);
        assertThat(readModel.isLiked()).isTrue();
        assertThat(readModel.createdAt()).isEqualTo(testDateTime.withNano(0));
        assertThat(readModel.updatedAt()).isEqualTo(testDateTime.withNano(0));
        assertThat(readModel.isDeleted()).isFalse();
        assertThat(readModel.replyToPath()).isEqualTo(testReplyToCommentPath.getValue());
        assertThat(readModel.replyToNickname()).isEqualTo("replyToNickname");
    }

    @Test
    @DisplayName("게시글의 식별자로 루트와 답글 모두 오래된 순으로 조회하는 활동 수행")
    void testFindByPost_givenValidPostId_willProcessAction() {
        // given: MockDataProvider does not execute ORDER BY / JOIN, so assert the ordering
        // contract against the generated SQL text instead of the returned row order
        Field<String> profileImage = DSL.field("profile_image", String.class);
        Field<String> nickname = DSL.field("nickname", String.class);
        Field<String> path = DSL.field("path", String.class);
        Field<String> content = DSL.field("content", String.class);
        Field<Integer> likeCount = DSL.field("like_count", Integer.class);
        Field<Boolean> isLiked = DSL.field("is_liked", Boolean.class);
        Field<LocalDateTime> createdAt = DSL.field("created_at", LocalDateTime.class);
        Field<Boolean> isDeleted = DSL.field("is_deleted", Boolean.class);
        Field<LocalDateTime> editedAt = DSL.field("edited_at", LocalDateTime.class);
        Field<String> replyToPath = DSL.field("reply_to_path", String.class);
        Field<String> replyToNickname = DSL.field("reply_to_nickname", String.class);

        String[] capturedSql = new String[1];
        MockDataProvider provider = ctx -> {
            capturedSql[0] = ctx.sql().toLowerCase();
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Result<Record11<String, String, String, String, Integer, Boolean, LocalDateTime, Boolean, LocalDateTime, String, String>> result =
                    dsl.newResult(profileImage, nickname, path, content, likeCount, isLiked, createdAt, isDeleted, editedAt,
                            replyToPath, replyToNickname);
            return new MockResult[] { new MockResult(0, result) };
        };
        CommentJooqRepository repository = createRepository(provider);

        // when
        repository.findByPost(testPostId, testAuthorWithUuid);

        // then
        String sql = capturedSql[0];
        assertThat(sql).contains("root_comment");
        assertThat(sql).contains("not like");
        int orderByIndex = sql.indexOf("order by");
        assertThat(orderByIndex).isGreaterThanOrEqualTo(0);
        String orderByClause = sql.substring(orderByIndex);
        assertThat(orderByClause).doesNotContain("desc");
        assertThat(orderByClause).contains("asc");
    }

    @Test
    @DisplayName("마지막 댓글 경로 없이 페이지 조회 시 CommentOfPostReadModel 목록 반환")
    void testFindByPostWithPage_givenNullLastPath_willReturnCommentOfPostReadModelList() {
        // given
        List<String> capturedSql = new ArrayList<>();
        List<Object[]> capturedBindings = new ArrayList<>();
        MockDataProvider provider = ctx -> {
            capturedSql.add(ctx.sql().toLowerCase());
            capturedBindings.add(ctx.bindings());
            Result<Record> result = createCommentOfPostResult(testCommentPath.getValue());
            return new MockResult[] { new MockResult(result.size(), result) };
        };
        CommentJooqRepository repository = createRepository(provider);

        // when
        List<CommentOfPostReadModel> result =
                repository.findByPost(testPostId, null, testCommentPageSize, testAuthorWithUuid);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().path()).isEqualTo(testCommentPath.getValue());
        assertThat(capturedSql).hasSize(1);
        assertThat(capturedSql.getFirst()).contains("fetch next ? rows only");
        assertThat(capturedBindings.getFirst()).contains(testCommentPageSize.getValue() + 1L);
    }

    @Test
    @DisplayName("존재하지 않는 마지막 댓글 경로로 페이지 조회 시 빈 CommentOfPostReadModel 목록 반환")
    void testFindByPostWithPage_givenNonExistentLastPath_willReturnCommentOfPostReadModelList() {
        // given
        List<String> capturedSql = new ArrayList<>();
        MockDataProvider provider = ctx -> {
            capturedSql.add(ctx.sql().toLowerCase());
            return new MockResult[] { new MockResult(0, createSortKeyResult()) };
        };
        CommentJooqRepository repository = createRepository(provider);

        // when
        List<CommentOfPostReadModel> result =
                repository.findByPost(testPostId, testCommentRootPath, testCommentPageSize, testAuthorWithUuid);

        // then
        assertThat(result).isEmpty();
        assertThat(capturedSql).hasSize(1);
    }

    @Test
    @DisplayName("존재하는 마지막 댓글 경로로 페이지 조회 시 CommentOfPostReadModel 목록 반환")
    void testFindByPostWithPage_givenExistentLastPath_willReturnCommentOfPostReadModelList() {
        // given
        List<String> capturedSql = new ArrayList<>();
        List<Object[]> capturedBindings = new ArrayList<>();
        MockDataProvider provider = ctx -> {
            capturedSql.add(ctx.sql().toLowerCase());
            capturedBindings.add(ctx.bindings());
            if (capturedSql.size() == 1) {
                Result<Record> sortKeyResult = createSortKeyResult();
                Record sortKeyRecord = DSL.using(SQLDialect.POSTGRES).newRecord(sortKeyResult.fields());
                sortKeyRecord.fromArray(testDateTime, testCommentRootPath.getValue(), false,
                        testDateTime, testCommentRootPath.getValue());
                sortKeyResult.add(sortKeyRecord);
                return new MockResult[] { new MockResult(1, sortKeyResult) };
            }
            Result<Record> result = createCommentOfPostResult(testCommentPath.getValue());
            return new MockResult[] { new MockResult(result.size(), result) };
        };
        CommentJooqRepository repository = createRepository(provider);

        // when
        List<CommentOfPostReadModel> result =
                repository.findByPost(testPostId, testCommentRootPath, testCommentPageSize, testAuthorWithUuid);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().path()).isEqualTo(testCommentPath.getValue());
        assertThat(capturedSql).hasSize(2);
        assertThat(capturedBindings.getFirst()).contains(testPostId.getValue(), testCommentRootPath.getValue());
        assertThat(capturedSql.get(1)).contains("fetch next ? rows only");
        assertThat(capturedBindings.get(1)).contains(Timestamp.valueOf(testDateTime), testCommentRootPath.getValue());
        assertThat(capturedBindings.get(1)).contains(testCommentPageSize.getValue() + 1L);
    }

    // column order must match CommentJooqRepository#getCommentOfPostFields
    private Result<Record> createCommentOfPostResult(String commentPath) {
        DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
        Field<?>[] fields = {
                DSL.field("profile_image", String.class),
                DSL.field("nickname", String.class),
                DSL.field("path", String.class),
                DSL.field("content", String.class),
                DSL.field("like_count", Integer.class),
                DSL.field("is_liked", Boolean.class),
                DSL.field("created_at", LocalDateTime.class),
                DSL.field("is_deleted", Boolean.class),
                DSL.field("edited_at", LocalDateTime.class),
                DSL.field("reply_to_path", String.class),
                DSL.field("reply_to_nickname", String.class)
        };
        Result<Record> result = dsl.newResult(fields);
        Record record = dsl.newRecord(fields);
        record.fromArray("profile.png", "nickname", commentPath,
                testCommentContent.getValue(), 3, true, testDateTime, false, testDateTime,
                testReplyToCommentPath.getValue(), "replyToNickname");
        result.add(record);
        return result;
    }

    // column order must match the sort-key select list in CommentJooqRepository#findByPost (paged)
    private Result<Record> createSortKeyResult() {
        Field<?>[] fields = {
                DSL.field("root_created_at", LocalDateTime.class),
                DSL.field("root_path", String.class),
                DSL.field("is_reply", Boolean.class),
                DSL.field("created_at", LocalDateTime.class),
                DSL.field("path", String.class)
        };
        return DSL.using(SQLDialect.POSTGRES).newResult(fields);
    }

    @Test
    @DisplayName("같은 부모 아래 형제 댓글들의 마지막 세그먼트 중 가장 큰 값 반환")
    void testFindMaximumSiblingPathOrder_givenSiblingComments_willReturnHighestOrdinal() {
        // given
        Field<String> path = DSL.field("path", String.class);
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Result<Record1<String>> result = dsl.newResult(path);
            result.add(dsl.newRecord(path).values("1.1"));
            result.add(dsl.newRecord(path).values("1.5"));
            result.add(dsl.newRecord(path).values("1.2"));
            return new MockResult[] { new MockResult(result.size(), result) };
        };
        CommentJooqRepository repository = createRepository(provider);

        // when
        int result = repository.findMaximumSiblingPathOrder(testPostId, testCommentPath);

        // then
        assertThat(result).isEqualTo(5);
    }

    @Test
    @DisplayName("형제 댓글이 없으면 0 반환")
    void testFindMaximumSiblingPathOrder_givenNoSiblingComments_willReturnZero() {
        // given
        Field<String> path = DSL.field("path", String.class);
        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Result<Record1<String>> result = dsl.newResult(path);
            return new MockResult[] { new MockResult(0, result) };
        };
        CommentJooqRepository repository = createRepository(provider);

        // when
        int result = repository.findMaximumSiblingPathOrder(testPostId, testCommentPath);

        // then
        assertThat(result).isEqualTo(0);
    }

    // findByAuthor issues two sequential statements (a selectCount() pre-check, then a grouped,
    // paginated join with a nested totalCommentsOfPost subquery). Reliably distinguishing both
    // statements through MockDataProvider's positional field-matching would be too brittle to
    // maintain here; this method is left to the jOOQ Repository Unit Test policy's discretion
    // rather than forcing a fragile mock.
}
