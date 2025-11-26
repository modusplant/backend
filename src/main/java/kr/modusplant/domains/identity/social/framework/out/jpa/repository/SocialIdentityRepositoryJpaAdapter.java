package kr.modusplant.domains.identity.social.framework.out.jpa.repository;

import kr.modusplant.domains.identity.social.domain.vo.MemberId;
import kr.modusplant.domains.identity.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.identity.social.domain.vo.SocialUserProfile;
import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.domains.identity.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.domains.identity.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberRoleJpaRepository;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SocialIdentityRepositoryJpaAdapter implements SocialIdentityRepository {
    private final SiteMemberJpaRepository memberJpaRepository;
    private final SiteMemberAuthJpaRepository memberAuthJpaRepository;
    private final SiteMemberRoleJpaRepository memberRoleJpaRepository;
    private final SocialIdentityJpaMapper socialIdentityJpaMapper;

    @Override
    public Optional<MemberId> getMemberIdBySocialCredentials(SocialCredentials socialCredentials) {
        return memberAuthJpaRepository.findByProviderAndProviderId(socialCredentials.getProvider(), socialCredentials.getProviderId())
                .map(memberAuthEntity -> MemberId.fromUuid(memberAuthEntity.getActiveMember().getUuid()));
    }

    @Override
    public UserPayload getUserPayloadByMemberId(MemberId memberId) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId.getValue())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, TableName.SITE_MEMBER));
        SiteMemberRoleEntity memberRoleEntity = memberRoleJpaRepository.findByMember(memberEntity)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_ROLE_NOT_FOUND, TableName.SITE_MEMBER_ROLE));
        return socialIdentityJpaMapper.toUserPayload(memberEntity,memberRoleEntity);
    }

    @Override
    public void updateLoggedInAt(MemberId memberId) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId.getValue())
                .orElseThrow();
        memberEntity.updateLoggedInAt(LocalDateTime.now());
        memberJpaRepository.save(memberEntity);
    }

    @Override
    public UserPayload createSocialMember(SocialUserProfile profile, Role role) {
        SiteMemberEntity memberEntity = memberJpaRepository.save(socialIdentityJpaMapper.toMemberEntity(profile.getNickname()));
        memberAuthJpaRepository.save(socialIdentityJpaMapper.toMemberAuthEntity(memberEntity, profile));
        memberRoleJpaRepository.save(socialIdentityJpaMapper.toMemberRoleEntity(memberEntity,role));
        return socialIdentityJpaMapper.toUserPayload(memberEntity, profile.getNickname(), role);
    }

}
