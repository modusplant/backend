package kr.modusplant.domains.member.framework.out.jpa.mapper;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.framework.out.jpa.mapper.supers.MemberProfileJpaMapper;
import kr.modusplant.framework.out.aws.service.S3FileService;
import kr.modusplant.framework.out.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MemberProfileJpaMapperImpl implements MemberProfileJpaMapper {
    private final SiteMemberJpaRepository memberJpaRepository;
    private final S3FileService s3FileService;

    @Override
    public SiteMemberProfileEntity toMemberProfileEntity(MemberProfile profile) {
        return SiteMemberProfileEntity.builder()
                .member(memberJpaRepository.findByUuid(profile.getMemberId().getValue()).orElseThrow())
                .imagePath(profile.getMemberProfileImage().getMemberProfileImagePath().getValue())
                .introduction(profile.getMemberProfileIntroduction().getValue())
                .build();
    }

    @Override
    public MemberProfile toMemberProfile(SiteMemberProfileEntity entity) throws IOException {
        return MemberProfile.create(
                MemberId.fromUuid(entity.getMember().getUuid()),
                MemberProfileImage.create(
                        MemberProfileImagePath.create(entity.getImagePath()),
                        MemberProfileImageBytes.create(s3FileService.downloadFile(entity.getImagePath()))),
                MemberProfileIntroduction.create(entity.getIntroduction()),
                MemberNickname.create(entity.getMember().getNickname()));
        }
}
