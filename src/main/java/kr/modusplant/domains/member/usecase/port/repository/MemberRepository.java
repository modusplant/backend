package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.enums.MemberWithdrawReason;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberWithdrawOpinion;
import kr.modusplant.shared.kernel.Nickname;

import java.util.Optional;

public interface MemberRepository {
    Member getById(MemberId memberId);

    Optional<Member> getByNickname(Nickname nickname);

    Member add(Nickname nickname);

    Member add(MemberId memberId, Nickname nickname);

    boolean isIdExist(MemberId memberId);

    boolean isNicknameExist(Nickname nickname);

    void withdraw(MemberId memberId, MemberWithdrawReason reason, MemberWithdrawOpinion opinion);
}
