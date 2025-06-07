package kr.modusplant.domains.communication.conversation.persistence.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.communication.conversation.persistence.entity.compositekey.ConvCommentCompositeKey;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.annotation.DefaultValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.domains.member.vo.MemberUuid.SNAKE_AUTH_MEMB_UUID;
import static kr.modusplant.domains.member.vo.MemberUuid.SNAKE_CREA_MEMB_UUID;
import static kr.modusplant.global.vo.SnakeCaseWord.SNAKE_IS_DELETED;
import static kr.modusplant.global.vo.TableName.CONV_COMM;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = CONV_COMM)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ConvCommentCompositeKey.class)
public class ConvCommentEntity {

    @Id
    private String postUlid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId("postUlid")
    @JoinColumn(name = "post_ulid", nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ConvPostEntity postEntity;

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

    @Column(name = SNAKE_IS_DELETED, nullable = false)
    @DefaultValue
    private Boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConvCommentEntity that)) return false;

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

    private ConvCommentEntity(
            ConvPostEntity postEntity, String path,
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

    public static ConvCommentEntityBuilder builder() {
        return new ConvCommentEntityBuilder();
    }

    public static final class ConvCommentEntityBuilder {
        private ConvPostEntity postEntity;
        private String path;
        private SiteMemberEntity authMember;
        private SiteMemberEntity createMember;
        private String content;
        private Boolean isDeleted;

        public ConvCommentEntityBuilder postEntity(final ConvPostEntity postEntity) {
            this.postEntity = postEntity;
            return this;
        }

        public ConvCommentEntityBuilder path(final String path) {
            this.path = path;
            return this;
        }

        public ConvCommentEntityBuilder authMember(final SiteMemberEntity authMember) {
            this.authMember = authMember;
            return this;
        }

        public ConvCommentEntityBuilder createMember(final SiteMemberEntity createMember) {
            this.createMember = createMember;
            return this;
        }

        public ConvCommentEntityBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public ConvCommentEntityBuilder isDeleted(final Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public ConvCommentEntityBuilder ConvCommentEntity(final ConvCommentEntity convCommentEntity) {
            this.postEntity = convCommentEntity.getPostEntity();
            this.path = convCommentEntity.getPath();
            this.authMember = convCommentEntity.getAuthMember();
            this.createMember = convCommentEntity.getCreateMember();
            this.content = convCommentEntity.getContent();
            this.isDeleted = convCommentEntity.getIsDeleted();
            return this;
        }

        public ConvCommentEntity build() {
            return new ConvCommentEntity(this.postEntity, this.path, this.authMember, this.createMember, this.content, this.isDeleted
            );
        }
    }
}