package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.CommCommentAbuRepEntity;
import kr.modusplant.framework.jpa.entity.CommCommentAbuRepEntity.CommCommentAbuRepEntityBuilder;

public interface CommCommentAbuRepEntityTestUtils extends SiteMemberEntityTestUtils, CommCommentEntityTestUtils {
    default CommCommentAbuRepEntityBuilder createCommCommentAbuRepEntityBuilder() {
        return CommCommentAbuRepEntity.builder();
    }
}
