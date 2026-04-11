package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.PropBugRepEntity;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.*;

public interface PropBugRepEntityTestUtils extends SiteMemberEntityTestUtils {
    default PropBugRepEntity.PropBugRepEntityBuilder createPropBugRepEntityBuilder() {
        return PropBugRepEntity.builder()
                .title(TEST_REPORT_TITLE)
                .content(TEST_REPORT_CONTENT)
                .imagePath(TEST_REPORT_IMAGE_PATH);
    }

    default PropBugRepEntity.PropBugRepEntityBuilder createPropBugRepEntityBuilderWithUlid() {
        return PropBugRepEntity.builder()
                .ulid(TEST_REPORT_ULID)
                .title(TEST_REPORT_TITLE)
                .content(TEST_REPORT_CONTENT)
                .imagePath(TEST_REPORT_IMAGE_PATH);
    }
}
