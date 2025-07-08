package kr.modusplant.domains.communication.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.global.vo.DatabaseFieldName.CREATED_AT;
import static kr.modusplant.global.vo.TableName.COMM_SECO_CATE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_SECO_CATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommSecondaryCategoryEntity {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @Column(nullable = false, updatable = false, unique = true)
    private String category;

    @Column(name = "\"order\"", nullable = false, updatable = false, unique = true)
    private Integer order;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public void updateCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommSecondaryCategoryEntity that)) return false;
        return new EqualsBuilder().append(getOrder(), that.getOrder()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getOrder()).toHashCode();
    }

    private CommSecondaryCategoryEntity(UUID uuid, String category, Integer order) {
        this.uuid = uuid;
        this.category = category;
        this.order = order;
    }

    public static CommCategoryEntityBuilder builder() {
        return new CommCategoryEntityBuilder();
    }

    public static final class CommCategoryEntityBuilder {
        private UUID uuid;
        private String category;
        private Integer order;

        public CommCategoryEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public CommCategoryEntityBuilder category(final String category) {
            this.category = category;
            return this;
        }

        public CommCategoryEntityBuilder order(final Integer order) {
            this.order = order;
            return this;
        }

        public CommCategoryEntityBuilder commCategoryEntity(final CommSecondaryCategoryEntity commCategory) {
            this.uuid = commCategory.getUuid();
            this.category = commCategory.getCategory();
            this.order = commCategory.getOrder();
            return this;
        }

        public CommSecondaryCategoryEntity build() {
            return new CommSecondaryCategoryEntity(this.uuid, this.category, this.order);
        }
    }
}
