package kr.modusplant.domains.communication.qna.persistence.entity;

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
import static kr.modusplant.global.vo.TableName.QNA_CATE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = QNA_CATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaCategoryEntity {
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
        if (!(o instanceof QnaCategoryEntity that)) return false;
        return new EqualsBuilder().append(getOrder(), that.getOrder()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getOrder()).toHashCode();
    }

    private QnaCategoryEntity(UUID uuid, String category, Integer order) {
        this.uuid = uuid;
        this.category = category;
        this.order = order;
    }

    public static QnaCategoryEntityBuilder builder() {
        return new QnaCategoryEntityBuilder();
    }

    public static final class QnaCategoryEntityBuilder {
        private UUID uuid;
        private String category;
        private Integer order;

        public QnaCategoryEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public QnaCategoryEntityBuilder category(final String category) {
            this.category = category;
            return this;
        }

        public QnaCategoryEntityBuilder order(final Integer order) {
            this.order = order;
            return this;
        }

        public QnaCategoryEntityBuilder qnaCategoryEntity(final QnaCategoryEntity qnaCategory) {
            this.uuid = qnaCategory.getUuid();
            this.category = qnaCategory.getCategory();
            this.order = qnaCategory.getOrder();
            return this;
        }

        public QnaCategoryEntity build() {
            return new QnaCategoryEntity(this.uuid, this.category, this.order);
        }
    }
}
