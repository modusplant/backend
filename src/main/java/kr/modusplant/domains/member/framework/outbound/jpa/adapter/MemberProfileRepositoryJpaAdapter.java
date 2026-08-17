package kr.modusplant.domains.member.framework.outbound.jpa.adapter;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberProfileImageBytes;
import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import kr.modusplant.domains.member.domain.vo.MemberProfileIntroduction;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberProfileEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.mapper.MemberProfileJpaMapperImpl;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberProfileJpaRepository;
import kr.modusplant.domains.member.usecase.port.repository.MemberProfileRepository;
import kr.modusplant.infrastructure.file.service.PendingFileService;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.kernel.Nickname;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_MEMBER_PROFILE;

@Repository
@RequiredArgsConstructor
public class MemberProfileRepositoryJpaAdapter implements MemberProfileRepository {
    private final PendingFileService pendingFileService;

    private final MemberProfileJpaMapperImpl memberProfileJpaMapper;
    private final MemberProfileJpaRepository memberProfileJpaRepository;

    @Override
    public MemberProfile getByIdWithoutImageBytes(MemberId memberId) {
        Optional<MemberProfileEntity> profileEntityOrEmpty =
                memberProfileJpaRepository.findByUuid(memberId.getValue());
        if (profileEntityOrEmpty.isPresent()) {
            MemberProfileEntity profileEntity = profileEntityOrEmpty.orElseThrow();
            MemberProfileImagePath profileImagePath = MemberProfileImagePath.create(profileEntity.getImagePath());
            return MemberProfile.create(
                    memberId,
                    MemberProfileImage.create(profileImagePath, MemberProfileImageBytes.create(null)),
                    MemberProfileIntroduction.create(profileEntity.getIntroduction()),
                    Nickname.create(profileEntity.getMember().getNickname()));
        } else {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER_PROFILE, "memberProfile");
        }
    }

    @Override
    public MemberProfile update(MemberProfile memberProfile, boolean needsUntracking, boolean needsImageBytes) throws IOException {
        String imagePath = memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue();
        String introduction = memberProfile.getMemberProfileIntroduction().getValue();
        String nickname = memberProfile.getNickname().getValue();
        MemberProfileEntity memberProfileEntity = memberProfileJpaRepository
                .findByUuid(memberProfile.getMemberId().getValue()).orElseThrow();
        memberProfileEntity.updateImagePath(imagePath);
        memberProfileEntity.updateIntroduction(introduction);
        memberProfileEntity.getMember().updateNickname(nickname);
        MemberProfileEntity savedMemberProfileEntity = memberProfileJpaRepository.save(memberProfileEntity);
        if (needsUntracking) {
            pendingFileService.untrackPendingFiles(List.of(imagePath));
        }
        return memberProfileJpaMapper.toMemberProfile(savedMemberProfileEntity, needsImageBytes);
    }

    @Override
    public boolean isIdExist(MemberId memberId) {
        return memberProfileJpaRepository.existsByUuid(memberId.getValue());
    }
}
