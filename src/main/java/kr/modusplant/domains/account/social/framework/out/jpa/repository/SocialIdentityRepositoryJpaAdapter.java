package kr.modusplant.domains.account.social.framework.out.jpa.repository;

import kr.modusplant.domains.account.identity.framework.out.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.account.identity.framework.out.jpa.repository.MemberAuthJpaRepository;
import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.exception.SocialAccountConflictException;
import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.domains.account.social.domain.vo.AgreedTerms;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.account.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.domains.account.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberProfileJpaRepository;
import kr.modusplant.domains.term.framework.out.jpa.repository.MemberTermJpaRepository;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
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
    private final MemberJpaRepository memberJpaRepository;
    private final MemberAuthJpaRepository memberAuthJpaRepository;
    private final MemberProfileJpaRepository memberProfileJpaRepository;
    private final MemberTermJpaRepository memberTermJpaRepository;
    private final SocialIdentityJpaMapper socialIdentityJpaMapper;

    public Optional<SocialMemberProfile> getSocialMemberProfileByEmail(Email email) {
        return memberAuthJpaRepository.findByEmail(email.getValue())
                .map(memberAuthEntity -> socialIdentityJpaMapper.toSocialMemberProfile(memberAuthEntity.getMember(),memberAuthEntity));
    }

    @Override
    public SocialMemberProfile getSocialMemberProfileByAccountId(AccountId accountId) {
        MemberAuthEntity memberAuthEntity = memberAuthJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH, TableName.SITE_MEMBER_AUTH));
        return socialIdentityJpaMapper.toSocialMemberProfile(memberAuthEntity.getMember(),memberAuthEntity);
    }

    @Override
    public SocialMemberProfile updateLoggedInAtAndGetProfile(AccountId accountId) {
        MemberEntity memberEntity = memberJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow();
        memberEntity.updateLoggedInAt(LocalDateTime.now());
        memberJpaRepository.save(memberEntity);
        MemberAuthEntity memberAuthEntity = memberAuthJpaRepository.findByMember(memberEntity)
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH, TableName.SITE_MEMBER_AUTH));
        return socialIdentityJpaMapper.toSocialMemberProfile(memberEntity, memberAuthEntity);
    }

    @Override
    public SocialMemberProfile saveSocialMember(SocialMemberProfile profile, String introduction, AgreedTerms agreedTerms) {
        MemberEntity memberEntity = memberJpaRepository.save(socialIdentityJpaMapper.toMemberEntity(profile.getNickname(), profile.getRole()));
        MemberAuthEntity memberAuthEntity = memberAuthJpaRepository.save(socialIdentityJpaMapper.toMemberAuthEntity(memberEntity, profile.getSocialCredentials(), profile.getEmail()));
        memberProfileJpaRepository.save(socialIdentityJpaMapper.toMemberProfileEntity(memberEntity,introduction));
        memberTermJpaRepository.save(socialIdentityJpaMapper.toMemberTermEntity(memberEntity,agreedTerms));
        return socialIdentityJpaMapper.toSocialMemberProfile(memberEntity, memberAuthEntity);
    }

    @Override
    public SocialMemberProfile updateSocialLinkedMember(SocialCredentials socialCredentials, Email email) {
        MemberAuthEntity memberAuthEntity = memberAuthJpaRepository.findByEmail(email.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH, TableName.SITE_MEMBER_AUTH));
        if (memberAuthEntity.getProvider().equals(socialCredentials.getProvider())) {
            throw new SocialAccountConflictException(SocialIdentityErrorCode.ALREADY_LINKED);
        }
        memberAuthEntity.updateProvider(socialCredentials.getProvider());
        memberAuthEntity.updateProviderId(socialCredentials.getProviderId());
        memberAuthJpaRepository.save(memberAuthEntity);
        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberAuthEntity.getUuid())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER, TableName.SITE_MEMBER));
        memberEntity.updateLoggedInAt(LocalDateTime.now());
        memberJpaRepository.save(memberEntity);
        return socialIdentityJpaMapper.toSocialMemberProfile(memberEntity, memberAuthEntity);
    }

    @Override
    public void updateSocialUnlinkedMember(AccountId accountId) {
        MemberAuthEntity memberAuthEntity = memberAuthJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH, TableName.SITE_MEMBER_AUTH));
        memberAuthEntity.updateProvider(AuthProvider.BASIC);
        memberAuthEntity.updateProviderId(null);
        memberAuthJpaRepository.save(memberAuthEntity);
    }
}
