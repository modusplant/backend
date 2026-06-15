package kr.modusplant.domains.member.adapter.translator;

import jakarta.annotation.PostConstruct;
import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityLinkController;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.framework.outbound.exception.UnsupportedSocialProviderException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberSocialTranslator {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    private boolean isLocal;

    private final SocialIdentityLinkController socialIdentityLinkController;

    @PostConstruct
    public void initIsLocal() {
        isLocal = activeProfile.equals("local");
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
