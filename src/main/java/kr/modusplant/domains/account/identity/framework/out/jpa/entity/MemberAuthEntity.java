package kr.modusplant.domains.account.identity.framework.out.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
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
public class MemberAuthEntity {
    @Id
    @ToString.Include
    private UUID uuid;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(nullable = false, name = "uuid", updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private MemberEntity member;

    @Column(nullable = false, unique = true, length = 80)
    private String email;

    @Column(length = 64)
    private String pw;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private AuthProvider provider;

    @Column(unique = true, name = "provider_id")
    private String providerId;

    @Column(name = LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    @ToString.Include
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = VER_NUM, nullable = false)
    private Long versionNumber;

    public String getETagSource() {
        return getUuid() + "-" + getVersionNumber();
    }

    public LocalDateTime getLastModifiedAtAsTruncatedToSeconds() {
        return getLastModifiedAt().truncatedTo(ChronoUnit.SECONDS);
    }

    public void updateAuthProvider(AuthProvider provider) { this.provider = provider; }

    public void updateEmail(String email) { this.email = email; }

    public void updatePassword(String password) { this.pw = password; }

    public void updateProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public void updateProviderId(String providerId) {
        this.providerId = providerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberAuthEntity that)) return false;
        return new EqualsBuilder().append(getMember(), that.getMember()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMember()).toHashCode();
    }

    private MemberAuthEntity(MemberEntity member, String email, String pw, AuthProvider provider, String providerId) {
        this.member = member;
        this.email = email;
        this.pw = pw;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static MemberAuthEntityBuilder builder() {
        return new MemberAuthEntityBuilder();
    }

    public static final class MemberAuthEntityBuilder {
        private MemberEntity member;
        private String email;
        private String pw;
        private AuthProvider provider;
        private String providerId;

        public MemberAuthEntityBuilder member(final MemberEntity member) {
            this.member = member;
            return this;
        }

        public MemberAuthEntityBuilder email(final String email) {
            this.email = email;
            return this;
        }

        public MemberAuthEntityBuilder pw(final String pw) {
            this.pw = pw;
            return this;
        }

        public MemberAuthEntityBuilder provider(final AuthProvider provider) {
            this.provider = provider;
            return this;
        }

        public MemberAuthEntityBuilder providerId(final String providerId) {
            this.providerId = providerId;
            return this;
        }

        public MemberAuthEntityBuilder memberAuth(final MemberAuthEntity memberAuth) {
            this.member = memberAuth.getMember();
            this.email = memberAuth.getEmail();
            this.pw = memberAuth.getPw();
            this.provider = memberAuth.getProvider();
            this.providerId = memberAuth.getProviderId();
            return this;
        }

        public MemberAuthEntity build() {
            return new MemberAuthEntity(this.member, this.email, this.pw, this.provider, this.providerId);
        }
    }
}
