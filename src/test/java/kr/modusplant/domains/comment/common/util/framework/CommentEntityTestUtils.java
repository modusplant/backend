package kr.modusplant.domains.comment.common.util.framework;

import kr.modusplant.domains.comment.common.util.domain.CommentTestUtils;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.CommPostEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;

public interface CommentEntityTestUtils extends
        CommentTestUtils, CommentCompositeKeyTestUtils,
        CommPostEntityTestUtils, SiteMemberEntityTestUtils {

    default CommentEntity createCommentEntity() {
        return CommentEntity.builder()
                .postEntity(createCommPostEntityBuilder().build())
                .authMember(createMemberBasicUserEntity())
                .createMember(createMemberBasicUserEntity())
                .id(testCommentCompositeKey)
                .likeCount(1)
                .content(testCommentContent.getContent())
                .isDeleted(false)
                .build();
    }

    default CommentEntity.CommentEntityBuilder createCommentEntityBuilder() {
        return CommentEntity.builder()
                .id(testCommentCompositeKey)
                .likeCount(1)
                .content(testCommentContent.getContent())
                .isDeleted(false);
    }
}
