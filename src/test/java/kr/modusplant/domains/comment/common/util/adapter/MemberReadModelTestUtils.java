package kr.modusplant.domains.comment.common.util.adapter;

import kr.modusplant.domains.comment.usecase.model.MemberReadModel;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;


public interface MemberReadModelTestUtils {
    MemberReadModel testMemberReadModel =
            new MemberReadModel(testMemberId.getValue(), testNormalUserNickname.getValue(), true);
}