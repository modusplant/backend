package kr.modusplant.domains.post.framework.out.jpa.entity;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class PrimaryCategoryEntity {
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

    private PrimaryCategoryEntity(Integer id, String category, Integer order) {
        this.id = id;
        this.category = category;
        this.order = order;
    }

    public static PrimaryCategoryEntityBuilder builder() {
        return new PrimaryCategoryEntityBuilder();
    }

    public static final class PrimaryCategoryEntityBuilder {
        private Integer id;
        private String category;
        private Integer order;

        public PrimaryCategoryEntityBuilder id(final Integer id) {
            this.id = id;
            return this;
        }

        public PrimaryCategoryEntityBuilder category(final String category) {
            this.category = category;
            return this;
        }

        public PrimaryCategoryEntityBuilder order(final Integer order) {
            this.order = order;
            return this;
        }

        public PrimaryCategoryEntityBuilder primaryCategory(final PrimaryCategoryEntity commCategory) {
            this.id = commCategory.getId();
            this.category = commCategory.getCategory();
            this.order = commCategory.getOrder();
            return this;
        }

        public PrimaryCategoryEntity build() {
            return new PrimaryCategoryEntity(this.id, this.category, this.order);
        }
    }
}
