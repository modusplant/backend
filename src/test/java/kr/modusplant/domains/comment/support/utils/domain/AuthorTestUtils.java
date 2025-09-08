package kr.modusplant.domains.comment.support.utils.domain;

import kr.modusplant.domains.comment.domain.vo.Author;

import static kr.modusplant.domains.member.test.constant.MemberStringConstant.TEST_MEMBER_NICKNAME;
import static kr.modusplant.domains.member.test.constant.MemberUuidConstant.TEST_MEMBER_UUID;

public interface AuthorTestUtils {

    Author testAuthor = Author.create(TEST_MEMBER_UUID, TEST_MEMBER_NICKNAME);
    Author testAuthorWithUuid = Author.create(TEST_MEMBER_UUID);

}
