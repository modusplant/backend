package kr.modusplant.domains.comment.framework.outbound.jpa.mapper;

import kr.modusplant.domains.comment.common.util.domain.vo.CommentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.PostIdTestUtils;
import kr.modusplant.domains.comment.common.util.framework.outbound.jpa.entity.CommentEntityTestUtils;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.nullobject.EmptyReplyToCommentPath;
import kr.modusplant.domains.comment.framework.outbound.jpa.entity.CommentEntity;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentJpaMapperTest implements CommentTestUtils, CommentEntityTestUtils,
        MemberEntityTestUtils {
    private final CommentJpaMapper mapper = new CommentJpaMapperImpl();

    @Test
    @DisplayName("유효한 댓글을 댓글 엔티티로 전환")
    public void testToCommentEntity_givenValidComment_willReturnCommentEntity() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntity();
        PostEntity postEntity = createPublishedPostEntityBuilder().build();
        CommentEntity compare = createCommentEntityBuilder()
                .post(postEntity)
                .authMember(memberEntity)
                .build();

        // when
        CommentEntity result = mapper.toCommCommentEntity(testValidComment, memberEntity, postEntity);

        // then
        assertThat(result).isEqualTo(compare);
        assertThat(result.getReplyToPath()).isEqualTo(testValidComment.getReplyToPath().getValue());
    }

    @Test
    @DisplayName("답글 대상 경로가 없는 댓글을 댓글 엔티티로 전환")
    public void testToCommentEntity_givenCommentWithoutReplyToPath_willReturnCommentEntity() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntity();
        PostEntity postEntity = createPublishedPostEntityBuilder().build();
        Comment comment = Comment.create(
                PostIdTestUtils.testPostId, testCommentPath, EmptyReplyToCommentPath.create(), testCommentAuthor, testCommentContent);

        // when
        CommentEntity result = mapper.toCommCommentEntity(comment, memberEntity, postEntity);

        // then
        assertThat(result.getPath()).isEqualTo(testCommentPath.getValue());
        assertThat(result.getReplyToPath()).isNull();
    }

}
