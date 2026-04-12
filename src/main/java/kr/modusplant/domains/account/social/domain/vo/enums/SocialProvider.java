package kr.modusplant.domains.account.social.domain.vo.enums;

import lombok.Getter;

@Getter
public enum SocialProvider {
    GOOGLE("google"),
    KAKAO("kakao");

    private final String value;

    SocialProvider(String value) {
        this.value = value;
    }
}
