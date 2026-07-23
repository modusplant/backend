package kr.modusplant.domains.search.common.util.domain.vo;

import kr.modusplant.domains.search.domain.vo.SearcherId;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

public interface SearcherIdTestUtils {
    SearcherId testSearcherId = SearcherId.fromUuid(MEMBER_BASIC_USER_UUID);
}
