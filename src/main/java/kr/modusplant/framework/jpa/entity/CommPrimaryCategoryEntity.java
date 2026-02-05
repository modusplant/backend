package kr.modusplant.framework.jpa.entity;

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

import static kr.modusplant.shared.persistence.constant.TableColumnName.CREATED_AT;
import static kr.modusplant.shared.persistence.constant.TableColumnName.ORDER;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_PRI_CATE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_PRI_CATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class CommPrimaryCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comm_pri_cate_seq")
    @SequenceGenerator(name = "comm_pri_cate_seq", sequenceName = "comm_pri_cate_id_seq", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Integer id;

    @Column(nullable = false, updatable = false, unique = true)
    private String category;

    @Column(name = ORDER, nullable = false, updatable = false, unique = true)
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
        if (!(o instanceof CommPrimaryCategoryEntity that)) return false;
        return new EqualsBuilder().append(getOrder(), that.getOrder()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getOrder()).toHashCode();
    }

    private CommPrimaryCategoryEntity(Integer id, String category, Integer order) {
        this.id = id;
        this.category = category;
        this.order = order;
    }

    public static CommCategoryEntityBuilder builder() {
        return new CommCategoryEntityBuilder();
    }

    public static final class CommCategoryEntityBuilder {
        private Integer id;
        private String category;
        private Integer order;

        public CommCategoryEntityBuilder id(final Integer id) {
            this.id = id;
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

        public CommCategoryEntityBuilder commPrimaryCategory(final CommPrimaryCategoryEntity commCategory) {
            this.id = commCategory.getId();
            this.category = commCategory.getCategory();
            this.order = commCategory.getOrder();
            return this;
        }

        public CommPrimaryCategoryEntity build() {
            return new CommPrimaryCategoryEntity(this.id, this.category, this.order);
        }
    }
}
