package kr.modusplant.domains.account.social.usecase.record;

public record SocialUserInfo(
        String socialAccessToken,

        String id,

        String email,

        String nickname
) {
}
