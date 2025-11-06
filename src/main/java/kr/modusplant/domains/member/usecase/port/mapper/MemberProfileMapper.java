package kr.modusplant.domains.member.usecase.port.mapper;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;

public interface MemberProfileMapper {
    MemberProfileResponse toMemberProfileResponse(MemberProfile member);
}
