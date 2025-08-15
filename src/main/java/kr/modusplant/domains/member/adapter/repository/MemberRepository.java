package kr.modusplant.domains.member.adapter.repository;

import kr.modusplant.domains.member.domain.entity.Member;

public interface MemberRepository {
    Member save(Member member);
}
