package kr.modusplant.domains.member.framework.outbound.jpa.mapper;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.entity.nullobject.EmptyMemberProfileImage;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberProfileImageBytes;
import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import kr.modusplant.domains.member.domain.vo.MemberProfileIntroduction;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberProfileIntroduction;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberProfileEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.mapper.supers.MemberProfileJpaMapper;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import kr.modusplant.shared.kernel.Nickname;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MemberProfileJpaMapperImpl implements MemberProfileJpaMapper {
    private final MemberJpaRepository memberJpaRepository;
    private final AmazonS3Service amazonS3Service;

    @Override
    public MemberProfileEntity toMemberProfileEntity(MemberProfile profile) {
        return MemberProfileEntity.builder()
                .member(memberJpaRepository.findByUuid(profile.getMemberId().getValue()).orElseThrow())
                .imagePath(profile.getMemberProfileImage().getMemberProfileImagePath().getValue())
                .introduction(profile.getMemberProfileIntroduction().getValue())
                .build();
    }

    @Override
    public MemberProfile toMemberProfile(MemberProfileEntity entity) throws IOException {
        MemberProfileImage memberProfileImage;
        if (entity.getImagePath() == null) {
            memberProfileImage = EmptyMemberProfileImage.create();
        } else {
            memberProfileImage = MemberProfileImage.create(
                    MemberProfileImagePath.create(entity.getImagePath()),
                    MemberProfileImageBytes.create(amazonS3Service.downloadFile(entity.getImagePath())));
        }
        MemberProfileIntroduction memberProfileIntroduction;
        if (entity.getIntroduction() == null) {
            memberProfileIntroduction = EmptyMemberProfileIntroduction.create();
        } else {
            memberProfileIntroduction = MemberProfileIntroduction.create(entity.getIntroduction());
        }
        return MemberProfile.create(
                MemberId.fromUuid(entity.getMember().getUuid()),
                memberProfileImage,
                memberProfileIntroduction,
                Nickname.create(entity.getMember().getNickname())
        );
    }
}
