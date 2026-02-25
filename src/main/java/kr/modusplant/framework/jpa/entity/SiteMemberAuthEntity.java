package kr.modusplant.framework.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.shared.enums.AuthProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.LAST_MODIFIED_AT;
import static kr.modusplant.shared.persistence.constant.TableColumnName.VER_NUM;
import static kr.modusplant.shared.persistence.constant.TableName.SITE_MEMBER_AUTH;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER_AUTH)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class SiteMemberAuthEntity {
    @Id
    @ToString.Include
    private UUID uuid;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId
    @JoinColumn(nullable = false, name = "uuid", updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity member;

    @Column(nullable = false, length = 80)
    private String email;

    @Column(length = 64)
    private String pw;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private AuthProvider provider;

    @Column(unique = true, updatable = false, name = "provider_id")
    private String providerId;

    @Column(name = "lockout_until")
    private LocalDateTime lockoutUntil;

    @Column(name = LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    @ToString.Include
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = VER_NUM, nullable = false)
    private Long versionNumber;

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePw(String pw) {
        this.pw = pw;
    }

    public String getETagSource() {
        return getUuid() + "-" + getVersionNumber();
    }

    public LocalDateTime getLastModifiedAtAsTruncatedToSeconds() {
        return getLastModifiedAt().truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteMemberAuthEntity that)) return false;
        return new EqualsBuilder().append(getMember(), that.getMember()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMember()).toHashCode();
    }

    private SiteMemberAuthEntity(SiteMemberEntity member, String email, String pw, AuthProvider provider, String providerId, LocalDateTime lockoutUntil) {
        this.member = member;
        this.email = email;
        this.pw = pw;
        this.provider = provider;
        this.providerId = providerId;
        this.lockoutUntil = lockoutUntil;
    }

    public static SiteMemberAuthEntityBuilder builder() {
        return new SiteMemberAuthEntityBuilder();
    }

    public static final class SiteMemberAuthEntityBuilder {
        private SiteMemberEntity member;
        private String email;
        private String pw;
        private AuthProvider provider;
        private String providerId;
        private LocalDateTime lockoutUntil;

        public SiteMemberAuthEntityBuilder member(final SiteMemberEntity member) {
            this.member = member;
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

        public SiteMemberAuthEntityBuilder lockoutUntil(final LocalDateTime lockoutUntil) {
            this.lockoutUntil = lockoutUntil;
            return this;
        }

        public SiteMemberAuthEntityBuilder memberAuth(final SiteMemberAuthEntity memberAuth) {
            this.member = memberAuth.getMember();
            this.email = memberAuth.getEmail();
            this.pw = memberAuth.getPw();
            this.provider = memberAuth.getProvider();
            this.providerId = memberAuth.getProviderId();
            this.lockoutUntil = memberAuth.getLockoutUntil();
            return this;
        }

        public SiteMemberAuthEntity build() {
            return new SiteMemberAuthEntity(this.member, this.email, this.pw, this.provider, this.providerId, this.lockoutUntil);
        }
    }
}
