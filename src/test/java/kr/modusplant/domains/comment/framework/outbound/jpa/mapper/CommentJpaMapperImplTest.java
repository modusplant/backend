package kr.modusplant.domains.comment.framework.outbound.jpa.mapper;

import kr.modusplant.domains.comment.common.util.domain.vo.CommentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.ParentPostIdTestUtils;
import kr.modusplant.domains.comment.common.util.framework.outbound.jpa.entity.CommentEntityTestUtils;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.nullobject.EmptyReplyToCommentPath;
import kr.modusplant.domains.comment.framework.outbound.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.outbound.jpa.mapper.supers.CommentJpaMapper;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.shared.framework.jdbc.common.util.SwearHolderTestUtils;
import kr.modusplant.shared.framework.jdbc.holder.SwearHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentJpaMapperImplTest implements CommentTestUtils, CommentEntityTestUtils,
        MemberEntityTestUtils {
    @SuppressWarnings("unused")
    private final SwearHolder swearHolder = SwearHolderTestUtils.createSwearHolder();
    private final CommentJpaMapper mapper = new CommentJpaMapperImpl();

    @Test
    @DisplayName("유효한 댓글로 엔티티 변환 시 CommentEntity 반환")
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
    @DisplayName("답글 대상 경로가 없는 댓글로 엔티티 변환 시 CommentEntity 반환")
    public void testToCommentEntity_givenCommentWithoutReplyToPath_willReturnCommentEntity() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntity();
        PostEntity postEntity = createPublishedPostEntityBuilder().build();
        Comment comment = Comment.create(
                ParentPostIdTestUtils.testParentPostId, testCommentPath, EmptyReplyToCommentPath.create(), testCommentAuthorId, testCommentContent);

        // when
        CommentEntity result = mapper.toCommCommentEntity(comment, memberEntity, postEntity);

        // then
        assertThat(result.getPath()).isEqualTo(testCommentPath.getValue());
        assertThat(result.getReplyToPath()).isNull();
    }

    @Test
    @DisplayName("삭제된 댓글로 엔티티 변환 시 CommentEntity 반환")
    public void testToCommentEntity_givenDeletedComment_willReturnCommentEntity() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntity();
        PostEntity postEntity = createPublishedPostEntityBuilder().build();

        // when
        CommentEntity result = mapper.toCommCommentEntity(testInvalidComment, memberEntity, postEntity);

        // then
        assertThat(result.getIsDeleted()).isTrue();
        assertThat(result.getPost()).isEqualTo(postEntity);
        assertThat(result.getAuthMember()).isEqualTo(memberEntity);
    }
}
