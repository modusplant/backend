package kr.modusplant.domains.post.framework.out.jpa.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
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
import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_POST;

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
    private CommPrimaryCategoryEntity primaryCategory;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = SECO_CATE_UUID, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private CommSecondaryCategoryEntity secondaryCategory;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = AUTH_MEMB_UUID, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity authMember;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = CREA_MEMB_UUID, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity createMember;

    @Column(name = LIKE_COUNT, nullable = false)
    @DefaultValue
    private Integer likeCount;

    @Column(name = VIEW_COUNT, nullable = false)
    @DefaultValue
    private Long viewCount;

    @Column(nullable = false, length = 60)
    private String title;

    @Type(JsonBinaryType.class)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode content;

    @Column(name = IS_PUBLISHED, nullable = false)
    @DefaultValue
    private Boolean isPublished;

    @Column(name = PUBLISHED_AT)
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

    private PostEntity(String ulid, CommPrimaryCategoryEntity primaryCategory, CommSecondaryCategoryEntity secondaryCategory, SiteMemberEntity authMember, SiteMemberEntity createMember, Integer likeCount, Long viewCount, String title, JsonNode content, Boolean isPublished, LocalDateTime publishedAt) {
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

    public static PostEntityBuilder builder() {
        return new PostEntityBuilder();
    }

    public static final class PostEntityBuilder {
        private String ulid;
        private CommPrimaryCategoryEntity primaryCategory;
        private CommSecondaryCategoryEntity secondaryCategory;
        private SiteMemberEntity authMember;
        private SiteMemberEntity createMember;
        private Integer likeCount;
        private Long viewCount;
        private String title;
        private JsonNode content;
        private Boolean isPublished;
        private LocalDateTime publishedAt;

        public PostEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public PostEntityBuilder primaryCategory(final CommPrimaryCategoryEntity primaryCategory) {
            this.primaryCategory = primaryCategory;
            return this;
        }

        public PostEntityBuilder secondaryCategory(final CommSecondaryCategoryEntity secondaryCategory) {
            this.secondaryCategory = secondaryCategory;
            return this;
        }

        public PostEntityBuilder authMember(final SiteMemberEntity authMember) {
            this.authMember = authMember;
            return this;
        }

        public PostEntityBuilder createMember(final SiteMemberEntity createMember) {
            this.createMember = createMember;
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

        public PostEntityBuilder isPublished(final Boolean isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        public PostEntityBuilder publishedAt(final LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public PostEntityBuilder commPostEntity(final PostEntity postEntity) {
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
