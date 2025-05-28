package kr.modusplant.domains.communication.conversation.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SNAKE_CONV_CATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConvCategoryEntity {
    @Id
    @Column(name = "\"order\"", nullable = false, updatable = false)
    private Integer order;

    @Column(nullable = false, updatable = false, unique = true)
    private String category;

    @Column(name = SNAKE_CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public void updateCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConvCategoryEntity that)) return false;
        return new EqualsBuilder().append(getOrder(), that.getOrder()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getOrder()).toHashCode();
    }

    private ConvCategoryEntity(Integer order, String category) {
        this.order = order;
        this.category = category;
    }

    public static ConvCategoryEntityBuilder builder() {
        return new ConvCategoryEntityBuilder();
    }

    public static final class ConvCategoryEntityBuilder {
        private Integer order;
        private String category;

        public ConvCategoryEntityBuilder order(final Integer order) {
            this.order = order;
            return this;
        }

        public ConvCategoryEntityBuilder category(final String category) {
            this.category = category;
            return this;
        }

        public ConvCategoryEntityBuilder convCategoryEntity(final ConvCategoryEntity convCategory) {
            this.order = convCategory.getOrder();
            this.category = convCategory.getCategory();
            return this;
        }

        public ConvCategoryEntity build() {
            return new ConvCategoryEntity(this.order, this.category);
        }
    }
}
