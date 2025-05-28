package kr.modusplant.domains.communication.qna.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.global.vo.SnakeCaseWord.SNAKE_CREATED_AT;
import static kr.modusplant.global.vo.SnakeCaseWord.SNAKE_QNA_CATE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SNAKE_QNA_CATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaCategoryEntity {
    @Id
    @Column(nullable = false, updatable = false)
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
        if (!(o instanceof QnaCategoryEntity that)) return false;
        return new EqualsBuilder().append(getOrder(), that.getOrder()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getOrder()).toHashCode();
    }

    private QnaCategoryEntity(Integer order, String category) {
        this.order = order;
        this.category = category;
    }

    public static QnaCategoryEntityBuilder builder() {
        return new QnaCategoryEntityBuilder();
    }

    public static final class QnaCategoryEntityBuilder {
        private Integer order;
        private String category;

        public QnaCategoryEntityBuilder order(final Integer order) {
            this.order = order;
            return this;
        }

        public QnaCategoryEntityBuilder category(final String category) {
            this.category = category;
            return this;
        }

        public QnaCategoryEntityBuilder qnaCategoryEntity(final QnaCategoryEntity qnaCategory) {
            this.order = qnaCategory.getOrder();
            this.category = qnaCategory.getCategory();
            return this;
        }

        public QnaCategoryEntity build() {
            return new QnaCategoryEntity(this.order, this.category);
        }
    }
}
