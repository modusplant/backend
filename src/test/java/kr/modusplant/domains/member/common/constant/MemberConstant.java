package kr.modusplant.domains.member.common.constant;

import kr.modusplant.domains.member.domain.vo.MemberStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberConstant {
    public static final String MEMBER_BASIC_USER_ACTIVE_STATUS_STRING = MemberStatus.active().getValue();
    public static final String MEMBER_BASIC_USER_INACTIVE_STATUS_STRING = MemberStatus.inactive().getValue();
}
