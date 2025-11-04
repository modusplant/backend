package kr.modusplant.domains.member.common.constant;

import kr.modusplant.domains.member.domain.vo.MemberStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberStringConstant {
    public static final String TEST_MEMBER_ACTIVE_STATUS_STRING = MemberStatus.active().getValue();
    public static final String TEST_MEMBER_INACTIVE_STATUS_STRING = MemberStatus.inactive().getValue();
    public static final String TEST_MEMBER_NICKNAME_STRING = MEMBER_BASIC_USER_NICKNAME;
    public static final String TEST_TARGET_COMMENT_PATH_STRING = TEST_COMM_COMMENT_PATH;
    public static final String TEST_TARGET_POST_ID_STRING = TEST_COMM_POST_ULID;
}
