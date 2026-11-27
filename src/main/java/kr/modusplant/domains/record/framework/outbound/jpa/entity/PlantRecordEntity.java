package kr.modusplant.domains.record.framework.outbound.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.record.domain.enums.RecordPublishedStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.constant.TableColumnName.AUTH_MEMB_UUID;
import static kr.modusplant.shared.persistence.constant.TableColumnName.CREATED_AT;
import static kr.modusplant.shared.persistence.constant.TableName.PLANT_RECORD;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = PLANT_RECORD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class PlantRecordEntity implements Persistable<String> {

    @Id
    @Column(nullable = false, updatable = false)
    private String ulid;

    @Transient
    private boolean isNew = true;

    @Override
    public String getId() {
        return ulid;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = AUTH_MEMB_UUID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private MemberEntity authMember;

    @Column(length = 60)
    private String description;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "video_key", nullable = false, length = 512)
    private String videoKey;

    @Column(name = "thumbnail_key", nullable = false, length = 512)
    private String thumbnailKey;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_published", nullable = false, length = 20)
    private RecordPublishedStatus isPublished;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateDescription(String description) {
        this.description = description;
    }

    public void markAsHidden() {
        this.isPublished = RecordPublishedStatus.HIDDEN;
    }

    public void markAsPublished() {
        this.isPublished = RecordPublishedStatus.PUBLISHED;
    }

    public void markAsBlocked() {
        this.isPublished = RecordPublishedStatus.BLOCKED;
    }

    public void markAsDeleted() {
        this.isPublished = RecordPublishedStatus.DELETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlantRecordEntity that)) return false;
        return new EqualsBuilder().append(getUlid(), that.getUlid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getUlid()).toHashCode();
    }

    private PlantRecordEntity(
            String ulid,
            MemberEntity authMember,
            String description,
            String originalFilename,
            String videoKey,
            String thumbnailKey,
            String contentType,
            Long sizeBytes,
            RecordPublishedStatus isPublished
    ) {
        this.ulid = ulid;
        this.authMember = authMember;
        this.description = description;
        this.originalFilename = originalFilename;
        this.videoKey = videoKey;
        this.thumbnailKey = thumbnailKey;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.isPublished = isPublished;
    }

    public static PlantRecordEntityBuilder builder() {
        return new PlantRecordEntityBuilder();
    }

    public static final class PlantRecordEntityBuilder {
        private String ulid;
        private MemberEntity authMember;
        private String description;
        private String originalFilename;
        private String videoKey;
        private String thumbnailKey;
        private String contentType;
        private Long sizeBytes;
        private RecordPublishedStatus isPublished;

        public PlantRecordEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public PlantRecordEntityBuilder authMember(final MemberEntity authMember) {
            this.authMember = authMember;
            return this;
        }

        public PlantRecordEntityBuilder description(final String description) {
            this.description = description;
            return this;
        }

        public PlantRecordEntityBuilder originalFilename(final String originalFilename) {
            this.originalFilename = originalFilename;
            return this;
        }

        public PlantRecordEntityBuilder videoKey(final String videoKey) {
            this.videoKey = videoKey;
            return this;
        }

        public PlantRecordEntityBuilder thumbnailKey(final String thumbnailKey) {
            this.thumbnailKey = thumbnailKey;
            return this;
        }

        public PlantRecordEntityBuilder contentType(final String contentType) {
            this.contentType = contentType;
            return this;
        }

        public PlantRecordEntityBuilder sizeBytes(final Long sizeBytes) {
            this.sizeBytes = sizeBytes;
            return this;
        }

        public PlantRecordEntityBuilder isPublished(final RecordPublishedStatus isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        public PlantRecordEntityBuilder plantRecord(final PlantRecordEntity entity) {
            this.ulid = entity.ulid;
            this.authMember = entity.authMember;
            this.description = entity.description;
            this.originalFilename = entity.originalFilename;
            this.videoKey = entity.videoKey;
            this.thumbnailKey = entity.thumbnailKey;
            this.contentType = entity.contentType;
            this.sizeBytes = entity.sizeBytes;
            this.isPublished = entity.isPublished;
            return this;
        }

        public PlantRecordEntity build() {
            return new PlantRecordEntity(
                    this.ulid,
                    this.authMember,
                    this.description,
                    this.originalFilename,
                    this.videoKey,
                    this.thumbnailKey,
                    this.contentType,
                    this.sizeBytes,
                    this.isPublished
            );
        }
    }
}
