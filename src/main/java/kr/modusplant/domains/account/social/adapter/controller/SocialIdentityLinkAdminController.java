package kr.modusplant.domains.account.social.adapter.controller;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.exception.SocialAccountConflictException;
import kr.modusplant.domains.account.social.domain.exception.SocialActionRequiredException;
import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.account.social.usecase.port.repository.SocialIdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SocialIdentityLinkAdminController {
    private final SocialIdentityRepository socialIdentityRepository;

    public void removeSocialLink(UUID currentMemberUuid) {
        AccountId accountId = AccountId.fromUuid(currentMemberUuid);
        SocialMemberProfile memberProfile = socialIdentityRepository.getSocialMemberProfileByAccountId(accountId);
        SocialCredentials socialCredentials = memberProfile.getSocialCredentials();
        if (socialCredentials.isPureBasic()) {
            throw new SocialAccountConflictException(SocialIdentityErrorCode.NOT_LINKED);
        } else if (socialCredentials.isPureSocial()) {
            throw new SocialActionRequiredException(SocialIdentityErrorCode.SOCIAL_WITHDRAWAL_REQUIRED);
        } else {
            socialIdentityRepository.updateSocialUnlinkedMember(accountId);
        }
    }
}
