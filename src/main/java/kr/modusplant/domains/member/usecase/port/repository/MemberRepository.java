package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberNickname;

import java.util.Optional;

public interface MemberRepository {
    Optional<Member> getByNickname(MemberNickname nickname);

    Member updateNickname(Member member);

    Member save(Member member);

    boolean isNicknameExist(MemberNickname nickname);
}
