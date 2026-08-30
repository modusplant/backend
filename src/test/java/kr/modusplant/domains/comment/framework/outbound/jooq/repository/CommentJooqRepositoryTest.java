package kr.modusplant.domains.comment.framework.outbound.jooq.repository;

import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentJooqRepositoryTest implements
        PostIdTestUtils, AuthorTestUtils, CommentPathTestUtils,
        CommentContentTestUtils {

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
    @DisplayName("게시글의 식별자로 댓글 읽기 모델 목록 가져오기")
    void testFindByPost_givenValidPostId_willReturnCommentOfPostReadModelList() {
        // given: column order must match CommentJooqRepository#findByPost's select list
        // (profileImage, nickname, path, content, likeCount, isLiked, createdAt, isDeleted, editedAt)
        Field<String> profileImage = DSL.field("profile_image", String.class);
        Field<String> nickname = DSL.field("nickname", String.class);
        Field<String> path = DSL.field("path", String.class);
        Field<String> content = DSL.field("content", String.class);
        Field<Integer> likeCount = DSL.field("like_count", Integer.class);
        Field<Boolean> isLiked = DSL.field("is_liked", Boolean.class);
        Field<LocalDateTime> createdAt = DSL.field("created_at", LocalDateTime.class);
        Field<Boolean> isDeleted = DSL.field("is_deleted", Boolean.class);
        Field<LocalDateTime> editedAt = DSL.field("edited_at", LocalDateTime.class);

        MockDataProvider provider = ctx -> {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Result<Record9<String, String, String, String, Integer, Boolean, LocalDateTime, Boolean, LocalDateTime>> result =
                    dsl.newResult(profileImage, nickname, path, content, likeCount, isLiked, createdAt, isDeleted, editedAt);
            result.add(dsl.newRecord(profileImage, nickname, path, content, likeCount, isLiked, createdAt, isDeleted, editedAt)
                    .values("profile.png", "nickname", testCommentPath.getValue(), testCommentContent.getValue(),
                            3, true, testDateTime, false, testDateTime));
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
    }

    // findByAuthor issues two sequential statements (a selectCount() pre-check, then a grouped,
    // paginated join with a nested totalCommentsOfPost subquery). Reliably distinguishing both
    // statements through MockDataProvider's positional field-matching would be too brittle to
    // maintain here; this method is left to the jOOQ Repository Unit Test policy's discretion
    // rather than forcing a fragile mock.
}
