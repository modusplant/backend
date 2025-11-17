package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity;

import static kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity.SiteMemberProfileEntityBuilder;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.*;

public interface SiteMemberProfileEntityTestUtils {
    default SiteMemberProfileEntityBuilder createMemberProfileBasicAdminEntityBuilder() {
        return SiteMemberProfileEntity.builder()
                .imagePath(MEMBER_PROFILE_BASIC_ADMIN_IMAGE_PATH)
                .introduction(MEMBER_PROFILE_BASIC_ADMIN_INTRODUCTION);
    }

    default SiteMemberProfileEntityBuilder createMemberProfileBasicUserEntityBuilder() {
        return SiteMemberProfileEntity.builder()
                .imagePath(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH)
                .introduction(MEMBER_PROFILE_BASIC_USER_INTRODUCTION);
    }
}
