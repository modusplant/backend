package kr.modusplant.domains.member.common.constant;

import kr.modusplant.domains.member.domain.vo.MemberStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberStringConstant {
    public static final String TEST_MEMBER_ACTIVE_STATUS = MemberStatus.active().getValue();
    public static final String TEST_MEMBER_INACTIVE_STATUS = MemberStatus.inactive().getValue();
    public static final String TEST_MEMBER_NICKNAME = MEMBER_BASIC_USER_NICKNAME;
}
