package kr.modusplant.domains.member.adapter.out.repository;

import kr.modusplant.domains.member.domain.entity.Member;

public interface MemberRepository {
    Member updateNickname(Member member);

    Member saveMember(Member member);
}
