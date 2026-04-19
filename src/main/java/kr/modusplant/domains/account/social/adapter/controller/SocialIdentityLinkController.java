package kr.modusplant.domains.account.social.adapter.controller;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.exception.AlreadyRegisteredWithOtherProviderException;
import kr.modusplant.domains.account.social.domain.exception.SocialAccountConflictException;
import kr.modusplant.domains.account.social.domain.exception.SocialActionRequiredException;
import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClientFactory;
import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.account.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.domains.account.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.shared.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialIdentityLinkController {
    private final SocialAuthClientFactory clientFactory;
    private final SocialIdentityRepository socialIdentityRepository;
    private final SocialIdentityMapper socialIdentityMapper;

    @Transactional
    public void linkSocialAccount(UUID currentMemberUuid, SocialProvider provider, String socialAccessToken) {
        SocialUserInfo user = clientFactory.getClient(provider).getUserInfo(socialAccessToken);
        SocialMemberProfile memberProfile = socialIdentityRepository.getSocialMemberProfileByAccountId(AccountId.fromUuid(currentMemberUuid));
        if (!memberProfile.getEmail().getValue().equals(user.getEmail())) {
            throw new SocialAccountConflictException(SocialIdentityErrorCode.EMAIL_MISMATCH);
        }
        if (memberProfile.getSocialCredentials().isLinked()) {
            throw new SocialAccountConflictException(SocialIdentityErrorCode.ALREADY_LINKED);
        }
        if (memberProfile.getSocialCredentials().isPureSocial()) {
            switch(memberProfile.getSocialCredentials().getProvider()) {
                case KAKAO ->  throw new AlreadyRegisteredWithOtherProviderException(SocialIdentityErrorCode.ALREADY_REGISTERED_WITH_KAKAO);
                case GOOGLE ->  throw new AlreadyRegisteredWithOtherProviderException(SocialIdentityErrorCode.ALREADY_REGISTERED_WITH_GOOGLE);
            }
        }
        AuthProvider linkedAuthProvider = socialIdentityMapper.toLinkedAuthProvider(provider);
        socialIdentityRepository.updateSocialLinkedMember(
                SocialCredentials.create(linkedAuthProvider,user.getId()),
                memberProfile.getEmail()
        );
    }

    @Transactional
    public void unlinkSocialAccount(UUID currentMemberUuid, SocialProvider provider, String socialAccessToken) {
        AccountId accountId = AccountId.fromUuid(currentMemberUuid);
        SocialMemberProfile memberProfile = socialIdentityRepository.getSocialMemberProfileByAccountId(accountId);
        SocialCredentials socialCredentials = memberProfile.getSocialCredentials();
        if (socialCredentials.isPureBasic()) {
            throw new SocialAccountConflictException(SocialIdentityErrorCode.NOT_LINKED);
        } else if (socialCredentials.isPureSocial()) {
            throw new SocialActionRequiredException(SocialIdentityErrorCode.SOCIAL_WITHDRAWAL_REQUIRED);
        } else if (socialCredentials.isLinked() && matches(provider, socialCredentials)) {
            // 연동 해제
            clientFactory.getClient(provider).revokeAccess(socialAccessToken);
            socialIdentityRepository.updateSocialUnlinkedMember(accountId);
        } else {
            throw new SocialAccountConflictException(SocialIdentityErrorCode.PROVIDER_MISMATCH);
        }
    }

    @Transactional
    public void deleteSocialAccount(UUID currentMemberUuid, SocialProvider provider, String socialAccessToken) {
        AccountId accountId = AccountId.fromUuid(currentMemberUuid);
        SocialMemberProfile memberProfile = socialIdentityRepository.getSocialMemberProfileByAccountId(accountId);
        SocialCredentials socialCredentials = memberProfile.getSocialCredentials();
        if (socialCredentials.isPureBasic()) {
            throw new SocialAccountConflictException(SocialIdentityErrorCode.NORMAL_USER_CANNOT_WITHDRAW);
        } else if (socialCredentials.isLinked()) {
            throw new SocialActionRequiredException(SocialIdentityErrorCode.SOCIAL_LINKAGE_REQUIRED);
        } else if (socialCredentials.isPureSocial() && matches(provider, socialCredentials)) {
            clientFactory.getClient(provider).revokeAccess(socialAccessToken);
        } else {
            throw new SocialAccountConflictException(SocialIdentityErrorCode.PROVIDER_MISMATCH);
        }
    }

    private boolean matches(SocialProvider socialProvider, SocialCredentials socialCredentials) {
        return switch (socialProvider) {
            case KAKAO ->  socialCredentials.isKakao();
            case GOOGLE -> socialCredentials.isGoogle();
        };
    }

}
