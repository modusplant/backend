package kr.modusplant.domains.account.social.usecase.response;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;

public record NeedSignupResult(
        String email,
        String nickname,
        String providerId,
        SocialProvider socialProvider
) implements SocialLoginResult {
}
