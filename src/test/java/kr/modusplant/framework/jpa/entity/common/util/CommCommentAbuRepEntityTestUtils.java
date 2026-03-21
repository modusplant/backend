package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.CommCommentAbuRepEntity;
import kr.modusplant.framework.jpa.entity.CommCommentAbuRepEntity.CommCommentAbuRepEntityBuilder;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_ULID;

public interface CommCommentAbuRepEntityTestUtils extends SiteMemberEntityTestUtils, CommCommentEntityTestUtils {
    default CommCommentAbuRepEntityBuilder createCommCommentAbuRepEntityBuilder() {
        return CommCommentAbuRepEntity.builder();
    }

    default CommCommentAbuRepEntityBuilder createCommCommentAbuRepEntityBuilderWithUlid() {
        return CommCommentAbuRepEntity.builder()
                .ulid(TEST_REPORT_ULID);
    }
}
