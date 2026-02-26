package kr.modusplant.domains.comment.framework.out.persistence.jooq;

import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.framework.aws.service.S3FileService;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentJooqRepositoryTest implements
        PostIdTestUtils, AuthorTestUtils, CommentPathTestUtils,
        CommentContentTestUtils {

    private Result<Record6<String, String, String, String, Boolean, LocalDateTime>> testResult;

    private final MockDataProvider provider = new MockDataProvider() {
        @Override
        public MockResult[] execute(MockExecuteContext mockExecuteContext) {
            return new MockResult[]{
                    new MockResult(1, testResult)
            };
        }
    };

    private final MockConnection connection = new MockConnection(provider);
    private final S3FileService s3FileService = Mockito.mock(S3FileService.class);
//    private final DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);
//    private final CommentJooqRepository repository = new CommentJooqRepository(dsl);
    private final LocalDateTime testDateTime = LocalDateTime.parse("2025-10-16 14:30:45", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    private CommentJooqRepository createRepository(MockDataProvider provider) {
        MockConnection connection = new MockConnection(provider);
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);
        return new CommentJooqRepository(dsl);
    }

    @Test
    @DisplayName("게시글의 식별자와 댓글 경로로 댓글 엔티티가 존재하는지 확인")
    void testExistsByPostAndPath_givenValidPostUlidAndCommentPath_willReturnTrue() {
        // given
        MockDataProvider provider = ctx -> {
            Object[] bindings = ctx.bindings();

            if (bindings[0].equals(testPostId.getId()) && bindings[1].equals(testCommentPath.getPath())) {
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
        boolean result = repository.existsByPostAndPath(testPostId, testCommentPath);

        // then
        assertThat(result).isEqualTo(true);
    }

//    @Test
//    @DisplayName("게시글의 식별자로 댓글 응답 가져오기")
//    void testFindByPost_givenValidPostUlid_willReturnCommentResponse() {
//
//        // given & when
//        List<CommentResponse> result = repository.findByPost(testPostId);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getFirst().postId()).isEqualTo(testPostId.getId());
//        assertThat(result.getFirst().path()).isEqualTo(testCommentPath.getPath());
//        assertThat(result.getFirst().nickname()).isEqualTo(testNickname.getNickname());
//        assertThat(result.getFirst().content()).isEqualTo(testCommentContent.getContent());
//        assertThat(result.getFirst().isDeleted()).isFalse();
//        assertThat(result.getFirst().createdAt()).isEqualTo(testDateTime.toString());
//    }

//    @Test
//    @DisplayName("사용자의 식별자로 댓글 응답 가져오기")
//    void testFindByAuthor_givenValidAuthMemberUuid_willReturnCommentResponse() {
//
//        // given & when
//        List<CommentResponse> result = repository.findByAuthor(testAuthorWithUuid);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getFirst().postId()).isEqualTo(testPostId.getId());
//        assertThat(result.getFirst().path()).isEqualTo(testCommentPath.getPath());
//        assertThat(result.getFirst().nickname()).isEqualTo(testNickname.getNickname());
//        assertThat(result.getFirst().content()).isEqualTo(testCommentContent.getContent());
//        assertThat(result.getFirst().isDeleted()).isFalse();
//        assertThat(result.getFirst().createdAt()).isEqualTo(testDateTime.toString());
//    }
}
