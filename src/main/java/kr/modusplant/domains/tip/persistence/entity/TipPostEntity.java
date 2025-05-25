package kr.modusplant.domains.tip.persistence.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.annotation.DefaultValue;
import kr.modusplant.global.persistence.annotation.Ulid;
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
@Table(name = SNAKE_TIP_POST)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TipPostEntity {
    @Id
    @Ulid
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

    public void updateLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
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
        if (!(o instanceof TipPostEntity that)) return false;
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

    private TipPostEntity(String ulid, PlantGroupEntity group, SiteMemberEntity authMember, SiteMemberEntity createMember, Integer likeCount, Long viewCount, String title, JsonNode content, Boolean isDeleted) {
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

    public static TipPostEntityBuilder builder() {
        return new TipPostEntityBuilder();
    }

    public static final class TipPostEntityBuilder {
        private String ulid;
        private PlantGroupEntity group;
        private SiteMemberEntity authMember;
        private SiteMemberEntity createMember;
        private Integer likeCount;
        private Long viewCount;
        private String title;
        private JsonNode content;
        private Boolean isDeleted;

        public TipPostEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public TipPostEntityBuilder group(final PlantGroupEntity group) {
            this.group = group;
            return this;
        }

        public TipPostEntityBuilder authMember(final SiteMemberEntity authMember) {
            this.authMember = authMember;
            return this;
        }

        public TipPostEntityBuilder createMember(final SiteMemberEntity createMember) {
            this.createMember = createMember;
            return this;
        }

        public TipPostEntityBuilder likeCount(final Integer likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public TipPostEntityBuilder viewCount(final Long viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public TipPostEntityBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public TipPostEntityBuilder content(final JsonNode content) {
            this.content = content;
            return this;
        }

        public TipPostEntityBuilder isDeleted(final Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public TipPostEntityBuilder tipPostEntity(final TipPostEntity tipPostEntity) {
            this.ulid = tipPostEntity.ulid;
            this.group = tipPostEntity.group;
            this.authMember = tipPostEntity.authMember;
            this.createMember = tipPostEntity.createMember;
            this.likeCount = tipPostEntity.likeCount;
            this.viewCount = tipPostEntity.viewCount;
            this.title = tipPostEntity.title;
            this.content = tipPostEntity.content;
            this.isDeleted = tipPostEntity.isDeleted;
            return this;
        }

        public TipPostEntity build() {
            return new TipPostEntity(this.ulid,this.group,this.authMember,this.createMember,this.likeCount,this.viewCount,this.title,this.content,this.isDeleted);
        }

    }
}
