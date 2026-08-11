package kr.modusplant.domains.member.usecase.port.mapper;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import kr.modusplant.domains.member.usecase.response.MemberProfilePrepareResponse;
import kr.modusplant.domains.member.usecase.response.supers.MemberProfileResponse;

public interface MemberProfileMapper {
    MemberProfileResponse toMemberProfileResponse(MemberProfile member, int version);

    MemberProfilePrepareResponse toMemberProfilePrepareResponse(MemberProfileImagePath imagePath, String storageUrl);
}
