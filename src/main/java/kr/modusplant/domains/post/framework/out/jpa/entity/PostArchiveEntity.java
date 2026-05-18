package kr.modusplant.domains.post.framework.out.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_POST_ARCHIVE;

@Entity
@Table(name = COMM_POST_ARCHIVE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class PostArchiveEntity {
    @Id
    @Column(nullable = false, updatable = false)
    private String ulid;

    @Column(name = PRI_CATE_ID, nullable = false)
    private Integer primaryCategoryId;

    @Column(name = SECO_CATE_ID, nullable = false)
    private Integer secondaryCategoryId;

    @Column(name = AUTH_MEMB_UUID)
    private UUID authMemberUuid;

    @Column(nullable = false, length = 60)
    private String title;

    @Column(name = CONTENT_TEXT, nullable = false)
    private String contentText;

    @Column(name = CREATED_AT, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = ARCHIVED_AT, nullable = false)
    @CreatedDate
    private LocalDateTime archivedAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostArchiveEntity that)) return false;
        return new EqualsBuilder().append(getUlid(), that.getUlid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37).append(getUlid()).toHashCode();
    }

    private PostArchiveEntity(String ulid, Integer primaryCategoryId, Integer secondaryCategoryId, UUID authMemberUuid, String title, String contentText, LocalDateTime createdAt, LocalDateTime archivedAt, LocalDateTime updatedAt, LocalDateTime publishedAt) {
        this.ulid = ulid;
        this.primaryCategoryId = primaryCategoryId;
        this.secondaryCategoryId = secondaryCategoryId;
        this.authMemberUuid = authMemberUuid;
        this.title = title;
        this.contentText = contentText;
        this.createdAt = createdAt;
        this.archivedAt = archivedAt;
        this.updatedAt = updatedAt;
        this.publishedAt = publishedAt;
    }

    public static PostArchiveEntityBuilder builder() {
        return new PostArchiveEntityBuilder();
    }

    public static final class PostArchiveEntityBuilder {
        private String ulid;
        private Integer primaryCategoryId;
        private Integer secondaryCategoryId;
        private UUID authMemberUuid;
        private String title;
        private String contentText;
        private LocalDateTime createdAt;
        private LocalDateTime archivedAt;
        private LocalDateTime updatedAt;
        private LocalDateTime publishedAt;

        public PostArchiveEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public PostArchiveEntityBuilder primaryCategoryId(Integer primaryCategoryId) {
            this.primaryCategoryId = primaryCategoryId;
            return this;
        }

        public PostArchiveEntityBuilder secondaryCategoryId(Integer secondaryCategoryId) {
            this.secondaryCategoryId = secondaryCategoryId;
            return this;
        }

        public PostArchiveEntityBuilder authMemberUuid(UUID authMemberUuid) {
            this.authMemberUuid = authMemberUuid;
            return this;
        }

        public PostArchiveEntityBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PostArchiveEntityBuilder contentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public PostArchiveEntityBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PostArchiveEntityBuilder archivedAt(LocalDateTime archivedAt) {
            this.archivedAt = archivedAt;
            return this;
        }

        public PostArchiveEntityBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PostArchiveEntityBuilder publishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public PostArchiveEntityBuilder postArchive(final PostArchiveEntity postEntity) {
            this.ulid = postEntity.ulid;
            this.primaryCategoryId = postEntity.primaryCategoryId;
            this.secondaryCategoryId = postEntity.secondaryCategoryId;
            this.authMemberUuid = postEntity.authMemberUuid;
            this.title = postEntity.title;
            this.contentText = postEntity.contentText;
            this.createdAt = postEntity.createdAt;
            this.archivedAt = postEntity.archivedAt;
            this.updatedAt = postEntity.updatedAt;
            this.publishedAt = postEntity.publishedAt;
            return this;
        }

        public PostArchiveEntity build() {
            return new PostArchiveEntity(this.ulid, this.primaryCategoryId, this.secondaryCategoryId, this.authMemberUuid, this.title, this.contentText, this.createdAt, this.archivedAt, this.updatedAt, this.publishedAt);
        }

    }
}
