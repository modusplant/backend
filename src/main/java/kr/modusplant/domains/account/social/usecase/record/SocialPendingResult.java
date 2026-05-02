package kr.modusplant.domains.account.social.usecase.record;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;

public sealed interface SocialPendingResult extends SocialLoginResult permits NeedSignupResult, NeedLinkResult {
    String email();
    String providerId();
    SocialProvider socialProvider();
    String socialAccessToken();
}
