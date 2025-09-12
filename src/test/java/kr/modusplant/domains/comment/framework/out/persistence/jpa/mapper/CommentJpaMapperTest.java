package kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.support.utils.domain.CommentTestUtils;
import kr.modusplant.domains.comment.support.utils.framework.CommentEntityTestUtils;
import kr.modusplant.framework.out.persistence.jpa.entity.SiteMemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentJpaMapperTest implements CommentTestUtils, CommentEntityTestUtils {
    private final CommentJpaMapper mapper = new CommentJpaMapperImpl();

    @Test
    @DisplayName("유효한 댓글을 댓글 엔티티로 전환")
    public void testToCommentEntity_givenValidComment_willReturnCommentEntity() {
        // given
        SiteMemberEntity testSiteMemberEntity = SiteMemberEntity.builder()
                .uuid(testAuthor.getMemberUuid())
                .nickname(memberBasicUser.getNickname())
                .birthDate(memberBasicUser.getBirthDate())
                .loggedInAt(memberBasicUser.getLoggedInAt())
                .build();

        CommentEntity compare = createCommentEntityBuilder()
                .postEntity(createCommPostEntityBuilder().ulid(TEST_COMM_POST_WITH_ULID.getUlid()).build())
                .authMember(testSiteMemberEntity)
                .createMember(testSiteMemberEntity).build();

        // when
        CommentEntity result = mapper.toCommentEntity(testValidComment);

        // then
        assertThat(result).isEqualTo(compare);
    }

}
