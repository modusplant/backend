package kr.modusplant.domains.comment.framework.outbound.persistence.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_COMMENT;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_COMMENT)
@IdClass(CommentCompositeKey.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class CommentEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = POST_ULID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private PostEntity post;

    @Id
    @Column(name = PATH, nullable = false, updatable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = AUTH_MEMB_UUID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private MemberEntity authMember;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "content", nullable = false, length = 900)
    @ToString.Exclude
    private String content;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = CREATED_AT, nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public void updateContent(String content) { this.content = content; }

    public void updateEditedAt() { this.editedAt = LocalDateTime.now(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentEntity that)) return false;

        return new EqualsBuilder()
                .append(getPost(), that.getPost())
                .append(getPath(), that.getPath())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPost())
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

    private CommentEntity(
            PostEntity post, String path,
            MemberEntity authMember, Integer likeCount,
            String content, Boolean isDeleted
    ) {
        this.post = post;
        this.path = path;
        this.authMember = authMember;
        this.likeCount = likeCount;
        this.content = content;
        this.isDeleted = isDeleted;
    }

    public static CommentEntityBuilder builder() {
        return new CommentEntityBuilder();
    }

    public static final class CommentEntityBuilder {
        private PostEntity post;
        private String path;
        private MemberEntity authMember;
        private Integer likeCount;
        private String content;
        private Boolean isDeleted;

        public CommentEntityBuilder post(final PostEntity post) {
            this.post = post;
            return this;
        }

        public CommentEntityBuilder path(final String path) {
            this.path = path;
            return this;
        }

        public CommentEntityBuilder authMember(final MemberEntity authMember) {
            this.authMember = authMember;
            return this;
        }

        public CommentEntityBuilder likeCount(final Integer likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public CommentEntityBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public CommentEntityBuilder isDeleted(final Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public CommentEntityBuilder comment(final CommentEntity commentEntity) {
            this.post = commentEntity.getPost();
            this.path = commentEntity.getPath();
            this.authMember = commentEntity.getAuthMember();
            this.likeCount = commentEntity.getLikeCount();
            this.content = commentEntity.getContent();
            this.isDeleted = commentEntity.getIsDeleted();
            return this;
        }

        public CommentEntity build() {
            return new CommentEntity(this.post, this.path, this.authMember, this.likeCount, this.content, this.isDeleted
            );
        }
    }
}