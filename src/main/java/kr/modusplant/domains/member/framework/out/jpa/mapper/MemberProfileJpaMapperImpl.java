package kr.modusplant.domains.member.framework.out.jpa.mapper;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.entity.nullobject.MemberEmptyProfileImage;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.domain.vo.nullobject.MemberEmptyProfileIntroduction;
import kr.modusplant.domains.member.framework.out.jpa.mapper.supers.MemberProfileJpaMapper;
import kr.modusplant.framework.aws.service.S3FileService;
import kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
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
        MemberProfileImage memberProfileImage;
        if (entity.getImagePath() == null) {
            memberProfileImage = MemberEmptyProfileImage.create();
        } else {
            memberProfileImage = MemberProfileImage.create(
                    MemberProfileImagePath.create(entity.getImagePath()),
                    MemberProfileImageBytes.create(s3FileService.downloadFile(entity.getImagePath())));
        }
        MemberProfileIntroduction memberProfileIntroduction;
        if (entity.getIntroduction() == null) {
            memberProfileIntroduction = MemberEmptyProfileIntroduction.create();
        } else {
            memberProfileIntroduction = MemberProfileIntroduction.create(entity.getIntroduction());
        }
        return MemberProfile.create(
                MemberId.fromUuid(entity.getMember().getUuid()),
                memberProfileImage,
                memberProfileIntroduction,
                MemberNickname.create(entity.getMember().getNickname()));
        }
}
