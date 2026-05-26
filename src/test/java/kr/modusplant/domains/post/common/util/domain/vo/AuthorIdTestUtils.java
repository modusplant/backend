package kr.modusplant.domains.post.common.util.domain.vo;

import kr.modusplant.domains.post.domain.vo.AuthorId;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_ADMIN_UUID;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

public interface AuthorIdTestUtils {
    AuthorId testAuthorId = AuthorId.fromUuid(MEMBER_BASIC_USER_UUID);
    AuthorId testAuthorId2 = AuthorId.fromUuid(MEMBER_BASIC_ADMIN_UUID);
}
