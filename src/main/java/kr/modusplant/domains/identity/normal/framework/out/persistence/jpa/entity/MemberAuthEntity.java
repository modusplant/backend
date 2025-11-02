package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.entity;

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

import static kr.modusplant.shared.persistence.constant.TableColumnName.LAST_MODIFIED_AT;
import static kr.modusplant.shared.persistence.constant.TableColumnName.VER_NUM;
import static kr.modusplant.shared.persistence.constant.TableName.SITE_MEMBER_AUTH;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER_AUTH)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAuthEntity {

    @Id
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID originalMemberUuid;

    @Column(name = "act_memb_uuid", nullable = false, updatable = true)
    private UUID activeMemberUuid;

    @Column(nullable = false, length = 80)
    private String email;

    @Column(length = 64)
    private String password;

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

    public void updateActiveMemberUuid(UUID uuid) {
        this.activeMemberUuid = uuid;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateLockoutUntil(LocalDateTime lockoutUntil) {
        this.lockoutUntil = lockoutUntil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberAuthEntity that)) return false;
        return new EqualsBuilder().append(getOriginalMemberUuid(), that.getOriginalMemberUuid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getOriginalMemberUuid()).toHashCode();
    }

    private MemberAuthEntity(UUID originalMemberUuid, UUID activeMemberUuid,
                             String email, String password, AuthProvider provider,
                             String providerId, LocalDateTime lockoutUntil) {
        this.originalMemberUuid = originalMemberUuid;
        this.activeMemberUuid = activeMemberUuid;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
        this.lockoutUntil = lockoutUntil;
    }

    public static MemberAuthEntity.MemberAuthEntityBuilder builder() {
        return new MemberAuthEntity.MemberAuthEntityBuilder();
    }

    public static final class MemberAuthEntityBuilder {
        private UUID originalMemberUuid;
        private UUID activeMemberUuid;
        private String email;
        private String password;
        private AuthProvider provider;
        private String providerId;
        private LocalDateTime lockoutUntil;

        public MemberAuthEntity.MemberAuthEntityBuilder originalMemberUuid(final UUID uuid) {
            this.originalMemberUuid = uuid;
            return this;
        }

        public MemberAuthEntity.MemberAuthEntityBuilder activeMemberUuid(final UUID uuid) {
            this.activeMemberUuid = uuid;
            return this;
        }

        public MemberAuthEntity.MemberAuthEntityBuilder email(final String email) {
            this.email = email;
            return this;
        }

        public MemberAuthEntity.MemberAuthEntityBuilder password(final String password) {
            this.password = password;
            return this;
        }

        public MemberAuthEntity.MemberAuthEntityBuilder provider(final AuthProvider provider) {
            this.provider = provider;
            return this;
        }

        public MemberAuthEntity.MemberAuthEntityBuilder providerId(final String providerId) {
            this.providerId = providerId;
            return this;
        }

        public MemberAuthEntity.MemberAuthEntityBuilder lockoutUntil(final LocalDateTime lockoutUntil) {
            this.lockoutUntil = lockoutUntil;
            return this;
        }

        public MemberAuthEntity.MemberAuthEntityBuilder memberAuthEntity(final MemberAuthEntity memberAuth) {
            this.originalMemberUuid = memberAuth.getOriginalMemberUuid();
            this.activeMemberUuid = memberAuth.getActiveMemberUuid();
            this.email = memberAuth.getEmail();
            this.password = memberAuth.getPassword();
            this.provider = memberAuth.getProvider();
            this.providerId = memberAuth.getProviderId();
            this.lockoutUntil = memberAuth.getLockoutUntil();
            return this;
        }

        public MemberAuthEntity build() {
            return new MemberAuthEntity(this.originalMemberUuid, this.activeMemberUuid, this.email, this.password, this.provider, this.providerId, this.lockoutUntil);
        }
    }
}
