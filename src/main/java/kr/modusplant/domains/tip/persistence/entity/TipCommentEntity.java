package kr.modusplant.domains.tip.persistence.entity;

import jakarta.persistence.*;
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

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "conv_comm")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(TipCommentCompositeKey.class)
public class TipCommentEntity {

    @Id
    private String postUlid;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId("postUlid")
    @JoinColumn(name = "ulid", nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private TipPostEntity postEntity;

    @Id
    @Column(nullable = false, updatable = false)
    private String materializedPath;

    @Column(name = "auth_memb_uuid", nullable = false)
    private UUID authMemberUuid;

    @Column(name = "crea_memb_uuid", nullable = false)
    private UUID createMemberUuid;

    @Column(nullable = false, length = 900)
    private String content;

    @Column(nullable = false)
    @DefaultValue
    private Boolean isDeleted;

    @Column(nullable = false)
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

    @PreUpdate
    public void preUpdate() {
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    private TipCommentEntity(TipPostEntity postEntity, String materializedPath, UUID authMemberUuid, UUID createMemberUuid, String content
    ) {
        this.postEntity = postEntity;
        this.materializedPath = materializedPath;
        this.authMemberUuid = authMemberUuid;
        this.createMemberUuid = createMemberUuid;
        this.content = content;
    }

    public static ConversationCommentEntityBuilder builder() {
        return new ConversationCommentEntityBuilder();
    }

    public static final class ConversationCommentEntityBuilder {
        private TipPostEntity postEntity;
        private String materializedPath;
        private UUID authMemberUuid;
        private UUID createMemberUuid;
        private String content;

        public ConversationCommentEntityBuilder postEntity(final TipPostEntity postEntity) {
            this.postEntity = postEntity;
            return this;
        }

        public ConversationCommentEntityBuilder materializedPath(final String materializedPath) {
            this.materializedPath = materializedPath;
            return this;
        }

        public ConversationCommentEntityBuilder authMemberUuid(final UUID authMemberUuid) {
            this.authMemberUuid = authMemberUuid;
            return this;
        }

        public ConversationCommentEntityBuilder createMemberUuid(final UUID createMemberUuid) {
            this.createMemberUuid = createMemberUuid;
            return this;
        }

        public ConversationCommentEntityBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public ConversationCommentEntityBuilder ConversationCommentEntity(final TipCommentEntity convCommEntity) {
            this.postEntity = convCommEntity.getPostEntity();
            this.materializedPath = convCommEntity.getMaterializedPath();
            this.authMemberUuid = convCommEntity.getAuthMemberUuid();
            this.createMemberUuid = convCommEntity.getCreateMemberUuid();
            this.content = convCommEntity.getContent();
            return this;
        }

        public TipCommentEntity build() {
            return new TipCommentEntity(this.postEntity, this.materializedPath, this.authMemberUuid, this.createMemberUuid, this.content
            );
        }
    }
}