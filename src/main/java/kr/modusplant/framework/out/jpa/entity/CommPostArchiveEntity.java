package kr.modusplant.framework.out.jpa.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_POST_ARCHIVE;


@Entity
@Table(name = COMM_POST_ARCHIVE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommPostArchiveEntity {
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

    @Column(nullable = false, length = 60)
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
        if (!(o instanceof CommPostArchiveEntity that)) return false;
        return new EqualsBuilder().append(getUlid(), that.getUlid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37).append(getUlid()).toHashCode();
    }

    private CommPostArchiveEntity(String ulid, UUID primaryCategoryUuid, UUID secondaryCategoryUuid, UUID authMemberUuid, UUID createMemberUuid, String title, JsonNode content, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime publishedAt) {
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

    public static CommPostArchiveEntityBuilder builder() {
        return new CommPostArchiveEntityBuilder();
    }

    public static final class CommPostArchiveEntityBuilder {
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

        public CommPostArchiveEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public CommPostArchiveEntityBuilder primaryCategoryUuid(UUID primaryCategoryUuid) {
            this.primaryCategoryUuid = primaryCategoryUuid;
            return this;
        }

        public CommPostArchiveEntityBuilder secondaryCategoryUuid(UUID secondaryCategoryUuid) {
            this.secondaryCategoryUuid = secondaryCategoryUuid;
            return this;
        }

        public CommPostArchiveEntityBuilder authMemberUuid(UUID authMemberUuid) {
            this.authMemberUuid = authMemberUuid;
            return this;
        }

        public CommPostArchiveEntityBuilder createMemberUuid(UUID createMemberUuid) {
            this.createMemberUuid = createMemberUuid;
            return this;
        }

        public CommPostArchiveEntityBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CommPostArchiveEntityBuilder content(JsonNode content) {
            this.content = content;
            return this;
        }

        public CommPostArchiveEntityBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CommPostArchiveEntityBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CommPostArchiveEntityBuilder publishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public CommPostArchiveEntityBuilder commPostEntity(final CommPostArchiveEntity postEntity) {
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

        public CommPostArchiveEntity build() {
            return new CommPostArchiveEntity(this.ulid, this.primaryCategoryUuid, this.secondaryCategoryUuid, this.authMemberUuid, this.createMemberUuid, this.title, this.content, this.createdAt, this.updatedAt, this.publishedAt);
        }

    }
}
