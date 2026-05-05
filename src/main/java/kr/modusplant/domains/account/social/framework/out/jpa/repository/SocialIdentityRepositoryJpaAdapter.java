package kr.modusplant.domains.account.social.framework.out.jpa.repository;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.exception.SocialAccountConflictException;
import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.domains.account.social.domain.vo.AgreedTerms;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.account.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.domains.account.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberProfileJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberTermJpaRepository;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.kernel.Email;
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
    private final SiteMemberProfileJpaRepository memberProfileJpaRepository;
    private final SiteMemberTermJpaRepository memberTermJpaRepository;
    private final SocialIdentityJpaMapper socialIdentityJpaMapper;

    public Optional<SocialMemberProfile> getSocialMemberProfileByEmail(Email email) {
        return memberAuthJpaRepository.findByEmail(email.getValue())
                .map(memberAuthEntity -> socialIdentityJpaMapper.toSocialMemberProfile(memberAuthEntity.getMember(),memberAuthEntity));
    }

    @Override
    public SocialMemberProfile getSocialMemberProfileByAccountId(AccountId accountId) {
        SiteMemberAuthEntity memberAuthEntity = memberAuthJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH, TableName.SITE_MEMBER_AUTH));
        return socialIdentityJpaMapper.toSocialMemberProfile(memberAuthEntity.getMember(),memberAuthEntity);
    }

    @Override
    public SocialMemberProfile updateLoggedInAtAndGetProfile(AccountId accountId) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow();
        memberEntity.updateLoggedInAt(LocalDateTime.now());
        memberJpaRepository.save(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = memberAuthJpaRepository.findByMember(memberEntity)
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH, TableName.SITE_MEMBER_AUTH));
        return socialIdentityJpaMapper.toSocialMemberProfile(memberEntity, memberAuthEntity);
    }

    @Override
    public SocialMemberProfile saveSocialMember(SocialMemberProfile profile, String introduction, AgreedTerms agreedTerms) {
        SiteMemberEntity memberEntity = memberJpaRepository.save(socialIdentityJpaMapper.toMemberEntity(profile.getNickname(), profile.getRole()));
        SiteMemberAuthEntity memberAuthEntity = memberAuthJpaRepository.save(socialIdentityJpaMapper.toMemberAuthEntity(memberEntity, profile.getSocialCredentials(), profile.getEmail()));
        memberProfileJpaRepository.save(socialIdentityJpaMapper.toMemberProfileEntity(memberEntity,introduction));
        memberTermJpaRepository.save(socialIdentityJpaMapper.toMemberTermEntity(memberEntity,agreedTerms));
        return socialIdentityJpaMapper.toSocialMemberProfile(memberEntity, memberAuthEntity);
    }

    @Override
    public SocialMemberProfile updateSocialLinkedMember(SocialCredentials socialCredentials, Email email) {
        SiteMemberAuthEntity memberAuthEntity = memberAuthJpaRepository.findByEmail(email.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH, TableName.SITE_MEMBER_AUTH));
        if (memberAuthEntity.getProvider().equals(socialCredentials.getProvider())) {
            throw new SocialAccountConflictException(SocialIdentityErrorCode.ALREADY_LINKED);
        }
        memberAuthEntity.updateProvider(socialCredentials.getProvider());
        memberAuthEntity.updateProviderId(socialCredentials.getProviderId());
        memberAuthJpaRepository.save(memberAuthEntity);
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberAuthEntity.getUuid())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER, TableName.SITE_MEMBER));
        memberEntity.updateLoggedInAt(LocalDateTime.now());
        memberJpaRepository.save(memberEntity);
        return socialIdentityJpaMapper.toSocialMemberProfile(memberEntity, memberAuthEntity);
    }

    @Override
    public void updateSocialUnlinkedMember(AccountId accountId) {
        SiteMemberAuthEntity memberAuthEntity = memberAuthJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH, TableName.SITE_MEMBER_AUTH));
        memberAuthEntity.updateProvider(AuthProvider.BASIC);
        memberAuthEntity.updateProviderId(null);
        memberAuthJpaRepository.save(memberAuthEntity);
    }
}
