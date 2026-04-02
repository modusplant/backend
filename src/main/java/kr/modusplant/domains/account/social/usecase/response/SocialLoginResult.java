package kr.modusplant.domains.account.social.usecase.response;

public sealed interface SocialLoginResult permits LoginResult, NeedSignupResult, NeedLinkResult {
}
