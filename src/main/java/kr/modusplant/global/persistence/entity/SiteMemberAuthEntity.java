package kr.modusplant.global.persistence.entity;

import jakarta.persistence.*;
import kr.modusplant.global.enums.AuthProvider;
import kr.modusplant.global.persistence.annotation.DefaultValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.PROVIDER;
import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SNAKE_SITE_MEMBER_AUTH)
@Getter
@NoArgsConstructor
public class SiteMemberAuthEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID uuid;

    @ManyToOne
    @Setter
    @MapsId
    @JoinColumn(name = SNAKE_ACTIVE_MEMBER_UUID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity activeMember;

    @OneToOne
    @Setter
    @MapsId
    @JoinColumn(name = SNAKE_ORIGINAL_MEMBER_UUID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity originalMember;

    @Setter
    @Column(nullable = false, length = 80)
    private String email;

    @Setter
    @Column(nullable = false, length = 64)
    private String pw;

    @Setter
    @Column(name = PROVIDER, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AuthProvider provider;

    @Setter
    @Column(name = SNAKE_PROVIDER_ID, nullable = false)
    private String providerId;

    @Setter
    @Column(name = SNAKE_FAILED_ATTEMPT, nullable = false)
    @DefaultValue
    private Integer failedAttempt;

    @Setter
    @Column(name = SNAKE_LOCKOUT_REFRESH_AT, nullable = false)
    private LocalDateTime lockoutRefreshAt;

    @Setter
    @Column(name = SNAKE_LOCKOUT_UNTIL, nullable = false)
    private LocalDateTime lockoutUntil;

    @Column(name = SNAKE_LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = SNAKE_VER_NUM, nullable = false)
    private Long versionNumber;

    public SiteMemberAuthEntity(SiteMemberEntity activeMember, SiteMemberEntity originalMember, String email, String pw, AuthProvider provider, String providerId, Integer failedAttempt, LocalDateTime lockoutRefreshAt, LocalDateTime lockoutUntil) {
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
        private SiteMemberEntity activeMember;
        private SiteMemberEntity originalMember;
        private String email;
        private String pw;
        private AuthProvider provider;
        private String providerId;
        private Integer failedAttempt;
        private LocalDateTime lockoutRefreshAt;
        private LocalDateTime lockoutUntil;

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
            return new SiteMemberAuthEntity(this.activeMember, this.originalMember, this.email, this.pw, this.provider, this.providerId, this.failedAttempt, this.lockoutRefreshAt, this.lockoutUntil);
        }
    }
}
