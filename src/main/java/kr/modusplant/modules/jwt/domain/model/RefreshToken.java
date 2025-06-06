package kr.modusplant.modules.jwt.domain.model;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class RefreshToken {
    private final UUID uuid;

    private final UUID memberUuid;

    private final String refreshToken;

    private final Date issuedAt;

    private final Date expiredAt;

    public static class TokenBuilder {
        private UUID uuid;
        private UUID memberUuid;
        private String refreshToken;
        private Date issuedAt;
        private Date expiredAt;

        public TokenBuilder token(RefreshToken refreshToken) {
            this.uuid = refreshToken.getUuid();
            this.memberUuid = refreshToken.getMemberUuid();
            this.refreshToken = refreshToken.getRefreshToken();
            this.issuedAt = refreshToken.getIssuedAt();
            this.expiredAt = refreshToken.getExpiredAt();
            return this;
        }

        public RefreshToken build() {
            return new RefreshToken(this.uuid, this.memberUuid, this.refreshToken, this.issuedAt, this.expiredAt);
        }
    }
}
