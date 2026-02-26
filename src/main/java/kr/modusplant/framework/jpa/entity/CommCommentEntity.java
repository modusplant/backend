package kr.modusplant.framework.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.shared.persistence.annotation.DefaultValue;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_COMMENT;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_COMMENT)
@IdClass(CommCommentId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class CommCommentEntity {
    @Id
    private String postUlid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId("postUlid")
    @JoinColumn(name = POST_ULID, nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private CommPostEntity postEntity;

    @Id
    @Column(name = PATH, nullable = false, updatable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = AUTH_MEMB_UUID, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private SiteMemberEntity authMember;

    @Column(name = "like_count", nullable = false)
    @DefaultValue
    private Integer likeCount;

    @Column(name = "content", nullable = false, length = 900)
    @ToString.Exclude
    private String content;

    @Column(name = "is_deleted", nullable = false)
    @DefaultValue
    private Boolean isDeleted;

    @Column(name = CREATED_AT, nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommCommentEntity that)) return false;

        return new EqualsBuilder()
                .append(getPostUlid(), that.getPostUlid())
                .append(getPath(), that.getPath())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPostUlid())
                .append(getPath())
                .toHashCode();
    }

    @PrePersist
    public void prePersist() {
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    private CommCommentEntity(
            CommPostEntity postEntity, String path,
            SiteMemberEntity authMember, Integer likeCount,
            String content, Boolean isDeleted
    ) {
        this.postEntity = postEntity;
        this.path = path;
        this.authMember = authMember;
        this.likeCount = likeCount;
        this.content = content;
        this.isDeleted = isDeleted;
    }

    public static CommCommentEntityBuilder builder() {
        return new CommCommentEntityBuilder();
    }

    public static final class CommCommentEntityBuilder {
        private CommPostEntity postEntity;
        private String path;
        private SiteMemberEntity authMember;
        private Integer likeCount;
        private String content;
        private Boolean isDeleted;

        public CommCommentEntityBuilder postEntity(final CommPostEntity postEntity) {
            this.postEntity = postEntity;
            return this;
        }

        public CommCommentEntityBuilder path(final String path) {
            this.path = path;
            return this;
        }

        public CommCommentEntityBuilder authMember(final SiteMemberEntity authMember) {
            this.authMember = authMember;
            return this;
        }

        public CommCommentEntityBuilder likeCount(final Integer likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public CommCommentEntityBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public CommCommentEntityBuilder isDeleted(final Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public CommCommentEntityBuilder commComment(final CommCommentEntity commCommentEntity) {
            this.postEntity = commCommentEntity.getPostEntity();
            this.path = commCommentEntity.getPath();
            this.authMember = commCommentEntity.getAuthMember();
            this.likeCount = commCommentEntity.getLikeCount();
            this.content = commCommentEntity.getContent();
            this.isDeleted = commCommentEntity.getIsDeleted();
            return this;
        }

        public CommCommentEntity build() {
            return new CommCommentEntity(this.postEntity, this.path, this.authMember, this.likeCount, this.content, this.isDeleted
            );
        }
    }
}