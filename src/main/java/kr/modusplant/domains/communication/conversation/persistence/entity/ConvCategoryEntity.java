package kr.modusplant.domains.communication.conversation.persistence.entity;

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
import static kr.modusplant.global.vo.TableName.CONV_CATE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = CONV_CATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConvCategoryEntity {
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
        if (!(o instanceof ConvCategoryEntity that)) return false;
        return new EqualsBuilder().append(getOrder(), that.getOrder()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getOrder()).toHashCode();
    }

    private ConvCategoryEntity(UUID uuid, String category, Integer order) {
        this.uuid = uuid;
        this.category = category;
        this.order = order;
    }

    public static ConvCategoryEntityBuilder builder() {
        return new ConvCategoryEntityBuilder();
    }

    public static final class ConvCategoryEntityBuilder {
        private UUID uuid;
        private String category;
        private Integer order;

        public ConvCategoryEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public ConvCategoryEntityBuilder category(final String category) {
            this.category = category;
            return this;
        }

        public ConvCategoryEntityBuilder order(final Integer order) {
            this.order = order;
            return this;
        }

        public ConvCategoryEntityBuilder convCategoryEntity(final ConvCategoryEntity convCategory) {
            this.uuid = convCategory.getUuid();
            this.category = convCategory.getCategory();
            this.order = convCategory.getOrder();
            return this;
        }

        public ConvCategoryEntity build() {
            return new ConvCategoryEntity(this.uuid, this.category, this.order);
        }
    }
}
