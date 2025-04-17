package kr.modusplant.modules.jwt.persistence.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Entity
@Table(name = SNAKE_REFRESH_TOKEN)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenEntity {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, name = SNAKE_MEMB_UUID, foreignKey = @ForeignKey(name = SNAKE_FK_TOKEN_MEMB_UUID, value = ConstraintMode.CONSTRAINT))
    private SiteMemberEntity member;

    @Column(name = SNAKE_DEVICE_ID, nullable = false, unique = true)
    private UUID deviceId;

    @Column(name = SNAKE_REFRESH_TOKEN, nullable = false)
    private String refreshToken;

    @Column(name = SNAKE_ISSUED_AT)
    private LocalDateTime issuedAt;

    @Column(name = SNAKE_EXPIRED_AT)
    private LocalDateTime expiredAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefreshTokenEntity that)) return false;
        return new EqualsBuilder().append(getMember(), that.getMember()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMember()).toHashCode();
    }

    private RefreshTokenEntity(UUID uuid, SiteMemberEntity member, UUID deviceId, String refreshToken, LocalDateTime issuedAt, LocalDateTime expiredAt) {
        this.uuid = uuid;
        this.member = member;
        this.deviceId = deviceId;
        this.refreshToken = refreshToken;
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
    }

    public static RefreshTokenEntityBuilder builder() {
        return new RefreshTokenEntityBuilder();
    }

    public static final class RefreshTokenEntityBuilder {
        private UUID uuid;
        private SiteMemberEntity member;
        private UUID deviceId;
        private String refreshToken;
        private LocalDateTime issuedAt;
        private LocalDateTime expiredAt;

        public RefreshTokenEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public RefreshTokenEntityBuilder member(final SiteMemberEntity member) {
            this.member = member;
            return this;
        }

        public RefreshTokenEntityBuilder deviceId(final UUID deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public RefreshTokenEntityBuilder refreshToken(final String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public RefreshTokenEntityBuilder issuedAt(final LocalDateTime issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public RefreshTokenEntityBuilder expiredAt(final LocalDateTime expiredAt) {
            this.expiredAt = expiredAt;
            return this;
        }

        public RefreshTokenEntityBuilder tokenEntity(final RefreshTokenEntity token) {
            this.uuid = token.getUuid();
            this.member = token.getMember();
            this.deviceId = token.getDeviceId();
            this.refreshToken = token.getRefreshToken();
            this.issuedAt = token.getIssuedAt();
            this.expiredAt = token.getExpiredAt();
            return this;
        }

        public RefreshTokenEntity build() {
            return new RefreshTokenEntity(this.uuid, this.member, this.deviceId, this.refreshToken, this.issuedAt, this.expiredAt);
        }
    }
}
