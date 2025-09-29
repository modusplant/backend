package kr.modusplant.legacy.modules.jwt.common.util.entity;

import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityConstant;
import kr.modusplant.legacy.modules.jwt.common.util.domain.RefreshTokenTestUtils;
import kr.modusplant.legacy.modules.jwt.persistence.entity.RefreshTokenEntity.RefreshTokenEntityBuilder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static kr.modusplant.legacy.modules.jwt.persistence.entity.RefreshTokenEntity.builder;

public interface RefreshTokenEntityTestUtils extends SiteMemberEntityConstant {
    default RefreshTokenEntityBuilder createRefreshTokenBasicEntityBuilder() {
        return builder()
                .refreshToken(RefreshTokenTestUtils.refreshTokenBasicUser.getRefreshToken())
                .issuedAt(convertToLocalDateTime(RefreshTokenTestUtils.refreshTokenBasicUser.getIssuedAt()))
                .expiredAt(convertToLocalDateTime(RefreshTokenTestUtils.refreshTokenBasicUser.getExpiredAt()));
    }

    private static LocalDateTime convertToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }
}
