package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.CommPostAbuRepEntity;
import kr.modusplant.framework.jpa.entity.CommPostAbuRepEntity.CommPostAbuRepEntityBuilder;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_ULID;

public interface CommPostAbuRepEntityTestUtils extends SiteMemberEntityTestUtils, CommPostEntityTestUtils {
    default CommPostAbuRepEntityBuilder createCommPostAbuRepEntityBuilder() {
        return CommPostAbuRepEntity.builder();
    }

    default CommPostAbuRepEntityBuilder createCommPostAbuRepEntityBuilderWithUlid() {
        return CommPostAbuRepEntity.builder()
                .ulid(TEST_REPORT_ULID);
    }
}
