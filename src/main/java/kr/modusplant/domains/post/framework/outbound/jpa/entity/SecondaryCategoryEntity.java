package kr.modusplant.domains.post.framework.outbound.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class SecondaryCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comm_seco_cate_seq")
    @SequenceGenerator(name = "comm_seco_cate_seq", sequenceName = "comm_seco_cate_id_seq", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = PRI_CATE_ID, nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private PrimaryCategoryEntity primaryCategory;

    @Column(nullable = false, updatable = false)
    private String category;

    @Column(name = ORDER, nullable = false, updatable = false)
    private Integer order;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecondaryCategoryEntity that)) return false;
        return new EqualsBuilder().append(getOrder(), that.getOrder()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getOrder()).toHashCode();
    }

    private SecondaryCategoryEntity(Integer id, PrimaryCategoryEntity primaryCategory, String category, Integer order) {
        this.id = id;
        this.primaryCategory = primaryCategory;
        this.category = category;
        this.order = order;
    }

    public static SecondaryCategoryEntityBuilder builder() {
        return new SecondaryCategoryEntityBuilder();
    }

    public static final class SecondaryCategoryEntityBuilder {
        private Integer id;
        private PrimaryCategoryEntity primaryCategory;
        private String category;
        private Integer order;

        public SecondaryCategoryEntityBuilder id(final Integer id) {
            this.id = id;
            return this;
        }

        public SecondaryCategoryEntityBuilder primaryCategory(PrimaryCategoryEntity primaryCategory) {
            this.primaryCategory = primaryCategory;
            return this;
        }

        public SecondaryCategoryEntityBuilder category(final String category) {
            this.category = category;
            return this;
        }

        public SecondaryCategoryEntityBuilder order(final Integer order) {
            this.order = order;
            return this;
        }

        public SecondaryCategoryEntityBuilder secondaryCategory(final SecondaryCategoryEntity secondaryCategory) {
            this.id = secondaryCategory.getId();
            this.primaryCategory = secondaryCategory.getPrimaryCategory();
            this.category = secondaryCategory.getCategory();
            this.order = secondaryCategory.getOrder();
            return this;
        }

        public SecondaryCategoryEntity build() {
            return new SecondaryCategoryEntity(this.id, this.primaryCategory, this.category, this.order);
        }
    }
}
