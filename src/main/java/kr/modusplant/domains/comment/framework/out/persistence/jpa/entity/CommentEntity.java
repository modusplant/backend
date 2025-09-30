package kr.modusplant.domains.comment.framework.out.persistence.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.infrastructure.persistence.annotation.DefaultValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.legacy.domains.member.vo.MemberUuid.SNAKE_AUTH_MEMB_UUID;
import static kr.modusplant.legacy.domains.member.vo.MemberUuid.SNAKE_CREA_MEMB_UUID;
import static kr.modusplant.shared.persistence.constant.TableColumnName.IS_DELETED;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_COMMENT;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_COMMENT)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentEntity {

    @EmbeddedId
    private CommentCompositeKey id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId("postUlid")
    @JoinColumn(name = "post_ulid", nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private CommPostEntity postEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = SNAKE_AUTH_MEMB_UUID, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity authMember;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = SNAKE_CREA_MEMB_UUID, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity createMember;

    @Column(name = "content", nullable = false, length = 900)
    private String content;

    @Column(name = IS_DELETED, nullable = false)
    @DefaultValue
    private Boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentEntity that)) return false;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .toHashCode();
    }

    @PrePersist
    public void prePersist() {
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    private CommentEntity(
            CommPostEntity postEntity, String path,
            SiteMemberEntity authMember, SiteMemberEntity createMember,
            String content, Boolean isDeleted
    ) {
        this.postEntity = postEntity;
        this.id = CommentCompositeKey.builder().postUlid(postEntity.getUlid()).path(path).build();
        this.authMember = authMember;
        this.createMember = createMember;
        this.content = content;
        this.isDeleted = isDeleted;
    }

    public static CommentEntity.CommentEntityBuilder builder() {
        return new CommentEntity.CommentEntityBuilder();
    }

    public static final class CommentEntityBuilder {
        private CommPostEntity postEntity;
        private CommentCompositeKey id;
        private SiteMemberEntity authMember;
        private SiteMemberEntity createMember;
        private String content;
        private Boolean isDeleted;

        public CommentEntity.CommentEntityBuilder postEntity(final CommPostEntity postEntity) {
            this.postEntity = postEntity;
            return this;
        }

        public CommentEntity.CommentEntityBuilder id(final CommentCompositeKey id) {
            this.id = id;
            return this;
        }

        public CommentEntity.CommentEntityBuilder authMember(final SiteMemberEntity authMember) {
            this.authMember = authMember;
            return this;
        }

        public CommentEntity.CommentEntityBuilder createMember(final SiteMemberEntity createMember) {
            this.createMember = createMember;
            return this;
        }

        public CommentEntity.CommentEntityBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public CommentEntity.CommentEntityBuilder isDeleted(final Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public CommentEntity.CommentEntityBuilder CommentEntity(final CommentEntity commentEntity) {
            this.postEntity = commentEntity.getPostEntity();
            this.id = commentEntity.getId();
            this.authMember = commentEntity.getAuthMember();
            this.createMember = commentEntity.getCreateMember();
            this.content = commentEntity.getContent();
            this.isDeleted = commentEntity.getIsDeleted();
            return this;
        }

        public CommentEntity build() {
            return new CommentEntity(this.postEntity, this.id.getPath(), this.authMember, this.createMember, this.content, this.isDeleted
            );
        }
    }
}
