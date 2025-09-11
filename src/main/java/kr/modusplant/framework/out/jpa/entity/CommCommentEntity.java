package kr.modusplant.framework.out.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.framework.out.jpa.entity.compositekey.CommCommentId;
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
import static kr.modusplant.shared.persistence.vo.TableColumnName.IS_DELETED;
import static kr.modusplant.shared.persistence.vo.TableName.COMM_COMMENT;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_COMMENT)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(CommCommentId.class)
public class CommCommentEntity {

    @Id
    private String postUlid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId("postUlid")
    @JoinColumn(name = "post_ulid", nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private CommPostEntity postEntity;

    @Id
    @Column(name = "path", nullable = false, updatable = false)
    private String path;

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
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    private CommCommentEntity(
            CommPostEntity postEntity, String path,
            SiteMemberEntity authMember, SiteMemberEntity createMember,
            String content, Boolean isDeleted
    ) {
        this.postEntity = postEntity;
        this.path = path;
        this.authMember = authMember;
        this.createMember = createMember;
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
        private SiteMemberEntity createMember;
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

        public CommCommentEntityBuilder createMember(final SiteMemberEntity createMember) {
            this.createMember = createMember;
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

        public CommCommentEntityBuilder CommCommentEntity(final CommCommentEntity commCommentEntity) {
            this.postEntity = commCommentEntity.getPostEntity();
            this.path = commCommentEntity.getPath();
            this.authMember = commCommentEntity.getAuthMember();
            this.createMember = commCommentEntity.getCreateMember();
            this.content = commCommentEntity.getContent();
            this.isDeleted = commCommentEntity.getIsDeleted();
            return this;
        }

        public CommCommentEntity build() {
            return new CommCommentEntity(this.postEntity, this.path, this.authMember, this.createMember, this.content, this.isDeleted
            );
        }
    }
}