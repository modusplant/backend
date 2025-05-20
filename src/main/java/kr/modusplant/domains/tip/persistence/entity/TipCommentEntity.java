package kr.modusplant.domains.tip.persistence.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.tip.persistence.entity.compositekey.TipCommentCompositeKey;
import kr.modusplant.global.persistence.annotation.DefaultValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tip_comm")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(TipCommentCompositeKey.class)
public class TipCommentEntity {

    @Id
    private String postUlid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId("postUlid")
    @JoinColumn(name = "post_ulid", nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private TipPostEntity postEntity;

    @Id
    @Column(name = "mate_path", nullable = false, updatable = false)
    private String materializedPath;

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
        if (!(o instanceof TipCommentEntity that)) return false;

        return new EqualsBuilder()
                .append(getPostUlid(), that.getPostUlid())
                .append(getMaterializedPath(), that.getMaterializedPath())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPostUlid())
                .append(getMaterializedPath())
                .toHashCode();
    }

    @PrePersist
    public void prePersist() {
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    private TipCommentEntity(
            TipPostEntity postEntity, String materializedPath,
            SiteMemberEntity authMember, SiteMemberEntity createMember,
            String content, Boolean isDeleted
    ) {
        this.postEntity = postEntity;
        this.materializedPath = materializedPath;
        this.authMember = authMember;
        this.createMember = createMember;
        this.content = content;
        this.isDeleted = isDeleted;
    }

    public static TipCommentEntityBuilder builder() {
        return new TipCommentEntityBuilder();
    }

    public static final class TipCommentEntityBuilder {
        private TipPostEntity postEntity;
        private String materializedPath;
        private SiteMemberEntity authMember;
        private SiteMemberEntity createMember;
        private String content;
        private Boolean isDeleted;

        public TipCommentEntityBuilder postEntity(final TipPostEntity postEntity) {
            this.postEntity = postEntity;
            return this;
        }

        public TipCommentEntityBuilder materializedPath(final String materializedPath) {
            this.materializedPath = materializedPath;
            return this;
        }

        public TipCommentEntityBuilder authMember(final SiteMemberEntity authMember) {
            this.authMember = authMember;
            return this;
        }

        public TipCommentEntityBuilder createMember(final SiteMemberEntity createMember) {
            this.createMember = createMember;
            return this;
        }

        public TipCommentEntityBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public TipCommentEntityBuilder isDeleted(final Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public TipCommentEntityBuilder TipCommentEntity(final TipCommentEntity tipCommentEntity) {
            this.postEntity = tipCommentEntity.getPostEntity();
            this.materializedPath = tipCommentEntity.getMaterializedPath();
            this.authMember = tipCommentEntity.getAuthMember();
            this.createMember = tipCommentEntity.getCreateMember();
            this.content = tipCommentEntity.getContent();
            this.isDeleted = tipCommentEntity.getIsDeleted();
            return this;
        }

        public TipCommentEntity build() {
            return new TipCommentEntity(this.postEntity, this.materializedPath, this.authMember, this.createMember, this.content, this.isDeleted
            );
        }
    }
}