package kr.modusplant.infrastructure.swear.persistence.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.infrastructure.swear.persistence.jpa.entity.enums.SwearType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.constant.TableColumnName.CREATED_AT;

@Entity
@Table(name = "swear")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SwearEntity {

    @Id
    @UuidGenerator
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SwearType type;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public SwearEntity(String word, SwearType type) {
        this.word = word;
        this.type = type;
    }

    public static SwearWordEntityBuilder builder() { return new SwearWordEntityBuilder(); }

    public static final class SwearWordEntityBuilder {
        private String word;
        private SwearType type;

        public SwearWordEntityBuilder word(final String word) {
            this.word = word;
            return this;
        }

        public SwearWordEntityBuilder type(final SwearType type) {
            this.type = type;
            return this;
        }

        public SwearEntity build() {
            return new SwearEntity(this.word, this.type);
        }
    }

}
