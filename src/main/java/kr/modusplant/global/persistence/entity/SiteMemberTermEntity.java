package kr.modusplant.global.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID uuid;

    @Setter
    @Column(name = SNAKE_AGREED_TOU_VER, nullable = false, length = 10)
    private String agreedTermsOfUseVersion;

    @Setter
    @Column(name = SNAKE_AGREED_PRIV_POLI_VER, nullable = false, length = 10)
    private String agreedPrivacyPolicyVersion;

    @Setter
    @Column(name = SNAKE_AGREED_AD_INFO_RECE_VER, nullable = false, length = 10)
    private String agreedAdInfoReceivingVersion;

    @Column(name = SNAKE_LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = SNAKE_VER_NUM, nullable = false)
    private Long versionNumber;

    public SiteMemberTermEntity(String agreedTermsOfUseVersion, String agreedPrivacyPolicyVersion, String agreedAdInfoReceivingVersion) {
        this.agreedTermsOfUseVersion = agreedTermsOfUseVersion;
        this.agreedPrivacyPolicyVersion = agreedPrivacyPolicyVersion;
        this.agreedAdInfoReceivingVersion = agreedAdInfoReceivingVersion;
    }

    public static SiteMemberTermEntityBuilder builder() {
        return new SiteMemberTermEntityBuilder();
    }

    public static final class SiteMemberTermEntityBuilder {
        private String agreedTermsOfUseVersion;
        private String agreedPrivacyPolicyVersion;
        private String agreedAdInfoReceivingVersion;

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

        public SiteMemberTermEntityBuilder memberTermEntity(final SiteMemberTermEntity memberClause) {
            this.agreedTermsOfUseVersion = memberClause.getAgreedTermsOfUseVersion();
            this.agreedPrivacyPolicyVersion = memberClause.getAgreedPrivacyPolicyVersion();
            this.agreedAdInfoReceivingVersion = memberClause.getAgreedAdInfoReceivingVersion();
            return this;
        }

        public SiteMemberTermEntity build() {
            return new SiteMemberTermEntity(this.agreedTermsOfUseVersion, this.agreedPrivacyPolicyVersion, this.agreedAdInfoReceivingVersion);
        }
    }
}