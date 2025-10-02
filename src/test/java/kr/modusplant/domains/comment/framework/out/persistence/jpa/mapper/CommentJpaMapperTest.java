package kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.comment.common.util.domain.CommentTestUtils;
import kr.modusplant.domains.comment.common.util.framework.CommentEntityTestUtils;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CommentJpaMapperTest implements CommentTestUtils, CommentEntityTestUtils {
    private final CommentJpaMapper mapper = new CommentJpaMapperImpl();

    @Test
    @DisplayName("유효한 댓글을 댓글 엔티티로 전환")
    public void testToCommentEntity_givenValidComment_willReturnCommentEntity() {
        // given
        SiteMemberEntity testSiteMemberEntity = SiteMemberEntity.builder()
                .uuid(testAuthor.getMemberUuid())
                .nickname(MEMBER_BASIC_USER_NICKNAME)
                .birthDate(MEMBER_BASIC_USER_BIRTH_DATE)
                .loggedInAt(MEMBER_BASIC_USER_LOGGED_IN_AT)
                .build();

        CommentEntity compare = createCommentEntityBuilder()
                .postEntity(createCommPostEntityBuilder().ulid(TEST_COMM_POST_ULID).build())
                .authMember(testSiteMemberEntity)
                .createMember(testSiteMemberEntity).build();

        // when
        CommentEntity result = mapper.toCommentEntity(testValidComment);

        // then
        assertThat(result).isEqualTo(compare);
    }

}
