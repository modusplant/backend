package kr.modusplant.framework.out.jpa.entity;

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

import static kr.modusplant.shared.persistence.constant.TableColumnName.LAST_MODIFIED_AT;
import static kr.modusplant.shared.persistence.constant.TableColumnName.VER_NUM;
import static kr.modusplant.shared.persistence.constant.TableName.SITE_MEMBER_PROF;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER_PROF)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SiteMemberProfileEntity {
    @Id
    private UUID uuid;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId
    @JoinColumn(name = "uuid", nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity member;

    @Column(name = "intro")
    private String introduction;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = VER_NUM, nullable = false)
    private Long versionNumber;

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteMemberProfileEntity that)) return false;
        return new EqualsBuilder().append(getMember(), that.getMember()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMember()).toHashCode();
    }

    private SiteMemberProfileEntity(SiteMemberEntity member, String introduction, String imageUrl) {
        this.member = member;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
    }

    public static SiteMemberProfileEntityBuilder builder() {
        return new SiteMemberProfileEntityBuilder();
    }

    public static final class SiteMemberProfileEntityBuilder {
        private SiteMemberEntity member;
        private String introduction;
        private String imageUrl;

        public SiteMemberProfileEntityBuilder member(final SiteMemberEntity member) {
            this.member = member;
            return this;
        }

        public SiteMemberProfileEntityBuilder introduction(final String introduction) {
            this.introduction = introduction;
            return this;
        }

        public SiteMemberProfileEntityBuilder imageUrl(final String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }
        
        public SiteMemberProfileEntityBuilder memberProfileEntity(final SiteMemberProfileEntity memberProfile) {
            this.member = memberProfile.getMember();
            this.introduction = memberProfile.getIntroduction();
            this.imageUrl = memberProfile.getImageUrl();
            return this;
        }

        public SiteMemberProfileEntity build() {
            return new SiteMemberProfileEntity(this.member, this.introduction, this.imageUrl);
        }
    }
}
