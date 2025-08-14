package kr.modusplant.legacy.domains.member.persistence.entity;

import jakarta.persistence.*;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.database.TableColumnName.LAST_MODIFIED_AT;
import static kr.modusplant.shared.database.TableColumnName.VER_NUM;
import static kr.modusplant.shared.database.TableName.SITE_MEMBER_AUTH;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER_AUTH)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SiteMemberAuthEntity {
    @Id
    private UUID uuid;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId
    @JoinColumn(nullable = false, name = "uuid", updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity originalMember;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(nullable = false, name = "act_memb_uuid", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity activeMember;

    @Column(nullable = false, length = 80)
    private String email;

    @Column(length = 64)
    private String pw;

    @Column(nullable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    private AuthProvider provider;

    @Column(unique = true, updatable = false, name = "provider_id")
    private String providerId;

    @Column(name = "lockout_until")
    private LocalDateTime lockoutUntil;

    @Column(name = LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = VER_NUM, nullable = false)
    private Long versionNumber;

    public void updateActiveMember(SiteMemberEntity activeMember) {
        this.activeMember = activeMember;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePw(String pw) {
        this.pw = pw;
    }

    public void updateLockoutUntil(LocalDateTime lockoutUntil) {
        this.lockoutUntil = lockoutUntil;
    }

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

    private SiteMemberAuthEntity(SiteMemberEntity originalMember, SiteMemberEntity activeMember, String email, String pw, AuthProvider provider, String providerId, LocalDateTime lockoutUntil) {
        this.originalMember = originalMember;
        this.activeMember = activeMember;
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
        private SiteMemberEntity originalMember;
        private SiteMemberEntity activeMember;
        private String email;
        private String pw;
        private AuthProvider provider;
        private String providerId;
        private LocalDateTime lockoutUntil;

        public SiteMemberAuthEntityBuilder originalMember(final SiteMemberEntity originalMember) {
            this.originalMember = originalMember;
            return this;
        }

        public SiteMemberAuthEntityBuilder activeMember(final SiteMemberEntity activeMember) {
            this.activeMember = activeMember;
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

        public SiteMemberAuthEntityBuilder memberAuthEntity(final SiteMemberAuthEntity memberAuth) {
            this.originalMember = memberAuth.getOriginalMember();
            this.activeMember = memberAuth.getActiveMember();
            this.email = memberAuth.getEmail();
            this.pw = memberAuth.getPw();
            this.provider = memberAuth.getProvider();
            this.providerId = memberAuth.getProviderId();
            this.lockoutUntil = memberAuth.getLockoutUntil();
            return this;
        }

        public SiteMemberAuthEntity build() {
            return new SiteMemberAuthEntity(this.originalMember, this.activeMember, this.email, this.pw, this.provider, this.providerId, this.lockoutUntil);
        }
    }
}
