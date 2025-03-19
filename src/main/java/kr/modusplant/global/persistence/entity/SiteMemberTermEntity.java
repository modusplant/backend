package kr.modusplant.global.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SNAKE_SITE_MEMBER_TERM)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SiteMemberTermEntity {
    @Id
    private UUID uuid;

    @OneToOne
    @MapsId
    @JoinColumn(name = "uuid", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity member;

    @Column(name = SNAKE_AGREED_TOU_VER, nullable = false, length = 10)
    private String agreedTermsOfUseVersion;

    @Column(name = SNAKE_AGREED_PRIV_POLI_VER, nullable = false, length = 10)
    private String agreedPrivacyPolicyVersion;

    @Column(name = SNAKE_AGREED_AD_INFO_RECE_VER, length = 10)
    private String agreedAdInfoReceivingVersion;

    @Column(name = SNAKE_LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = SNAKE_VER_NUM, nullable = false)
    private Long versionNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteMemberTermEntity that)) return false;
        return new EqualsBuilder().append(getMember(), that.getMember()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMember()).toHashCode();
    }

    public SiteMemberTermEntity(SiteMemberEntity member, String agreedTermsOfUseVersion, String agreedPrivacyPolicyVersion, String agreedAdInfoReceivingVersion) {
        this.member = member;
        this.agreedTermsOfUseVersion = agreedTermsOfUseVersion;
        this.agreedPrivacyPolicyVersion = agreedPrivacyPolicyVersion;
        this.agreedAdInfoReceivingVersion = agreedAdInfoReceivingVersion;
    }

    public static SiteMemberTermEntityBuilder builder() {
        return new SiteMemberTermEntityBuilder();
    }

    public static final class SiteMemberTermEntityBuilder {
        private SiteMemberEntity member;
        private String agreedTermsOfUseVersion;
        private String agreedPrivacyPolicyVersion;
        private String agreedAdInfoReceivingVersion;

        public SiteMemberTermEntityBuilder member(final SiteMemberEntity member) {
            this.member = member;
            return this;
        }

        public SiteMemberTermEntityBuilder agreedTermsOfUseVersion(final String agreedTermsOfUseVersion) {
            this.agreedTermsOfUseVersion = agreedTermsOfUseVersion;
            return this;
        }

        public SiteMemberTermEntityBuilder agreedPrivacyPolicyVersion(final String agreedPrivacyPolicyVersion) {
            this.agreedPrivacyPolicyVersion = agreedPrivacyPolicyVersion;
            return this;
        }

        public SiteMemberTermEntityBuilder agreedAdInfoReceivingVersion(final String agreedAdInfoReceivingVersion) {
            this.agreedAdInfoReceivingVersion = agreedAdInfoReceivingVersion;
            return this;
        }

        public SiteMemberTermEntityBuilder memberTermEntity(final SiteMemberTermEntity memberTerm) {
            this.member = memberTerm.getMember();
            this.agreedTermsOfUseVersion = memberTerm.getAgreedTermsOfUseVersion();
            this.agreedPrivacyPolicyVersion = memberTerm.getAgreedPrivacyPolicyVersion();
            this.agreedAdInfoReceivingVersion = memberTerm.getAgreedAdInfoReceivingVersion();
            return this;
        }

        public SiteMemberTermEntity build() {
            return new SiteMemberTermEntity(this.member, this.agreedTermsOfUseVersion, this.agreedPrivacyPolicyVersion, this.agreedAdInfoReceivingVersion);
        }
    }
}