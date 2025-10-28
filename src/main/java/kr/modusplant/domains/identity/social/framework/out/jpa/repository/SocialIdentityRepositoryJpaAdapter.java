package kr.modusplant.domains.identity.social.framework.out.jpa.repository;

import kr.modusplant.domains.identity.social.domain.vo.*;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberRoleEntity;
import kr.modusplant.domains.identity.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.domains.identity.social.framework.out.jpa.repository.supers.MemberAuthJpaRepository;
import kr.modusplant.domains.identity.social.framework.out.jpa.repository.supers.MemberJpaRepository;
import kr.modusplant.domains.identity.social.framework.out.jpa.repository.supers.MemberRoleJpaRepository;
import kr.modusplant.domains.identity.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SocialIdentityRepositoryJpaAdapter implements SocialIdentityRepository {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberAuthJpaRepository memberAuthJpaRepository;
    private final MemberRoleJpaRepository memberRoleJpaRepository;
    private final SocialIdentityJpaMapper socialIdentityJpaMapper;

    @Override
    public Optional<MemberId> getMemberIdBySocialCredentials(SocialCredentials socialCredentials) {
        return memberAuthJpaRepository.findByProviderAndProviderId(socialCredentials.getProvider(),socialCredentials.getProviderId())
                .map(memberAuthEntity -> MemberId.fromUuid(memberAuthEntity.getActiveMember().getUuid()));
    }

    @Override
    public UserPayload getUserPayloadByMemberId(MemberId memberId) {
        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberId.getValue())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, EntityName.SITE_MEMBER));
        MemberRoleEntity memberRoleEntity = memberRoleJpaRepository.findByMember(memberEntity)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_ROLE_NOT_FOUND, EntityName.SITE_MEMBER_ROLE));
        return socialIdentityJpaMapper.toUserPayload(memberEntity,memberRoleEntity);
    }

    @Override
    public void updateLoggedInAt(MemberId memberId) {
        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberId.getValue())
                .orElseThrow();
        memberEntity.updateLoggedInAt(LocalDateTime.now());
        memberJpaRepository.save(memberEntity);
    }

    @Override
    public UserPayload createSocialMember(SocialUserProfile profile, Role role) {
        MemberEntity memberEntity = memberJpaRepository.save(socialIdentityJpaMapper.toMemberEntity(profile.getNickname()));
        memberAuthJpaRepository.save(socialIdentityJpaMapper.toMemberAuthEntity(memberEntity, profile));
        memberRoleJpaRepository.save(socialIdentityJpaMapper.toMemberRoleEntity(memberEntity,role));
        return socialIdentityJpaMapper.toUserPayload(memberEntity, profile.getNickname(), role);
    }

}
