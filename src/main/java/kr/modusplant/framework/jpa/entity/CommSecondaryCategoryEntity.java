package kr.modusplant.framework.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_SECO_CATE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_SECO_CATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommSecondaryCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comm_seco_cate_seq")
    @SequenceGenerator(name = "comm_seco_cate_seq", sequenceName = "comm_seco_cate_id_seq", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = PRI_CATE_ID, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private CommPrimaryCategoryEntity primaryCategoryEntity;

    @Column(nullable = false, updatable = false)
    private String category;

    @Column(name = ORDER, nullable = false, updatable = false)
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

    private CommSecondaryCategoryEntity(Integer id, CommPrimaryCategoryEntity primaryCategoryEntity, String category, Integer order) {
        this.id = id;
        this.primaryCategoryEntity = primaryCategoryEntity;
        this.category = category;
        this.order = order;
    }

    public static CommSecondaryCategoryEntityBuilder builder() {
        return new CommSecondaryCategoryEntityBuilder();
    }

    public static final class CommSecondaryCategoryEntityBuilder {
        private Integer id;
        private CommPrimaryCategoryEntity primaryCategoryEntity;
        private String category;
        private Integer order;

        public CommSecondaryCategoryEntityBuilder id(final Integer id) {
            this.id = id;
            return this;
        }

        public CommSecondaryCategoryEntityBuilder primaryCategory(CommPrimaryCategoryEntity primaryCategoryEntity) {
            this.primaryCategoryEntity = primaryCategoryEntity;
            return this;
        }

        public CommSecondaryCategoryEntityBuilder category(final String category) {
            this.category = category;
            return this;
        }

        public CommSecondaryCategoryEntityBuilder order(final Integer order) {
            this.order = order;
            return this;
        }

        public CommSecondaryCategoryEntityBuilder commSecondaryCategory(final CommSecondaryCategoryEntity commCategory) {
            this.id = commCategory.getId();
            this.primaryCategoryEntity = commCategory.getPrimaryCategoryEntity();
            this.category = commCategory.getCategory();
            this.order = commCategory.getOrder();
            return this;
        }

        public CommSecondaryCategoryEntity build() {
            return new CommSecondaryCategoryEntity(this.id, this.primaryCategoryEntity, this.category, this.order);
        }
    }
}
