package kr.modusplant.domains.qna.persistence.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.annotation.DefaultValue;
import kr.modusplant.global.persistence.annotation.UlidGenerator;
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

import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SNAKE_QNA_POST)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaPostEntity {
    @Id
    @UlidGenerator
    @Column(nullable = false, updatable = false)
    private String ulid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = SNAKE_GROUP_ORDER, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private PlantGroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = SNAKE_AUTH_MEMB_UUID, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity authMember;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = SNAKE_CREA_MEMB_UUID, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SiteMemberEntity createMember;

    @Column(name = SNAKE_LIKE_COUNT, nullable = false)
    @DefaultValue
    private Integer likeCount;

    @Column(name = SNAKE_VIEW_COUNT, nullable = false)
    @DefaultValue
    private Long viewCount;

    @Column(nullable = false, length = 150)
    private String title;

    @Type(JsonBinaryType.class)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode content;

    @Column(name = SNAKE_IS_DELETED, nullable = false)
    @DefaultValue
    private Boolean isDeleted;

    @Column(name = SNAKE_CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = SNAKE_UPDATED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Long ver;

    public void updateGroup(PlantGroupEntity group) {
        this.group = group;
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
        if (!(o instanceof QnaPostEntity that)) return false;
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

    private QnaPostEntity(String ulid, PlantGroupEntity group, SiteMemberEntity authMember, SiteMemberEntity createMember, Integer likeCount, Long viewCount, String title, JsonNode content, Boolean isDeleted) {
        this.ulid = ulid;
        this.group = group;
        this.authMember = authMember;
        this.createMember = createMember;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.title = title;
        this.content = content;
        this.isDeleted = isDeleted;
    }

    public static QnaPostEntityBuilder builder() {
        return new QnaPostEntityBuilder();
    }

    public static final class QnaPostEntityBuilder {
        private String ulid;
        private PlantGroupEntity group;
        private SiteMemberEntity authMember;
        private SiteMemberEntity createMember;
        private Integer likeCount;
        private Long viewCount;
        private String title;
        private JsonNode content;
        private Boolean isDeleted;

        public QnaPostEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public QnaPostEntityBuilder group(final PlantGroupEntity group) {
            this.group = group;
            return this;
        }

        public QnaPostEntityBuilder authMember(final SiteMemberEntity authMember) {
            this.authMember = authMember;
            return this;
        }

        public QnaPostEntityBuilder createMember(final SiteMemberEntity createMember) {
            this.createMember = createMember;
            return this;
        }

        public QnaPostEntityBuilder likeCount(final Integer likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public QnaPostEntityBuilder viewCount(final Long viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public QnaPostEntityBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public QnaPostEntityBuilder content(final JsonNode content) {
            this.content = content;
            return this;
        }

        public QnaPostEntityBuilder isDeleted(final Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public QnaPostEntityBuilder QnaPostEntity(final QnaPostEntity qnaPostEntity) {
            this.ulid = qnaPostEntity.ulid;
            this.group = qnaPostEntity.group;
            this.authMember = qnaPostEntity.authMember;
            this.createMember = qnaPostEntity.createMember;
            this.likeCount = qnaPostEntity.likeCount;
            this.viewCount = qnaPostEntity.viewCount;
            this.title = qnaPostEntity.title;
            this.content = qnaPostEntity.content;
            this.isDeleted = qnaPostEntity.isDeleted;
            return this;
        }

        public QnaPostEntity build() {
            return new QnaPostEntity(this.ulid,this.group,this.authMember,this.createMember,this.likeCount,this.viewCount,this.title,this.content,this.isDeleted);
        }

    }
}
