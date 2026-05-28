package kr.modusplant.domains.post.framework.outbound.jpa.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.shared.framework.jpa.generator.UlidGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_POST;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_POST)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class PostEntity {
    @Id
    @UlidGenerator
    @Column(nullable = false, updatable = false)
    private String ulid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = PRI_CATE_ID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private PrimaryCategoryEntity primaryCategory;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = SECO_CATE_ID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private SecondaryCategoryEntity secondaryCategory;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = AUTH_MEMB_UUID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private MemberEntity authMember;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(length = 60)
    private String title;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @ToString.Exclude
    private JsonNode content;

    @Column(name = CONTENT_TEXT, insertable = false, updatable = false)
    private String contentText;

    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    @ToString.Exclude
    private Long ver;

    public void updatePrimaryCategory(PrimaryCategoryEntity primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public void updateSecondaryCategory(SecondaryCategoryEntity secondaryCategory) {
        this.secondaryCategory = secondaryCategory;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(JsonNode content) {
        this.content = content;
    }

    public void updateThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public void updateIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void updateViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public void updatePublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getETagSource() {
        return getUlid() + "-" + getVer();
    }

    public LocalDateTime getUpdatedAtAsTruncatedToSeconds() {
        return getUpdatedAt().truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostEntity that)) return false;
        return new EqualsBuilder().append(getUlid(), that.getUlid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37).append(getUlid()).toHashCode();
    }

    @PrePersist
    public void prePersist() {
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
        if (this.isPublished == null) {
            this.isPublished = false;
        }
    }

    private PostEntity(String ulid, PrimaryCategoryEntity primaryCategory, SecondaryCategoryEntity secondaryCategory, MemberEntity authMember, Integer likeCount, Long viewCount, String title, JsonNode content, String thumbnailPath, Boolean isPublished, LocalDateTime publishedAt) {
        this.ulid = ulid;
        this.primaryCategory = primaryCategory;
        this.secondaryCategory = secondaryCategory;
        this.authMember = authMember;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.title = title;
        this.content = content;
        this.thumbnailPath = thumbnailPath;
        this.isPublished = isPublished;
        this.publishedAt = publishedAt;
    }

    public static PostEntityBuilder builder() {
        return new PostEntityBuilder();
    }

    public static final class PostEntityBuilder {
        private String ulid;
        private PrimaryCategoryEntity primaryCategory;
        private SecondaryCategoryEntity secondaryCategory;
        private MemberEntity authMember;
        private Integer likeCount;
        private Long viewCount;
        private String title;
        private JsonNode content;
        private String thumbnailPath;
        private Boolean isPublished;
        private LocalDateTime publishedAt;

        public PostEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public PostEntityBuilder primaryCategory(final PrimaryCategoryEntity primaryCategory) {
            this.primaryCategory = primaryCategory;
            return this;
        }

        public PostEntityBuilder secondaryCategory(final SecondaryCategoryEntity secondaryCategory) {
            this.secondaryCategory = secondaryCategory;
            return this;
        }

        public PostEntityBuilder authMember(final MemberEntity authMember) {
            this.authMember = authMember;
            return this;
        }

        public PostEntityBuilder likeCount(final Integer likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public PostEntityBuilder viewCount(final Long viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public PostEntityBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public PostEntityBuilder content(final JsonNode content) {
            this.content = content;
            return this;
        }

        public PostEntityBuilder thumbnailPath(final String thumbnailPath) {
            this.thumbnailPath = thumbnailPath;
            return this;
        }

        public PostEntityBuilder isPublished(final Boolean isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        public PostEntityBuilder publishedAt(final LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public PostEntityBuilder post(final PostEntity postEntity) {
            this.ulid = postEntity.ulid;
            this.primaryCategory = postEntity.primaryCategory;
            this.secondaryCategory = postEntity.secondaryCategory;
            this.authMember = postEntity.authMember;
            this.likeCount = postEntity.likeCount;
            this.viewCount = postEntity.viewCount;
            this.title = postEntity.title;
            this.content = postEntity.content;
            this.thumbnailPath = postEntity.thumbnailPath;
            this.isPublished = postEntity.isPublished;
            this.publishedAt = postEntity.publishedAt;
            return this;
        }

        public PostEntity build() {
            return new PostEntity(this.ulid, this.primaryCategory, this.secondaryCategory, this.authMember, this.likeCount, this.viewCount, this.title, this.content, this.thumbnailPath, this.isPublished, this.publishedAt);
        }

    }
}
