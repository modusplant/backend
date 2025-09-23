package kr.modusplant.domains.comment.support.utils.adapter;

import kr.modusplant.domains.comment.usecase.model.MemberReadModel;
import kr.modusplant.domains.member.common.utils.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.member.common.utils.domain.vo.MemberNicknameTestUtils;


public interface MemberReadModelTestUtils extends MemberIdTestUtils, MemberNicknameTestUtils {
    MemberReadModel testMemberReadModel =
            new MemberReadModel(testMemberId.getValue(), testMemberNickname.getValue(), true);
}