package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.shared.kernel.Nickname;

import java.util.Optional;

public interface MemberRepository {
    Optional<Member> getById(MemberId memberId);

    Optional<Member> getByNickname(Nickname nickname);

    Member save(Nickname nickname);

    Member save(MemberId memberId, Nickname nickname);

    boolean isIdExist(MemberId memberId);

    boolean isNicknameExist(Nickname nickname);
}
