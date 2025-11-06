package kr.modusplant.domains.comment.common.util.domain;

import kr.modusplant.domains.comment.domain.vo.Author;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;


public interface AuthorTestUtils {

    Author testAuthor = Author.create(MEMBER_BASIC_USER_UUID, MEMBER_BASIC_USER_NICKNAME);
    Author testAuthorWithUuid = Author.create(MEMBER_BASIC_USER_UUID);

}
