package kr.modusplant.modules.jwt.common.util.domain;

import kr.modusplant.modules.jwt.domain.model.RefreshToken;

import java.util.Date;

public interface RefreshTokenTestUtils {
    RefreshToken refreshTokenBasicUser = RefreshToken.builder()
            .refreshToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIn0.sFzQWkpK8HG2xKcI1vNH3oW7nIO9QaX3ghTkfT2Yq3s")
            .issuedAt(new Date())
            .expiredAt(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7))  // 7일 후
            .build();
}