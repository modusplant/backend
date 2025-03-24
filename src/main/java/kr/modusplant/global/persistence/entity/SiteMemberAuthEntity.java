package kr.modusplant.global.persistence.entity;

import jakarta.persistence.*;
import kr.modusplant.global.enums.AuthProvider;
import kr.modusplant.global.persistence.annotation.DefaultValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SNAKE_SITE_MEMBER_AUTH)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SiteMemberAuthEntity {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(nullable = false, name = SNAKE_ACT_MEMB_UUID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity activeMember;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(nullable = false, unique = true, updatable = false, name = SNAKE_ORI_MEMB_UUID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity originalMember;

    @Column(nullable = false, length = 80)
    private String email;

    @Column(length = 64)
    private String pw;

    @Column(nullable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    private AuthProvider provider;

    @Column(unique = true, updatable = false, name = SNAKE_PROVIDER_ID)
    private String providerId;

    @Column(name = SNAKE_FAILED_ATTEMPT, nullable = false)
    @DefaultValue
    private Integer failedAttempt;

    @Column(name = SNAKE_LOCKOUT_REFRESH_AT)
    private LocalDateTime lockoutRefreshAt;

    @Column(name = SNAKE_LOCKOUT_UNTIL)
    private LocalDateTime lockoutUntil;

    @Column(name = SNAKE_LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = SNAKE_VER_NUM, nullable = false)
    private Long versionNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteMemberAuthEntity that)) return false;
        return new EqualsBuilder().append(getOriginalMember(), that.getOriginalMember()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getOriginalMember()).toHashCode();
    }

    @PrePersist
    public void prePersist() {
        if (this.failedAttempt == null) {
            this.failedAttempt = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.failedAttempt == null) {
            this.failedAttempt = 0;
        }
    }

    private SiteMemberAuthEntity(UUID uuid, SiteMemberEntity activeMember, SiteMemberEntity originalMember, String email, String pw, AuthProvider provider, String providerId, Integer failedAttempt, LocalDateTime lockoutRefreshAt, LocalDateTime lockoutUntil) {
        this.uuid = uuid;
        this.activeMember = activeMember;
        this.originalMember = originalMember;
        this.email = email;
        this.pw = pw;
        this.provider = provider;
        this.providerId = providerId;
        this.failedAttempt = failedAttempt;
        this.lockoutRefreshAt = lockoutRefreshAt;
        this.lockoutUntil = lockoutUntil;
    }

    public static SiteMemberAuthEntityBuilder builder() {
        return new SiteMemberAuthEntityBuilder();
    }

    public static final class SiteMemberAuthEntityBuilder {
        private UUID uuid;
        private SiteMemberEntity activeMember;
        private SiteMemberEntity originalMember;
        private String email;
        private String pw;
        private AuthProvider provider;
        private String providerId;
        private Integer failedAttempt;
        private LocalDateTime lockoutRefreshAt;
        private LocalDateTime lockoutUntil;

        public SiteMemberAuthEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public SiteMemberAuthEntityBuilder activeMember(final SiteMemberEntity activeMember) {
            this.activeMember = activeMember;
            return this;
        }

        public SiteMemberAuthEntityBuilder originalMember(final SiteMemberEntity originalMember) {
            this.originalMember = originalMember;
            return this;
        }

        public SiteMemberAuthEntityBuilder email(final String email) {
            this.email = email;
            return this;
        }

        public SiteMemberAuthEntityBuilder pw(final String pw) {
            this.pw = pw;
            return this;
        }

        public SiteMemberAuthEntityBuilder provider(final AuthProvider provider) {
            this.provider = provider;
            return this;
        }

        public SiteMemberAuthEntityBuilder providerId(final String providerId) {
            this.providerId = providerId;
            return this;
        }

        public SiteMemberAuthEntityBuilder failedAttempt(final Integer failedAttempt) {
            this.failedAttempt = failedAttempt;
            return this;
        }

        public SiteMemberAuthEntityBuilder lockoutRefreshAt(final LocalDateTime lockoutRefreshAt) {
            this.lockoutRefreshAt = lockoutRefreshAt;
            return this;
        }

        public SiteMemberAuthEntityBuilder lockoutUntil(final LocalDateTime lockoutUntil) {
            this.lockoutUntil = lockoutUntil;
            return this;
        }

        public SiteMemberAuthEntityBuilder memberAuthEntity(final SiteMemberAuthEntity memberAuth) {
            this.uuid = memberAuth.getUuid();
            this.activeMember = memberAuth.getActiveMember();
            this.originalMember = memberAuth.getOriginalMember();
            this.email = memberAuth.getEmail();
            this.pw = memberAuth.getPw();
            this.provider = memberAuth.getProvider();
            this.providerId = memberAuth.getProviderId();
            this.failedAttempt = memberAuth.getFailedAttempt();
            this.lockoutRefreshAt = memberAuth.getLockoutRefreshAt();
            this.lockoutUntil = memberAuth.getLockoutUntil();
            return this;
        }

        public SiteMemberAuthEntity build() {
            return new SiteMemberAuthEntity(this.uuid, this.activeMember, this.originalMember, this.email, this.pw, this.provider, this.providerId, this.failedAttempt, this.lockoutRefreshAt, this.lockoutUntil);
        }
    }
}
