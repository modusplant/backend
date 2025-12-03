package kr.modusplant.infrastructure.jwt.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CookieName {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
}
