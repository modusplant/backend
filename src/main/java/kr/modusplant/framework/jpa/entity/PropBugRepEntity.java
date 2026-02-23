package kr.modusplant.framework.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.PROP_BUG_REP;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = PROP_BUG_REP)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class PropBugRepEntity {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = MEMB_UUID, nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private SiteMemberEntity member;

    @Column(nullable = false, updatable = false, length = 60)
    private String title;

    @Column(nullable = false, updatable = false, length = 600)
    private String content;

    @Column(name = "image_path", updatable = false)
    private String imagePath;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @Column(name = "handled_at")
    private LocalDateTime handledAt;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = VER_NUM, nullable = false)
    @ToString.Exclude
    private Long versionNumber;

    public String getETagSource() {
        return getUuid() + "-" + getVersionNumber();
    }

    public LocalDateTime getLastModifiedAtAsTruncatedToSeconds() {
        return getLastModifiedAt().truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (!(object instanceof PropBugRepEntity that)) return false;

        return new EqualsBuilder().append(getUuid(), that.getUuid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getUuid()).toHashCode();
    }

    private PropBugRepEntity(UUID uuid, SiteMemberEntity member, String title, String content, String imagePath, LocalDateTime checkedAt, LocalDateTime handledAt) {
        this.uuid = uuid;
        this.member = member;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.checkedAt = checkedAt;
        this.handledAt = handledAt;
    }

    public static PropBugRepEntityBuilder builder() {
        return new PropBugRepEntityBuilder();
    }

    public static final class PropBugRepEntityBuilder {
        private UUID uuid;
        private SiteMemberEntity member;
        private String title;
        private String content;
        private String imagePath;
        private LocalDateTime checkedAt;
        private LocalDateTime handledAt;

        public PropBugRepEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public PropBugRepEntityBuilder member(final SiteMemberEntity member) {
            this.member = member;
            return this;
        }

        public PropBugRepEntityBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public PropBugRepEntityBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public PropBugRepEntityBuilder imagePath(final String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public PropBugRepEntityBuilder checkedAt(final LocalDateTime checkedAt) {
            this.checkedAt = checkedAt;
            return this;
        }

        public PropBugRepEntityBuilder handledAt(final LocalDateTime handledAt) {
            this.handledAt = handledAt;
            return this;
        }

        public PropBugRepEntityBuilder propBugRep(final PropBugRepEntity propBugRep) {
            this.uuid = propBugRep.getUuid();
            this.member = propBugRep.getMember();
            this.title = propBugRep.getTitle();
            this.content = propBugRep.getContent();
            this.imagePath = propBugRep.getImagePath();
            this.checkedAt = propBugRep.getCheckedAt();
            this.handledAt = propBugRep.getHandledAt();
            return this;
        }

        public PropBugRepEntity build() {
            return new PropBugRepEntity(this.uuid, this.member, this.title, this.content, this.imagePath, this.checkedAt, this.handledAt);
        }
    }
}
