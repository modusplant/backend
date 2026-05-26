package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.domain.vo.MemberWithdrawOpinion;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyMemberWithdrawOpinion extends MemberWithdrawOpinion {
    public static EmptyMemberWithdrawOpinion create() {
        return instance;
    }
    private static final EmptyMemberWithdrawOpinion instance = new EmptyMemberWithdrawOpinion();

    @Override
    public String getValue() {
        return null;
    }
}
