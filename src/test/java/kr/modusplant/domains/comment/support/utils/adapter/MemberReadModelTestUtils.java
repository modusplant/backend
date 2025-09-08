package kr.modusplant.domains.comment.support.utils.adapter;

import kr.modusplant.domains.comment.adapter.model.MemberReadModel;
import kr.modusplant.domains.member.test.utils.domain.MemberIdTestUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberNicknameTestUtils;

public interface MemberReadModelTestUtils extends MemberIdTestUtils, MemberNicknameTestUtils {
    MemberReadModel testMemberReadModel =
            new MemberReadModel(testMemberId.getValue(), testMemberNickname.getValue(), true);
}