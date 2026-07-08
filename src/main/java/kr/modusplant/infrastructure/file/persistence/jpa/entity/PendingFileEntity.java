package kr.modusplant.infrastructure.file.persistence.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.shared.framework.jpa.generator.UlidGenerator;
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
import static kr.modusplant.shared.persistence.constant.TableName.PENDING_FILE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = PENDING_FILE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class PendingFileEntity {

    @Id
    @UlidGenerator
    @Column(nullable = false, updatable = false)
    private String ulid;

    @Column(name = "file_key", nullable = false, unique = true, length = 255)
    private String fileKey;

    @Column(nullable = false, length = 50)
    private String domain;

    @CreatedDate
    @Column(name = CREATED_AT, nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PendingFileEntity other)) return false;
        return new EqualsBuilder().append(ulid, other.ulid).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(ulid).toHashCode();
    }

    private PendingFileEntity(String fileKey, String domain) {
        this.fileKey = fileKey;
        this.domain  = domain;
    }

    public static PendingFileEntityBuilder builder() {
        return new PendingFileEntityBuilder();
    }

    public static final class PendingFileEntityBuilder {
        private String fileKey;
        private String domain;

        public PendingFileEntityBuilder fileKey(final String fileKey) {
            this.fileKey = fileKey;
            return this;
        }

        public PendingFileEntityBuilder domain(final String domain) {
            this.domain = domain;
            return this;
        }

        public PendingFileEntityBuilder pendingFile(final PendingFileEntity pendingFileEntity) {
            this.fileKey = pendingFileEntity.fileKey;
            this.domain = pendingFileEntity.domain;
            return this;
        }

        public PendingFileEntity build() {
            return new PendingFileEntity(this.fileKey, this.domain);
        }
    }

}
