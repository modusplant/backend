package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.MemberWithdrawOpinion;

import static kr.modusplant.domains.member.common.constant.MemberWithdrawConstant.MEMBER_WITHDRAW_BASIC_USER_OPINION;

public interface MemberWithdrawOpinionTestUtils {
    MemberWithdrawOpinion testMemberWithdrawOpinion = MemberWithdrawOpinion.create(MEMBER_WITHDRAW_BASIC_USER_OPINION);
}
