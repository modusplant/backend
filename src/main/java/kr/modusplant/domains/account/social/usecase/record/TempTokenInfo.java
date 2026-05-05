package kr.modusplant.domains.account.social.usecase.record;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;

public record TempTokenInfo(
        String email,
        String providerId,
        SocialProvider socialProvider
) {
}
