package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.framework.out.jpa.mapper.MemberProfileJpaMapperImpl;
import kr.modusplant.domains.member.usecase.port.repository.MemberProfileRepository;
import kr.modusplant.framework.out.aws.service.S3FileService;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberProfileRepositoryJpaAdapter implements MemberProfileRepository {
    private final S3FileService s3FileService;
    private final MemberProfileJpaMapperImpl memberProfileJpaMapper;
    private final SiteMemberJpaRepository memberJpaRepository;
    private final SiteMemberProfileJpaRepository memberProfileJpaRepository;

    @Override
    public Optional<MemberProfile> getById(MemberId memberId) throws IOException {
        Optional<SiteMemberProfileEntity> memberProfileEntityOrEmpty = memberProfileJpaRepository.findByUuid(memberId.getValue());
        if (memberProfileEntityOrEmpty.isPresent()) {
            SiteMemberProfileEntity memberProfileEntity = memberProfileEntityOrEmpty.orElseThrow();
            return Optional.of(MemberProfile.create(memberId,
                    MemberProfileImage.create(
                            MemberProfileImagePath.create(memberProfileEntity.getImagePath()),
                            MemberProfileImageBytes.create(s3FileService.downloadFile(memberProfileEntity.getImagePath()))),
                    MemberProfileIntroduction.create(memberProfileEntity.getIntroduction()),
                    MemberNickname.create(memberProfileEntity.getMember().getNickname())));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public MemberProfile add(MemberProfile memberProfile) throws IOException {
        return memberProfileJpaMapper.toMemberProfile(memberProfileJpaRepository.save(
                SiteMemberProfileEntity.builder()
                        .member(memberJpaRepository.findByUuid(memberProfile.getMemberId().getValue()).orElseThrow())
                        .imagePath(memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue())
                        .introduction(memberProfile.getMemberProfileIntroduction().getValue())
                        .build()));
    }

    @Override
    public MemberProfile addOrUpdate(MemberProfile memberProfile) throws IOException {
        String imagePath = memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue();
        String introduction = memberProfile.getMemberProfileIntroduction().getValue();
        String nickname = memberProfile.getMemberNickname().getValue();
        Optional<SiteMemberProfileEntity> optionalMemberProfileEntity = memberProfileJpaRepository.findByUuid(
                memberProfile.getMemberId().getValue());
        SiteMemberProfileEntity memberProfileEntity;
        if (optionalMemberProfileEntity.isPresent()) {
            memberProfileEntity = optionalMemberProfileEntity.orElseThrow();
            memberProfileEntity.updateImagePath(imagePath);
            memberProfileEntity.updateIntroduction(introduction);
            memberProfileEntity.getMember().updateNickname(nickname);
        } else {
            SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberProfile.getMemberId().getValue()).orElseThrow();
            memberEntity.updateNickname(nickname);
            memberProfileEntity = SiteMemberProfileEntity.builder()
                    .member(memberEntity)
                    .imagePath(imagePath)
                    .introduction(introduction)
                    .build();
        }
        return memberProfileJpaMapper.toMemberProfile(memberProfileJpaRepository.save(
                memberProfileEntity));
    }

    @Override
    public boolean isIdExist(MemberId memberId) {
        return memberProfileJpaRepository.existsByUuid(memberId.getValue());
    }
}
