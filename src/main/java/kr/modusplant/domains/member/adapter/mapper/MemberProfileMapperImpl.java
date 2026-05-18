package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberProfileMapperImpl implements MemberProfileMapper {
    private final AmazonS3Service amazonS3Service;

    @Override
    public MemberProfileResponse toMemberProfileResponse(MemberProfile memberProfile) {
        String imagePath = memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue();
        if (imagePath == null) {
            return new MemberProfileResponse(
                    memberProfile.getMemberId().getValue(),
                    null,
                    memberProfile.getMemberProfileIntroduction().getValue(),
                    memberProfile.getNickname().getValue());
        } else {
            return new MemberProfileResponse(
                    memberProfile.getMemberId().getValue(),
                    amazonS3Service.generateS3SrcUrl(imagePath),
                    memberProfile.getMemberProfileIntroduction().getValue(),
                    memberProfile.getNickname().getValue());
        }
    }
}
