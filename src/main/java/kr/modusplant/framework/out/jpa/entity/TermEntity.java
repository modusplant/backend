package kr.modusplant.framework.out.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.infrastructure.persistence.annotation.DefaultValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.infrastructure.persistence.constant.EntityFieldName.VER;
import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.TERM;
import static kr.modusplant.shared.util.VersionUtils.createVersion;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = TERM)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermEntity {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @Column(nullable = false, updatable = false, length = 40)
    private String name;

    @Column(nullable = false, length = 60000)
    private String content;

    @Column(name = VER, nullable = false, length = 10)
    @DefaultValue
    private String version;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = VER_NUM, nullable = false)
    private Long versionNumber;

    public void updateContent(String content) { this.content = content; }

    public void updateVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TermEntity that)) return false;
        return new EqualsBuilder().append(getUuid(), that.getUuid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getUuid()).toHashCode();
    }

    @PrePersist
    public void prePersist() {
        if (this.version == null) {
            this.version = createVersion(1, 0, 0);
        }
    }

    private TermEntity(UUID uuid, String name, String content, String version) {
        this.uuid = uuid;
        this.name = name;
        this.content = content;
        this.version = version;
    }

    public static TermEntityBuilder builder() {
        return new TermEntityBuilder();
    }

    public static final class TermEntityBuilder {
        private UUID uuid;
        private String name;
        private String content;
        private String version;

        public TermEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public TermEntityBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public TermEntityBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public TermEntityBuilder version(final String version) {
            this.version = version;
            return this;
        }

        public TermEntityBuilder term(final TermEntity term) {
            this.uuid = term.getUuid();
            this.name = term.getName();
            this.content = term.getContent();
            this.version = term.getVersion();
            return this;
        }

        public TermEntity build() {
            return new TermEntity(this.uuid, this.name, this.content, this.version);
        }
    }
}
