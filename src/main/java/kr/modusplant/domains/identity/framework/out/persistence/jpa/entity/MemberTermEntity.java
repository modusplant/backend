package kr.modusplant.domains.identity.framework.out.persistence.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.framework.out.jpa.entity.SiteMemberTermEntity;
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
import static kr.modusplant.shared.persistence.constant.TableName.SITE_MEMBER_TERM;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER_TERM)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTermEntity {
    @Id
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteMemberTermEntity that)) return false;
        return new EqualsBuilder().append(getUuid(), that.getMember()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getUuid()).toHashCode();
    }

    private MemberTermEntity(UUID uuid, String agreedTermsOfUseVersion, String agreedPrivacyPolicyVersion, String agreedAdInfoReceivingVersion) {
        this.uuid = uuid;
        this.agreedTermsOfUseVersion = agreedTermsOfUseVersion;
        this.agreedPrivacyPolicyVersion = agreedPrivacyPolicyVersion;
        this.agreedAdInfoReceivingVersion = agreedAdInfoReceivingVersion;
    }

    public static MemberTermEntity.MemberTermEntityBuilder builder() {
        return new MemberTermEntity.MemberTermEntityBuilder();
    }

    public static final class MemberTermEntityBuilder {
        private UUID uuid;
        private String agreedTermsOfUseVersion;
        private String agreedPrivacyPolicyVersion;
        private String agreedAdInfoReceivingVersion;

        public MemberTermEntity.MemberTermEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public MemberTermEntity.MemberTermEntityBuilder agreedTermsOfUseVersion(final String agreedTermsOfUseVersion) {
            this.agreedTermsOfUseVersion = agreedTermsOfUseVersion;
            return this;
        }

        public MemberTermEntity.MemberTermEntityBuilder agreedPrivacyPolicyVersion(final String agreedPrivacyPolicyVersion) {
            this.agreedPrivacyPolicyVersion = agreedPrivacyPolicyVersion;
            return this;
        }

        public MemberTermEntity.MemberTermEntityBuilder agreedAdInfoReceivingVersion(final String agreedAdInfoReceivingVersion) {
            this.agreedAdInfoReceivingVersion = agreedAdInfoReceivingVersion;
            return this;
        }

        public MemberTermEntity.MemberTermEntityBuilder memberTermEntity(final MemberTermEntity memberTerm) {
            this.uuid = memberTerm.getUuid();
            this.agreedTermsOfUseVersion = memberTerm.getAgreedTermsOfUseVersion();
            this.agreedPrivacyPolicyVersion = memberTerm.getAgreedPrivacyPolicyVersion();
            this.agreedAdInfoReceivingVersion = memberTerm.getAgreedAdInfoReceivingVersion();
            return this;
        }

        public MemberTermEntity build() {
            return new MemberTermEntity(this.uuid, this.agreedTermsOfUseVersion, this.agreedPrivacyPolicyVersion, this.agreedAdInfoReceivingVersion);
        }
    }
}
