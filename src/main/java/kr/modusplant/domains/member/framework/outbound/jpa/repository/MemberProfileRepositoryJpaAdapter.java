package kr.modusplant.domains.member.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.entity.nullobject.EmptyMemberProfileImage;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberProfileImageBytes;
import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import kr.modusplant.domains.member.domain.vo.MemberProfileIntroduction;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberProfileEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.mapper.MemberProfileJpaMapperImpl;
import kr.modusplant.domains.member.usecase.port.repository.MemberProfileRepository;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.kernel.Nickname;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Optional;

import static kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode.NOT_FOUND_MEMBER_PROFILE;

@Repository
@RequiredArgsConstructor
public class MemberProfileRepositoryJpaAdapter implements MemberProfileRepository {
    private final AmazonS3Service amazonS3Service;
    private final MemberProfileJpaMapperImpl memberProfileJpaMapper;
    private final MemberJpaRepository memberJpaRepository;
    private final MemberProfileJpaRepository memberProfileJpaRepository;

    @Override
    public MemberProfile getById(MemberId memberId) throws IOException {
        Optional<MemberProfileEntity> profileEntityOrEmpty =
                memberProfileJpaRepository.findByUuid(memberId.getValue());
        if (profileEntityOrEmpty.isPresent()) {
            MemberProfileEntity profileEntity = profileEntityOrEmpty.orElseThrow();
            MemberProfileImage profileImage;
            if (profileEntity.getImagePath() == null) {
                profileImage = EmptyMemberProfileImage.create();
            } else {
                profileImage = MemberProfileImage.create(
                        MemberProfileImagePath.create(profileEntity.getImagePath()),
                        MemberProfileImageBytes.create(amazonS3Service.downloadFile(profileEntity.getImagePath())));
            }
            return MemberProfile.create(
                    memberId,
                    profileImage,
                    MemberProfileIntroduction.create(profileEntity.getIntroduction()),
                    Nickname.create(profileEntity.getMember().getNickname()));
        } else {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER_PROFILE, "memberProfile");
        }
    }

    @Override
    public MemberProfile add(MemberProfile memberProfile) throws IOException {
        return memberProfileJpaMapper.toMemberProfile(
                memberProfileJpaRepository.save(
                        MemberProfileEntity.builder()
                                .member(memberJpaRepository
                                        .findByUuid(memberProfile.getMemberId().getValue())
                                        .orElseThrow())
                                .imagePath(memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue())
                                .introduction(memberProfile.getMemberProfileIntroduction().getValue())
                                .build()));
    }

    @Override
    public MemberProfile update(MemberProfile memberProfile) throws IOException {
        String imagePath = memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue();
        String introduction = memberProfile.getMemberProfileIntroduction().getValue();
        String nickname = memberProfile.getNickname().getValue();
        MemberProfileEntity memberProfileEntity = memberProfileJpaRepository
                .findByUuid(memberProfile.getMemberId().getValue()).orElseThrow();
        memberProfileEntity.updateImagePath(imagePath);
        memberProfileEntity.updateIntroduction(introduction);
        memberProfileEntity.getMember().updateNickname(nickname);
        return memberProfileJpaMapper.toMemberProfile(memberProfileJpaRepository.save(memberProfileEntity));
    }

    @Override
    public boolean isIdExist(MemberId memberId) {
        return memberProfileJpaRepository.existsByUuid(memberId.getValue());
    }
}
