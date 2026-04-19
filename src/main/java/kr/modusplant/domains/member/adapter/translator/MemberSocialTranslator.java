package kr.modusplant.domains.member.adapter.translator;

import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityController;
import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityLinkController;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.framework.out.exception.UnsupportedSocialProviderException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberSocialTranslator {
    private final SocialIdentityController socialIdentityController;
    private final SocialIdentityLinkController socialIdentityLinkController;

    public String getSocialAccessToken(String authCode, String authProvider) {
        String upperCaseAuthProvider = authProvider.toUpperCase(Locale.ROOT);
        if (upperCaseAuthProvider.equals(SocialProvider.KAKAO.name())) {
            return socialIdentityController.issueSocialAccessToken(
                    SocialProvider.KAKAO, authCode);
        } else if (upperCaseAuthProvider.equals(SocialProvider.GOOGLE.name())) {
            return socialIdentityController.issueSocialAccessToken(
                    SocialProvider.GOOGLE, authCode);
        } else {
            throw new UnsupportedSocialProviderException();
        }
    }

    public void deleteSocialAccountWithSocialAccessToken(String socialAccessToken, String authProvider, UUID memberId) {
        String upperCaseAuthProvider = authProvider.toUpperCase(Locale.ROOT);
        if (upperCaseAuthProvider.equals(SocialProvider.KAKAO.name())) {
            socialIdentityLinkController.deleteSocialAccount(
                    memberId, SocialProvider.KAKAO, socialAccessToken);
        } else if (upperCaseAuthProvider.equals(SocialProvider.GOOGLE.name())) {
            socialIdentityLinkController.deleteSocialAccount(
                    memberId, SocialProvider.GOOGLE, socialAccessToken);
        } else {
            throw new UnsupportedSocialProviderException();
        }
    }
}
