package kr.modusplant.infrastructure.jwt.common.util.entity;

import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.infrastructure.jwt.framework.out.jpa.entity.RefreshTokenEntity.RefreshTokenEntityBuilder;
import static kr.modusplant.infrastructure.jwt.framework.out.jpa.entity.RefreshTokenEntity.builder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public interface RefreshTokenEntityTestUtils extends SiteMemberEntityTestUtils {
    default RefreshTokenEntityBuilder createRefreshTokenBasicEntityBuilder() {
        return builder()
                .refreshToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIn0.sFzQWkpK8HG2xKcI1vNH3oW7nIO9QaX3ghTkfT2Yq3s")
                .issuedAt(convertToLocalDateTime(new Date()))
                .expiredAt(convertToLocalDateTime(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)));
    }

    private static LocalDateTime convertToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }
}
