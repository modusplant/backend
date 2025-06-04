package kr.modusplant.modules.auth.normal.signup.app.http.request;

public record NormalSignUpRequest(
        String email,
        String pw,
        String nickname,
        String agreedTermsOfUseVersion,
        String agreedPrivacyPolicyVersion,
        String agreedAdInfoReceivingVersion) {}
