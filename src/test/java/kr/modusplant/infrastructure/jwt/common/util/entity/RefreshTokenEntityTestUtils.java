package kr.modusplant.infrastructure.jwt.common.util.entity;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.infrastructure.jwt.framework.outbound.jpa.entity.RefreshTokenEntity.RefreshTokenEntityBuilder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static kr.modusplant.infrastructure.jwt.framework.outbound.jpa.entity.RefreshTokenEntity.builder;

public interface RefreshTokenEntityTestUtils extends MemberEntityTestUtils {
    default RefreshTokenEntityBuilder createRefreshTokenBasicEntityBuilder() {
        return builder()
                .refreshToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIn0.sFzQWkpK8HG2xKcI1vNH3oW7nIO9QaX3ghTkfT2Yq3s")
                .expiredAt(convertToLocalDateTime(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)));
    }

    private static LocalDateTime convertToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }
}
