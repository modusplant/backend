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

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "intro", length = 60)
    private String introduction;

    @Column(name = LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = VER_NUM, nullable = false)
    private Long versionNumber;

    public void updateImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
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

    private SiteMemberProfileEntity(SiteMemberEntity member, String imagePath, String introduction) {
        this.member = member;
        this.imagePath = imagePath;
        this.introduction = introduction;
    }

    public static SiteMemberProfileEntityBuilder builder() {
        return new SiteMemberProfileEntityBuilder();
    }

    public static final class SiteMemberProfileEntityBuilder {
        private SiteMemberEntity member;
        private String imagePath;
        private String introduction;

        public SiteMemberProfileEntityBuilder member(final SiteMemberEntity member) {
            this.member = member;
            return this;
        }

        public SiteMemberProfileEntityBuilder imagePath(final String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public SiteMemberProfileEntityBuilder introduction(final String introduction) {
            this.introduction = introduction;
            return this;
        }

        public SiteMemberProfileEntityBuilder memberProfile(final SiteMemberProfileEntity memberProfile) {
            this.member = memberProfile.getMember();
            this.imagePath = memberProfile.getImagePath();
            this.introduction = memberProfile.getIntroduction();
            return this;
        }

        public SiteMemberProfileEntity build() {
            return new SiteMemberProfileEntity(this.member, this.imagePath, this.introduction);
        }
    }
}
