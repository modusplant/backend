package kr.modusplant.domains.post.common.util.domain.vo;

import kr.modusplant.domains.post.domain.vo.PostAuthorId;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_ADMIN_UUID;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

public interface AuthorIdTestUtils {
    PostAuthorId testPostAuthorId = PostAuthorId.fromUuid(MEMBER_BASIC_USER_UUID);
    PostAuthorId testPostAuthorId2 = PostAuthorId.fromUuid(MEMBER_BASIC_ADMIN_UUID);
}
