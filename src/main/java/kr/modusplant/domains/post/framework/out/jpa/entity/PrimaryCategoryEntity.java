package kr.modusplant.domains.post.framework.out.jpa.entity;

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

import static kr.modusplant.shared.persistence.vo.TableColumnName.CREATED_AT;
import static kr.modusplant.shared.persistence.vo.TableName.COMM_PRI_CATE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_PRI_CATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrimaryCategoryEntity {
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
        if (!(o instanceof PrimaryCategoryEntity that)) return false;
        return new EqualsBuilder().append(getOrder(), that.getOrder()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getOrder()).toHashCode();
    }

    private PrimaryCategoryEntity(UUID uuid, String category, Integer order) {
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

        public CommCategoryEntityBuilder commCategoryEntity(final PrimaryCategoryEntity commCategory) {
            this.uuid = commCategory.getUuid();
            this.category = commCategory.getCategory();
            this.order = commCategory.getOrder();
            return this;
        }

        public PrimaryCategoryEntity build() {
            return new PrimaryCategoryEntity(this.uuid, this.category, this.order);
        }
    }
}
