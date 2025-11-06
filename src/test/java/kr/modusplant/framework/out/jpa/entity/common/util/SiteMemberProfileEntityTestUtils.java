package kr.modusplant.framework.out.jpa.entity.common.util;

import kr.modusplant.framework.out.jpa.entity.SiteMemberProfileEntity;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.*;

public interface SiteMemberProfileEntityTestUtils {
    default SiteMemberProfileEntity.SiteMemberProfileEntityBuilder createMemberProfileBasicAdminEntityBuilder() {
        return SiteMemberProfileEntity.builder()
                .imagePath(MEMBER_PROFILE_BASIC_ADMIN_IMAGE_URL)
                .introduction(MEMBER_PROFILE_BASIC_ADMIN_INTRODUCTION);
    }

    default SiteMemberProfileEntity.SiteMemberProfileEntityBuilder createMemberProfileBasicUserEntityBuilder() {
        return SiteMemberProfileEntity.builder()
                .imagePath(MEMBER_PROFILE_BASIC_USER_IMAGE_URL)
                .introduction(MEMBER_PROFILE_BASIC_USER_INTRODUCTION);
    }
}
