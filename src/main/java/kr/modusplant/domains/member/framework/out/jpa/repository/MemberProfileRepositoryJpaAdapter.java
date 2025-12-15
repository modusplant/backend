package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.entity.nullobject.MemberEmptyProfileImage;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberProfileImageBytes;
import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import kr.modusplant.domains.member.domain.vo.MemberProfileIntroduction;
import kr.modusplant.domains.member.domain.vo.nullobject.MemberEmptyProfileIntroduction;
import kr.modusplant.domains.member.framework.out.jpa.mapper.MemberProfileJpaMapperImpl;
import kr.modusplant.domains.member.usecase.port.repository.MemberProfileRepository;
import kr.modusplant.framework.aws.service.S3FileService;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberProfileJpaRepository;
import kr.modusplant.shared.kernel.Nickname;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberProfileRepositoryJpaAdapter implements MemberProfileRepository {
    private final S3FileService s3FileService;
    private final MemberProfileJpaMapperImpl profileJpaMapper;
    private final SiteMemberJpaRepository jpaRepository;
    private final SiteMemberProfileJpaRepository profileJpaRepository;

    @Override
    public Optional<MemberProfile> getById(MemberId memberId) throws IOException {
        Optional<SiteMemberProfileEntity> profileEntityOrEmpty = profileJpaRepository.findByUuid(memberId.getValue());
        if (profileEntityOrEmpty.isPresent()) {
            SiteMemberProfileEntity profileEntity = profileEntityOrEmpty.orElseThrow();
            MemberProfileImage profileImage;
            MemberProfileIntroduction profileIntroduction;
            if (profileEntity.getImagePath() == null) {
                profileImage = MemberEmptyProfileImage.create();
            } else {
                profileImage = MemberProfileImage.create(
                        MemberProfileImagePath.create(profileEntity.getImagePath()),
                        MemberProfileImageBytes.create(s3FileService.downloadFile(profileEntity.getImagePath())));
            }
            if (profileEntity.getIntroduction() == null) {
                profileIntroduction = MemberEmptyProfileIntroduction.create();
            } else {
                profileIntroduction = MemberProfileIntroduction.create(profileEntity.getIntroduction());
            }
            return Optional.of(MemberProfile.create(memberId,
                    profileImage,
                    profileIntroduction,
                    Nickname.create(profileEntity.getMember().getNickname())));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public MemberProfile add(MemberProfile memberProfile) throws IOException {
        return profileJpaMapper.toMemberProfile(profileJpaRepository.save(
                SiteMemberProfileEntity.builder()
                        .member(jpaRepository.findByUuid(memberProfile.getMemberId().getValue()).orElseThrow())
                        .imagePath(memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue())
                        .introduction(memberProfile.getMemberProfileIntroduction().getValue())
                        .build()));
    }

    @Override
    public MemberProfile addOrUpdate(MemberProfile memberProfile) throws IOException {
        String imagePath = memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue();
        String introduction = memberProfile.getMemberProfileIntroduction().getValue();
        String nickname = memberProfile.getNickname().getValue();
        Optional<SiteMemberProfileEntity> optionalMemberProfileEntity = profileJpaRepository.findByUuid(
                memberProfile.getMemberId().getValue());
        SiteMemberProfileEntity memberProfileEntity;
        if (optionalMemberProfileEntity.isPresent()) {
            memberProfileEntity = optionalMemberProfileEntity.orElseThrow();
            memberProfileEntity.updateImagePath(imagePath);
            memberProfileEntity.updateIntroduction(introduction);
            memberProfileEntity.getMember().updateNickname(nickname);
        } else {
            SiteMemberEntity memberEntity = jpaRepository.findByUuid(memberProfile.getMemberId().getValue()).orElseThrow();
            memberEntity.updateNickname(nickname);
            memberProfileEntity = SiteMemberProfileEntity.builder()
                    .member(memberEntity)
                    .imagePath(imagePath)
                    .introduction(introduction)
                    .build();
        }
        return profileJpaMapper.toMemberProfile(profileJpaRepository.save(
                memberProfileEntity));
    }

    @Override
    public boolean isIdExist(MemberId memberId) {
        return profileJpaRepository.existsByUuid(memberId.getValue());
    }
}
