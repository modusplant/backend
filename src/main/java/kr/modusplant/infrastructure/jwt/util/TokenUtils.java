package kr.modusplant.infrastructure.jwt.util;

import kr.modusplant.infrastructure.jwt.exception.InvalidTokenException;

public abstract class TokenUtils {
    public static String getTokenFromAuthorizationHeader(String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new InvalidTokenException();
        }
        return auth.substring(7);
    }
}
