package kr.modusplant.domains.member.framework.out.jpa.entity.common.util;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberProfileEntity;

import static kr.modusplant.domains.member.framework.out.jpa.entity.MemberProfileEntity.MemberProfileEntityBuilder;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.*;

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
