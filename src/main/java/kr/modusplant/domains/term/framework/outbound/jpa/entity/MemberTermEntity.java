package kr.modusplant.domains.term.framework.outbound.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class MemberTermEntity {
    @Id
    private UUID uuid;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "uuid", nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private MemberEntity member;

    @Column(name = "agreed_tou_ver", nullable = false, length = 10)
    private String agreedTermsOfUseVersion;

    @Column(name = "agreed_priv_poli_ver", nullable = false, length = 10)
    private String agreedPrivacyPolicyVersion;

    @Column(name = "agreed_comm_poli_ver", length = 10)
    private String agreedCommunityPolicyVersion;

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

    public void updateAgreedCommunityPolicyVersion(String agreedCommunityPolicyVersion) {
        this.agreedCommunityPolicyVersion = agreedCommunityPolicyVersion;
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
        if (!(o instanceof MemberTermEntity that)) return false;
        return new EqualsBuilder().append(getMember(), that.getMember()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMember()).toHashCode();
    }

    private MemberTermEntity(MemberEntity member, String agreedTermsOfUseVersion, String agreedPrivacyPolicyVersion, String agreedCommunityPolicyVersion) {
        this.member = member;
        this.agreedTermsOfUseVersion = agreedTermsOfUseVersion;
        this.agreedPrivacyPolicyVersion = agreedPrivacyPolicyVersion;
        this.agreedCommunityPolicyVersion = agreedCommunityPolicyVersion;
    }

    public static MemberTermEntityBuilder builder() {
        return new MemberTermEntityBuilder();
    }

    public static final class MemberTermEntityBuilder {
        private MemberEntity member;
        private String agreedTermsOfUseVersion;
        private String agreedPrivacyPolicyVersion;
        private String agreedCommunityPolicyVersion;

        public MemberTermEntityBuilder member(final MemberEntity member) {
            this.member = member;
            return this;
        }

        public MemberTermEntityBuilder agreedTermsOfUseVersion(final String agreedTermsOfUseVersion) {
            this.agreedTermsOfUseVersion = agreedTermsOfUseVersion;
            return this;
        }

        public MemberTermEntityBuilder agreedPrivacyPolicyVersion(final String agreedPrivacyPolicyVersion) {
            this.agreedPrivacyPolicyVersion = agreedPrivacyPolicyVersion;
            return this;
        }

        public MemberTermEntityBuilder agreedCommunityPolicyVersion(final String agreedCommunityPolicyVersion) {
            this.agreedCommunityPolicyVersion = agreedCommunityPolicyVersion;
            return this;
        }

        public MemberTermEntityBuilder memberTerm(final MemberTermEntity memberTerm) {
            this.member = memberTerm.getMember();
            this.agreedTermsOfUseVersion = memberTerm.getAgreedTermsOfUseVersion();
            this.agreedPrivacyPolicyVersion = memberTerm.getAgreedPrivacyPolicyVersion();
            this.agreedCommunityPolicyVersion = memberTerm.getAgreedCommunityPolicyVersion();
            return this;
        }

        public MemberTermEntity build() {
            return new MemberTermEntity(this.member, this.agreedTermsOfUseVersion, this.agreedPrivacyPolicyVersion, this.agreedCommunityPolicyVersion);
        }
    }
}