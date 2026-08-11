package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.domains.member.usecase.response.MemberProfilePrepareResponse;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponseWithImagePath;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponseWithImageUrl;
import kr.modusplant.domains.member.usecase.response.supers.MemberProfileResponse;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INVALID_MEMBER_PROFILE_OVERRIDE_VERSION;

@Component
@RequiredArgsConstructor
public class MemberProfileMapperImpl implements MemberProfileMapper {
    private final AmazonS3Service amazonS3Service;

    @Override
    public MemberProfileResponse toMemberProfileResponse(MemberProfile memberProfile, int version) {
        if (version == 1) {
            String imagePath = memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue();
            if (imagePath == null) {
                return new MemberProfileResponseWithImageUrl(
                        memberProfile.getMemberId().getValue(),
                        null,
                        memberProfile.getMemberProfileIntroduction().getValue(),
                        memberProfile.getNickname().getValue());
            } else {
                return new MemberProfileResponseWithImageUrl(
                        memberProfile.getMemberId().getValue(),
                        amazonS3Service.generateS3SrcUrl(imagePath),
                        memberProfile.getMemberProfileIntroduction().getValue(),
                        memberProfile.getNickname().getValue());
            }
        } else if (version == 2) {
            String imagePath = memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue();
            if (imagePath == null) {
                return new MemberProfileResponseWithImageUrl(
                        memberProfile.getMemberId().getValue(),
                        null,
                        memberProfile.getMemberProfileIntroduction().getValue(),
                        memberProfile.getNickname().getValue());
            } else {
                return new MemberProfileResponseWithImageUrl(
                        memberProfile.getMemberId().getValue(),
                        imagePath,
                        memberProfile.getMemberProfileIntroduction().getValue(),
                        memberProfile.getNickname().getValue());
            }
        } else if (version == 3) {
            String imagePath = memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue();
            if (imagePath == null) {
                return new MemberProfileResponseWithImagePath(
                        memberProfile.getMemberId().getValue(),
                        null,
                        memberProfile.getMemberProfileIntroduction().getValue(),
                        memberProfile.getNickname().getValue());
            } else {
                return new MemberProfileResponseWithImagePath(
                        memberProfile.getMemberId().getValue(),
                        imagePath,
                        memberProfile.getMemberProfileIntroduction().getValue(),
                        memberProfile.getNickname().getValue());
            }
        } else {
            throw new InvalidValueException(INVALID_MEMBER_PROFILE_OVERRIDE_VERSION, "version");
        }
    }

    @Override
    public MemberProfilePrepareResponse toMemberProfilePrepareResponse(MemberProfileImagePath imagePath, String storageUrl) {
        return new MemberProfilePrepareResponse(imagePath.getValue(), storageUrl);
    }
}
