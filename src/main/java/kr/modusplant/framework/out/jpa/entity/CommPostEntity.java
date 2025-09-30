package kr.modusplant.framework.out.jpa.entity;

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

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_POST;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_POST)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommPostEntity {
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

    @Column(name = IS_DELETED, nullable = false)
    @DefaultValue
    private Boolean isDeleted;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = UPDATED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Long ver;

    public void updatePrimaryCategory(CommPrimaryCategoryEntity primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public void updateSecondaryCategory(CommSecondaryCategoryEntity secondaryCategory) {
        this.secondaryCategory = secondaryCategory;
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

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(JsonNode content) {
        this.content = content;
    }

    public void updateIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommPostEntity that)) return false;
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
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    private CommPostEntity(String ulid, CommPrimaryCategoryEntity primaryCategory, CommSecondaryCategoryEntity secondaryCategory, SiteMemberEntity authMember, SiteMemberEntity createMember, Integer likeCount, Long viewCount, String title, JsonNode content, Boolean isDeleted) {
        this.ulid = ulid;
        this.primaryCategory = primaryCategory;
        this.secondaryCategory = secondaryCategory;
        this.authMember = authMember;
        this.createMember = createMember;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.title = title;
        this.content = content;
        this.isDeleted = isDeleted;
    }

    public static CommPostEntityBuilder builder() {
        return new CommPostEntityBuilder();
    }

    public static final class CommPostEntityBuilder {
        private String ulid;
        private CommPrimaryCategoryEntity primaryCategory;
        private CommSecondaryCategoryEntity secondaryCategory;
        private SiteMemberEntity authMember;
        private SiteMemberEntity createMember;
        private Integer likeCount;
        private Long viewCount;
        private String title;
        private JsonNode content;
        private Boolean isDeleted;

        public CommPostEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public CommPostEntityBuilder primaryCategory(final CommPrimaryCategoryEntity primaryCategory) {
            this.primaryCategory = primaryCategory;
            return this;
        }

        public CommPostEntityBuilder secondaryCategory(final CommSecondaryCategoryEntity secondaryCategory) {
            this.secondaryCategory = secondaryCategory;
            return this;
        }

        public CommPostEntityBuilder authMember(final SiteMemberEntity authMember) {
            this.authMember = authMember;
            return this;
        }

        public CommPostEntityBuilder createMember(final SiteMemberEntity createMember) {
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

        public CommPostEntityBuilder isDeleted(final Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public CommPostEntityBuilder commPostEntity(final CommPostEntity commPostEntity) {
            this.ulid = commPostEntity.ulid;
            this.primaryCategory = commPostEntity.primaryCategory;
            this.secondaryCategory = commPostEntity.secondaryCategory;
            this.authMember = commPostEntity.authMember;
            this.createMember = commPostEntity.createMember;
            this.likeCount = commPostEntity.likeCount;
            this.viewCount = commPostEntity.viewCount;
            this.title = commPostEntity.title;
            this.content = commPostEntity.content;
            this.isDeleted = commPostEntity.isDeleted;
            return this;
        }

        public CommPostEntity build() {
            return new CommPostEntity(this.ulid, this.primaryCategory, this.secondaryCategory, this.authMember, this.createMember, this.likeCount, this.viewCount, this.title, this.content, this.isDeleted);
        }

    }
}
