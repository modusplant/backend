package kr.modusplant.domains.account.social.usecase.record;

public sealed interface SocialLoginResult permits LoginResult, SocialPendingResult {
}
