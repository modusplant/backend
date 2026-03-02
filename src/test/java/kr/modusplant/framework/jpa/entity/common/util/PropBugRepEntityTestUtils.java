package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.PropBugRepEntity;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.*;

public interface PropBugRepEntityTestUtils extends SiteMemberEntityTestUtils {
    default PropBugRepEntity.PropBugRepEntityBuilder createPropBugRepEntityBuilder() {
        return PropBugRepEntity.builder()
                .title(REPORT_TITLE)
                .content(REPORT_CONTENT)
                .imagePath(REPORT_IMAGE_PATH);
    }

    default PropBugRepEntity.PropBugRepEntityBuilder createPropBugRepEntityBuilderWithUlid() {
        return PropBugRepEntity.builder()
                .ulid(REPORT_ULID)
                .title(REPORT_TITLE)
                .content(REPORT_CONTENT)
                .imagePath(REPORT_IMAGE_PATH);
    }
}
