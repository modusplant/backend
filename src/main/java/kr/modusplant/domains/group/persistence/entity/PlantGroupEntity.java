package kr.modusplant.domains.group.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.global.vo.EscapedWord.ORDER;
import static kr.modusplant.global.vo.SnakeCaseWord.SNAKE_CREATED_AT;
import static kr.modusplant.global.vo.SnakeCaseWord.SNAKE_PLANT_GROUP;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SNAKE_PLANT_GROUP)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlantGroupEntity {
    @Id
    @Column(name = ORDER, nullable = false)
    private Integer order;

    @Column(unique = true, nullable = false, length = 40)
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
        if (!(o instanceof PlantGroupEntity that)) return false;
        return new EqualsBuilder().append(getOrder(),that.getOrder()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getOrder()).toHashCode();
    }

    private PlantGroupEntity(Integer order, String category) {
        this.order = order;
        this.category = category;
    }

    public static PlantGroupEntityBuilder builder() {
        return new PlantGroupEntityBuilder();
    }

    public static final class PlantGroupEntityBuilder {
        private Integer order;
        private String category;

        public  PlantGroupEntityBuilder order(final Integer order) {
            this.order = order;
            return this;
        }

        public  PlantGroupEntityBuilder category(final String category) {
            this.category = category;
            return this;
        }

        public PlantGroupEntityBuilder plantGroupEntity(final PlantGroupEntity plantGroupEntity) {
            this.order = plantGroupEntity.getOrder();
            this.category = plantGroupEntity.getCategory();
            return this;
        }

        public PlantGroupEntity build() {
            return new PlantGroupEntity(this.order,this.category);
        }
    }
}
