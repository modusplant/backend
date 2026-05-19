package kr.modusplant.domains.member.framework.out.jpa.entity;

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
import static kr.modusplant.shared.persistence.constant.TableName.SITE_MEMBER_PROF;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER_PROF)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class MemberProfileEntity {
    @Id
    private UUID uuid;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "uuid", nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private MemberEntity member;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "intro", length = 60)
    private String introduction;

    @Column(name = LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = VER_NUM, nullable = false)
    @ToString.Exclude
    private Long versionNumber;

    public void updateImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
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
        if (!(o instanceof MemberProfileEntity that)) return false;
        return new EqualsBuilder().append(getMember(), that.getMember()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMember()).toHashCode();
    }

    private MemberProfileEntity(MemberEntity member, String imagePath, String introduction) {
        this.member = member;
        this.imagePath = imagePath;
        this.introduction = introduction;
    }

    public static MemberProfileEntityBuilder builder() {
        return new MemberProfileEntityBuilder();
    }

    public static final class MemberProfileEntityBuilder {
        private MemberEntity member;
        private String imagePath;
        private String introduction;

        public MemberProfileEntityBuilder member(final MemberEntity member) {
            this.member = member;
            return this;
        }

        public MemberProfileEntityBuilder imagePath(final String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public MemberProfileEntityBuilder introduction(final String introduction) {
            this.introduction = introduction;
            return this;
        }

        public MemberProfileEntityBuilder memberProfile(final MemberProfileEntity memberProfile) {
            this.member = memberProfile.getMember();
            this.imagePath = memberProfile.getImagePath();
            this.introduction = memberProfile.getIntroduction();
            return this;
        }

        public MemberProfileEntity build() {
            return new MemberProfileEntity(this.member, this.imagePath, this.introduction);
        }
    }
}
