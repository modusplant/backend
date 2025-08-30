package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.aggregate.Member;

public interface MemberRepository {
    Member updateNickname(Member member);

    Member save(Member member);
}
