package kr.modusplant.domains.comment.common.util.adapter;

import kr.modusplant.domains.comment.usecase.model.MemberReadModel;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberNicknameTestUtils.testMemberNickname;


public interface MemberReadModelTestUtils {
    MemberReadModel testMemberReadModel =
            new MemberReadModel(testMemberId.getValue(), testMemberNickname.getValue(), true);
}