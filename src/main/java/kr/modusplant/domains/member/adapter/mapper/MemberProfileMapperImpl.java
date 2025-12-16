package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class MemberProfileMapperImpl implements MemberProfileMapper {
    @Override
    public MemberProfileResponse toMemberProfileResponse(MemberProfile memberProfile) {
        return new MemberProfileResponse(
                memberProfile.getMemberId().getValue(),
                memberProfile.getMemberProfileImage().getMemberProfileImageBytes().getValue(),
                memberProfile.getMemberProfileIntroduction().getValue(),
                memberProfile.getNickname().getValue());
    }
}
