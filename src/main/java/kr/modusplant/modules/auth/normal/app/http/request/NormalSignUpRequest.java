package kr.modusplant.modules.auth.normal.app.http.request;

public record NormalSignUpRequest(
        String email,
        String pw,
        String pw_check,
        String nickname,
        String agreedTermsOfUseVerion,
        String agreedPrivacyPolicyVerion,
        String agreedAdInfoRecevingVerion) {}
