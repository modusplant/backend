package kr.modusplant.api.signup.normal.model.request;

public record NormalSignUpRequest(
        String email,
        String pw,
        String pw_check,
        String nickname,
        String agreedTermsOfUseVerion,
        String agreedPrivacyPolicyVerion,
        String agreedAdInfoRecevingVerion) {}
