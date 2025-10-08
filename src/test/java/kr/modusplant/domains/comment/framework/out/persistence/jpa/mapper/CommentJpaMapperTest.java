package kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.comment.common.util.domain.CommentTestUtils;
import kr.modusplant.framework.out.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.CommCommentEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CommentJpaMapperTest implements CommentTestUtils, CommCommentEntityTestUtils,
        SiteMemberEntityTestUtils {
    private final CommentJpaMapper mapper = new CommentJpaMapperImpl();

    @Test
    @DisplayName("유효한 댓글을 댓글 엔티티로 전환")
    public void testToCommCommentEntity_givenValidComment_willReturnCommCommentEntity() {
        // given
        CommCommentEntity compare = createCommCommentEntityBuilder()
                .postEntity(createCommPostEntityBuilder().ulid(TEST_COMM_POST_ULID).build())
                .path(TEST_COMM_COMMENT_PATH)
                .authMember(createMemberBasicUserEntity())
                .createMember(createMemberBasicUserEntity()).build();

        // when
        CommCommentEntity result = mapper.toCommCommentEntity(testValidComment);

        // then
        assertThat(result).isEqualTo(compare);
    }

}
