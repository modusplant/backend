package kr.modusplant.domains.post.framework.out.jpa.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import kr.modusplant.infrastructure.persistence.annotation.DefaultValue;
import kr.modusplant.infrastructure.persistence.generator.UlidGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.vo.TableColumnName.*;
import static kr.modusplant.shared.persistence.vo.TableName.COMM_POST;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_POST)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostEntity {
    @Id
    @UlidGenerator
    @Column(nullable = false, updatable = false)
    private String ulid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = PRI_CATE_UUID, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private PrimaryCategoryEntity primaryCategory;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = SECO_CATE_UUID, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SecondaryCategoryEntity secondaryCategory;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "auth_memb_uuid", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private AuthorEntity authMember;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "crea_memb_uuid", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private AuthorEntity createMember;

    @Column(name = "like_count", nullable = false)
    @DefaultValue
    private Integer likeCount;

    @Column(name = "view_count", nullable = false)
    @DefaultValue
    private Long viewCount;

    @Column(nullable = false, length = 150)
    private String title;

    @Type(JsonBinaryType.class)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode content;

    @Column(name = "is_published", nullable = false)
    @DefaultValue
    private Boolean isPublished;

    @Column(name = "published_at", nullable = true)
    private LocalDateTime publishedAt;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = UPDATED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Long ver;

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

    @PreUpdate
    public void preUpdate() {
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
        if (this.isPublished == null) {
            this.isPublished = false;
        }
    }

    private PostEntity(String ulid, PrimaryCategoryEntity primaryCategory, SecondaryCategoryEntity secondaryCategory, AuthorEntity authMember, AuthorEntity createMember, Integer likeCount, Long viewCount, String title, JsonNode content, Boolean isPublished, LocalDateTime publishedAt) {
        this.ulid = ulid;
        this.primaryCategory = primaryCategory;
        this.secondaryCategory = secondaryCategory;
        this.authMember = authMember;
        this.createMember = createMember;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.title = title;
        this.content = content;
        this.isPublished = isPublished;
        this.publishedAt = publishedAt;
    }

    public static CommPostEntityBuilder builder() {
        return new CommPostEntityBuilder();
    }

    public static final class CommPostEntityBuilder {
        private String ulid;
        private PrimaryCategoryEntity primaryCategory;
        private SecondaryCategoryEntity secondaryCategory;
        private AuthorEntity authMember;
        private AuthorEntity createMember;
        private Integer likeCount;
        private Long viewCount;
        private String title;
        private JsonNode content;
        private Boolean isPublished;
        private LocalDateTime publishedAt;

        public CommPostEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public CommPostEntityBuilder primaryCategory(final PrimaryCategoryEntity primaryCategory) {
            this.primaryCategory = primaryCategory;
            return this;
        }

        public CommPostEntityBuilder secondaryCategory(final SecondaryCategoryEntity secondaryCategory) {
            this.secondaryCategory = secondaryCategory;
            return this;
        }

        public CommPostEntityBuilder authMember(final AuthorEntity authMember) {
            this.authMember = authMember;
            return this;
        }

        public CommPostEntityBuilder createMember(final AuthorEntity createMember) {
            this.createMember = createMember;
            return this;
        }

        public CommPostEntityBuilder likeCount(final Integer likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public CommPostEntityBuilder viewCount(final Long viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public CommPostEntityBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public CommPostEntityBuilder content(final JsonNode content) {
            this.content = content;
            return this;
        }

        public CommPostEntityBuilder isPublished(final Boolean isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        public CommPostEntityBuilder publishedAt(final LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public CommPostEntityBuilder commPostEntity(final PostEntity postEntity) {
            this.ulid = postEntity.ulid;
            this.primaryCategory = postEntity.primaryCategory;
            this.secondaryCategory = postEntity.secondaryCategory;
            this.authMember = postEntity.authMember;
            this.createMember = postEntity.createMember;
            this.likeCount = postEntity.likeCount;
            this.viewCount = postEntity.viewCount;
            this.title = postEntity.title;
            this.content = postEntity.content;
            this.isPublished = postEntity.isPublished;
            this.publishedAt = postEntity.publishedAt;
            return this;
        }

        public PostEntity build() {
            return new PostEntity(this.ulid, this.primaryCategory, this.secondaryCategory, this.authMember, this.createMember, this.likeCount, this.viewCount, this.title, this.content, this.isPublished, this.publishedAt);
        }

    }
}
