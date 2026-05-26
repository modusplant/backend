package kr.modusplant.domains.member.framework.out.jpa.entity.common.util;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberProfileEntity;

import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.*;
import static kr.modusplant.domains.member.framework.out.jpa.entity.MemberProfileEntity.MemberProfileEntityBuilder;

public interface MemberProfileEntityTestUtils extends MemberEntityTestUtils {
    default MemberProfileEntityBuilder createMemberProfileBasicAdminEntityBuilder() {
        return MemberProfileEntity.builder()
                .imagePath(MEMBER_PROFILE_BASIC_ADMIN_IMAGE_PATH)
                .introduction(MEMBER_PROFILE_BASIC_ADMIN_INTRODUCTION);
    }

    default MemberProfileEntityBuilder createMemberProfileBasicUserEntityBuilder() {
        return MemberProfileEntity.builder()
                .imagePath(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH)
                .introduction(MEMBER_PROFILE_BASIC_USER_INTRODUCTION);
    }
}
