package kr.modusplant.domains.member.adapter.translator;

import jakarta.annotation.PostConstruct;
import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityLinkController;
import kr.modusplant.domains.account.social.domain.exception.SocialAccountConflictException;
import kr.modusplant.domains.account.social.domain.exception.SocialActionRequiredException;
import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.framework.outbound.exception.UnsupportedSocialProviderException;
import kr.modusplant.domains.account.social.usecase.port.repository.SocialIdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberSocialTranslator {
    private boolean isLocal;

    private final SocialIdentityLinkController socialIdentityLinkController;
    private final SocialIdentityRepository socialIdentityRepository;
    private final Environment environment;

    @PostConstruct
    public void initIsLocal() {
        isLocal = environment.acceptsProfiles(Profiles.of("local"));
    }

    public String getSocialAccessToken(String authCode, String authProvider) {
        String upperCaseAuthProvider = authProvider.toUpperCase(Locale.ROOT);
        if (upperCaseAuthProvider.equals(SocialProvider.KAKAO.name())) {
            return socialIdentityLinkController.issueSocialToken(
                    SocialProvider.KAKAO, authCode, isLocal).socialAccessToken();
        } else if (upperCaseAuthProvider.equals(SocialProvider.GOOGLE.name())) {
            return socialIdentityLinkController.issueSocialToken(
                    SocialProvider.GOOGLE, authCode, isLocal).socialAccessToken();
        } else {
            throw new UnsupportedSocialProviderException();
        }
    }

    public void validateIfSocialAccountDeletable(String authProvider, UUID memberId) {
        String upperCaseAuthProvider = authProvider.toUpperCase(Locale.ROOT);
        if (upperCaseAuthProvider.equals(SocialProvider.KAKAO.name())) {
            validateSocialAccountDeletion(memberId, SocialProvider.KAKAO);
        } else if (upperCaseAuthProvider.equals(SocialProvider.GOOGLE.name())) {
            validateSocialAccountDeletion(memberId, SocialProvider.GOOGLE);
        } else {
            throw new UnsupportedSocialProviderException();
        }
    }

    private void validateSocialAccountDeletion(UUID memberId, SocialProvider provider) {
        SocialCredentials socialCredentials = socialIdentityRepository
                .getSocialMemberProfileByAccountId(AccountId.fromUuid(memberId))
                .getSocialCredentials();
        if (socialCredentials.isPureBasic()) {
            throw new SocialAccountConflictException(SocialIdentityErrorCode.NORMAL_USER_CANNOT_WITHDRAW);
        } else if (socialCredentials.isLinked()) {
            throw new SocialActionRequiredException(SocialIdentityErrorCode.SOCIAL_LINKAGE_REQUIRED);
        } else if (!socialCredentials.isPureSocial() || !matches(provider, socialCredentials)) {
            throw new SocialAccountConflictException(SocialIdentityErrorCode.PROVIDER_MISMATCH);
        }
    }

    private boolean matches(SocialProvider socialProvider, SocialCredentials socialCredentials) {
        return switch (socialProvider) {
            case KAKAO -> socialCredentials.isKakao();
            case GOOGLE -> socialCredentials.isGoogle();
        };
    }
}
