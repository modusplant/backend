package kr.modusplant.domains.comment.support.utils.framework;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.support.utils.domain.CommentTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommPostEntityTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityConstant;

public interface CommentEntityTestUtils extends
        CommentTestUtils, CommentCompositeKeyTestUtils,
        CommPostEntityTestUtils, SiteMemberEntityConstant {

    default CommentEntity createCommentEntity() {
        return CommentEntity.builder()
                .postEntity(createCommPostEntityBuilder().build())
                .authMember(createMemberBasicUserEntity())
                .createMember(createMemberBasicUserEntity())
                .id(testCommentCompositeKey)
                .content(testCommentContent.getContent())
                .isDeleted(false)
                .build();
    }

    default CommentEntity.CommentEntityBuilder createCommentEntityBuilder() {
        return CommentEntity.builder()
                .id(testCommentCompositeKey)
                .content(testCommentContent.getContent())
                .isDeleted(false);
    }
}
