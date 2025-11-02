package kr.modusplant.domains.member.usecase.port.mapper;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.usecase.response.MemberResponse;

public interface MemberMapper {
    MemberResponse toMemberResponse(Member member);
}
