package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.CommPostAbuRepEntity;
import kr.modusplant.framework.jpa.entity.CommPostAbuRepEntity.CommPostAbuRepEntityBuilder;

public interface CommPostAbuRepEntityTestUtils extends SiteMemberEntityTestUtils, CommPostEntityTestUtils {
    default CommPostAbuRepEntityBuilder createCommPostAbuRepEntityBuilder() {
        return CommPostAbuRepEntity.builder();
    }
}
