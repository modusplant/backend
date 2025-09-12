package kr.modusplant.domains.comment.support.utils.domain;

import kr.modusplant.domains.comment.domain.vo.Author;

import java.util.UUID;

import static kr.modusplant.domains.member.test.constant.MemberStringConstant.TEST_MEMBER_NICKNAME;

public interface AuthorTestUtils {

    Author testAuthor = Author.create(UUID.fromString("d6b716f1-60f7-4c79-aeaf-37037101f126"), TEST_MEMBER_NICKNAME);
    Author testAuthorWithUuid = Author.create(UUID.fromString("d6b716f1-60f7-4c79-aeaf-37037101f126"));

}
