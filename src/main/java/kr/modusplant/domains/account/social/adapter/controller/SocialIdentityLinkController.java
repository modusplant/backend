package kr.modusplant.domains.account.social.adapter.controller;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.exception.AlreadyRegisteredWithOtherProviderException;
import kr.modusplant.domains.account.social.domain.exception.SocialAccountConflictException;
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

}
