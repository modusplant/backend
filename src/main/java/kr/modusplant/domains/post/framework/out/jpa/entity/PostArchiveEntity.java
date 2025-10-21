package kr.modusplant.domains.post.framework.out.jpa.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.POST_ARCHIVE;


@Entity
@Table(name = POST_ARCHIVE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostArchiveEntity {
    @Id
    @Column(nullable = false, updatable = false)
    private String ulid;

    @Column(name = PRI_CATE_UUID, nullable = false)
    private UUID primaryCategoryUuid;

    @Column(name = SECO_CATE_UUID, nullable = false)
    private UUID secondaryCategoryUuid;

    @Column(name = AUTH_MEMB_UUID, nullable = false)
    private UUID authMemberUuid;

    @Column(name = CREA_MEMB_UUID, nullable = false)
    private UUID createMemberUuid;

    @Column(nullable = false, length = 150)
    private String title;

    @Type(JsonBinaryType.class)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode content;

    @Column(name = CREATED_AT, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "published_at", nullable = true)
    private LocalDateTime publishedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostArchiveEntity that)) return false;
        return new EqualsBuilder().append(getUlid(), that.getUlid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37).append(getUlid()).toHashCode();
    }

    private PostArchiveEntity(String ulid, UUID primaryCategoryUuid, UUID secondaryCategoryUuid, UUID authMemberUuid, UUID createMemberUuid,  String title, JsonNode content, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime publishedAt) {
        this.ulid = ulid;
        this.primaryCategoryUuid = primaryCategoryUuid;
        this.secondaryCategoryUuid = secondaryCategoryUuid;
        this.authMemberUuid = authMemberUuid;
        this.createMemberUuid = createMemberUuid;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.publishedAt = publishedAt;
    }

    public static PostArchiveEntityBuilder builder() {
        return new PostArchiveEntityBuilder();
    }

    public static final class PostArchiveEntityBuilder {
        private String ulid;
        private UUID primaryCategoryUuid;
        private UUID secondaryCategoryUuid;
        private UUID authMemberUuid;
        private UUID createMemberUuid;
        private String title;
        private JsonNode content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime publishedAt;

        public PostArchiveEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public PostArchiveEntityBuilder primaryCategoryUuid(UUID primaryCategoryUuid) {
            this.primaryCategoryUuid = primaryCategoryUuid;
            return this;
        }

        public PostArchiveEntityBuilder secondaryCategoryUuid(UUID secondaryCategoryUuid) {
            this.secondaryCategoryUuid = secondaryCategoryUuid;
            return this;
        }

        public PostArchiveEntityBuilder authMemberUuid(UUID authMemberUuid) {
            this.authMemberUuid = authMemberUuid;
            return this;
        }

        public PostArchiveEntityBuilder createMemberUuid(UUID createMemberUuid) {
            this.createMemberUuid = createMemberUuid;
            return this;
        }

        public PostArchiveEntityBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PostArchiveEntityBuilder content(JsonNode content) {
            this.content = content;
            return this;
        }

        public PostArchiveEntityBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PostArchiveEntityBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PostArchiveEntityBuilder publishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public PostArchiveEntityBuilder commPostEntity(final PostArchiveEntity postEntity) {
            this.ulid = postEntity.ulid;
            this.primaryCategoryUuid = postEntity.primaryCategoryUuid;
            this.secondaryCategoryUuid = postEntity.secondaryCategoryUuid;
            this.authMemberUuid = postEntity.authMemberUuid;
            this.createMemberUuid = postEntity.createMemberUuid;
            this.title = postEntity.title;
            this.content = postEntity.content;
            this.createdAt = postEntity.createdAt;
            this.updatedAt = postEntity.updatedAt;
            this.publishedAt = postEntity.publishedAt;
            return this;
        }

        public PostArchiveEntity build() {
            return new PostArchiveEntity(this.ulid, this.primaryCategoryUuid, this.secondaryCategoryUuid, this.authMemberUuid, this.createMemberUuid, this.title, this.content, this.createdAt, this.updatedAt, this.publishedAt);
        }

    }
}
