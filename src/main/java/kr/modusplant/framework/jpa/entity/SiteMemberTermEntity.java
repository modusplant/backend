package kr.modusplant.framework.jpa.entity;

import jakarta.persistence.*;
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
import static kr.modusplant.shared.persistence.constant.TableName.SITE_MEMBER_TERM;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER_TERM)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class SiteMemberTermEntity {
    @Id
    private UUID uuid;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId
    @JoinColumn(name = "uuid", nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private SiteMemberEntity member;

    @Column(name = "agreed_tou_ver", nullable = false, length = 10)
    private String agreedTermsOfUseVersion;

    @Column(name = "agreed_priv_poli_ver", nullable = false, length = 10)
    private String agreedPrivacyPolicyVersion;

    @Column(name = "agreed_ad_info_rece_ver", length = 10)
    private String agreedAdInfoReceivingVersion;

    @Column(name = LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = VER_NUM, nullable = false)
    @ToString.Exclude
    private Long versionNumber;

    public void updateAgreedTermsOfUseVersion(String agreedTermsOfUseVersion) {
        this.agreedTermsOfUseVersion = agreedTermsOfUseVersion;
    }

    public void updateAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion) {
        this.agreedPrivacyPolicyVersion = agreedPrivacyPolicyVersion;
    }

    public void updateAgreedAdInfoReceivingVersion(String agreedAdInfoReceivingVersion) {
        this.agreedAdInfoReceivingVersion = agreedAdInfoReceivingVersion;
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
        if (!(o instanceof SiteMemberTermEntity that)) return false;
        return new EqualsBuilder().append(getMember(), that.getMember()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMember()).toHashCode();
    }

    private SiteMemberTermEntity(SiteMemberEntity member, String agreedTermsOfUseVersion, String agreedPrivacyPolicyVersion, String agreedAdInfoReceivingVersion) {
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

        public SiteMemberTermEntityBuilder memberTerm(final SiteMemberTermEntity memberTerm) {
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