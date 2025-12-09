package kr.modusplant.infrastructure.swear.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.constant.TableColumnName.CREATED_AT;

@Entity
@Table(name = "swear_word")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SwearEntity {

    @Id
    @UuidGenerator
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;

    @Column(nullable = false)
    private String category;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public SwearEntity(String word, String category) {
        this.word = word;
        this.category = category;
    }

    public static SwearWordEntityBuilder builder() { return new SwearWordEntityBuilder(); }

    public static final class SwearWordEntityBuilder {
        private String word;
        private String category;

        public SwearWordEntityBuilder word(final String word) {
            this.word = word;
            return this;
        }

        public SwearWordEntityBuilder category(final String category) {
            this.category = category;
            return this;
        }

        public SwearEntity build() {
            return new SwearEntity(this.word, this.category);
        }
    }

}
