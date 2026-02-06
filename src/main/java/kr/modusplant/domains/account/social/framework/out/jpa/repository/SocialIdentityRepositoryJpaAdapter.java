package kr.modusplant.domains.account.social.framework.out.jpa.repository;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.vo.SocialAccountPayload;
import kr.modusplant.domains.account.social.domain.vo.SocialAccountProfile;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.domains.account.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberRoleJpaRepository;
import kr.modusplant.infrastructure.security.enums.Role;
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
    public Optional<AccountId> getMemberIdBySocialCredentials(SocialCredentials socialCredentials) {
        return memberAuthJpaRepository.findByProviderAndProviderId(socialCredentials.getProvider(), socialCredentials.getProviderId())
                .map(memberAuthEntity -> AccountId.fromUuid(memberAuthEntity.getActiveMember().getUuid()));
    }

    @Override
    public SocialAccountPayload getUserPayloadByMemberId(AccountId accountId) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.MEMBER_NOT_FOUND, TableName.SITE_MEMBER));
        SiteMemberAuthEntity memberAuthEntity = memberAuthJpaRepository.findByActiveMember(memberEntity).getFirst();
        SiteMemberRoleEntity memberRoleEntity = memberRoleJpaRepository.findByMember(memberEntity)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.MEMBER_ROLE_NOT_FOUND, TableName.SITE_MEMBER_ROLE));
        return socialIdentityJpaMapper.toUserPayload(memberEntity,memberAuthEntity,memberRoleEntity);
    }

    @Override
    public void updateLoggedInAt(AccountId accountId) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow();
        memberEntity.updateLoggedInAt(LocalDateTime.now());
        memberJpaRepository.save(memberEntity);
    }

    @Override
    public SocialAccountPayload createSocialMember(SocialAccountProfile profile, Role role) {
        SiteMemberEntity memberEntity = memberJpaRepository.save(socialIdentityJpaMapper.toMemberEntity(profile.getNickname()));
        memberAuthJpaRepository.save(socialIdentityJpaMapper.toMemberAuthEntity(memberEntity, profile));
        memberRoleJpaRepository.save(socialIdentityJpaMapper.toMemberRoleEntity(memberEntity,role));
        return socialIdentityJpaMapper.toUserPayload(memberEntity, profile.getNickname(), profile.getEmail(), role);
    }

}
