package kr.modusplant.modules.jwt.common.util.domain;

import kr.modusplant.modules.jwt.domain.model.RefreshToken;

import java.util.Date;
import java.util.UUID;

public interface RefreshTokenTestUtils {
    RefreshToken refreshTokenBasicUser = RefreshToken.builder()
            .deviceId(UUID.fromString("378c0ca1-b67f-4ae7-a43b-e6cf583b7667"))
            .refreshToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIn0.sFzQWkpK8HG2xKcI1vNH3oW7nIO9QaX3ghTkfT2Yq3s")
            .issuedAt(new Date())
            .expiredAt(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7))  // 7일 후
            .build();
}