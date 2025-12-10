package kr.modusplant.domains.comment.framework.out.persistence.jooq;

import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private final DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);
    private final CommentJooqRepository repository = new CommentJooqRepository(dsl);
    private final LocalDateTime testDateTime = LocalDateTime.parse("2025-10-16 14:30:45", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @BeforeEach
    void setUp() {
        Record6<String, String, String, String, Boolean, LocalDateTime> testRecord = dsl.newRecord(
                DSL.field("ulid", String.class),
                DSL.field("path", String.class),
                DSL.field("nickname", String.class),
                DSL.field("content", String.class),
                DSL.field("is_deleted", Boolean.class),
                DSL.field("created_at", LocalDateTime.class)
        );

        testRecord.value1(testPostId.getId());
        testRecord.value2(testCommentPath.getPath());
//        testRecord.value3(testNickname.getNickname());
        testRecord.value4(testCommentContent.getContent());
        testRecord.value5(false);
        testRecord.value6(testDateTime);

        testResult = dsl.newResult(testRecord.field1(), testRecord.field2(), testRecord.field3(),
                        testRecord.field4(), testRecord.field5(), testRecord.field6());
        testResult.add(testRecord);
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
