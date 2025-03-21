package kr.modusplant.global.persistence.entity;

import jakarta.persistence.*;
import kr.modusplant.global.persistence.annotation.DefaultValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SNAKE_SITE_MEMBER)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SiteMemberEntity {
    @Id
    @UuidGenerator
    @Column(nullable = false)
    private UUID uuid;

    @Column(nullable = false, length = 40)
    private String nickname;

    @Column(name = SNAKE_BIRTH_DATE)
    private LocalDate birthDate;

    @Column(name = SNAKE_IS_ACTIVE, nullable = false)
    @DefaultValue
    private Boolean isActive;

    @Column(name = SNAKE_IS_DISABLED_BY_LINKING, nullable = false)
    @DefaultValue
    private Boolean isDisabledByLinking;

    @Column(name = SNAKE_IS_BANNED, nullable = false)
    @DefaultValue
    private Boolean isBanned;

    @Column(name = SNAKE_IS_DELETED, nullable = false)
    @DefaultValue
    private Boolean isDeleted;

    @Column(name = SNAKE_LOGGED_IN_AT)
    private LocalDateTime loggedInAt;

    @Column(name = SNAKE_CREATED_AT, nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = SNAKE_LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = SNAKE_VER_NUM, nullable = false)
    private Long versionNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteMemberEntity that)) return false;
        return new EqualsBuilder().append(getUuid(), that.getUuid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getUuid()).toHashCode();
    }

    @PrePersist
    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.isDisabledByLinking == null) {
            this.isDisabledByLinking = false;
        }
        if (this.isBanned == null) {
            this.isBanned = false;
        }
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.isDisabledByLinking == null) {
            this.isDisabledByLinking = false;
        }
        if (this.isBanned == null) {
            this.isBanned = false;
        }
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    private SiteMemberEntity(UUID uuid, String nickname, LocalDate birthDate, Boolean isActive, Boolean isDisabledByLinking, Boolean isBanned, Boolean isDeleted, LocalDateTime loggedInAt) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.isActive = isActive;
        this.isDisabledByLinking = isDisabledByLinking;
        this.isBanned = isBanned;
        this.isDeleted = isDeleted;
        this.loggedInAt = loggedInAt;
    }

    public static SiteMemberEntityBuilder builder() {
        return new SiteMemberEntityBuilder();
    }

    public static final class SiteMemberEntityBuilder {
        private UUID uuid;
        private String nickname;
        private LocalDate birthDate;
        private Boolean isActive;
        private Boolean isDisabledByLinking;
        private Boolean isBanned;
        private Boolean isDeleted;
        private LocalDateTime loggedInAt;

        public SiteMemberEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public SiteMemberEntityBuilder nickname(final String nickname) {
            this.nickname = nickname;
            return this;
        }

        public SiteMemberEntityBuilder birthDate(final LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public SiteMemberEntityBuilder isActive(final Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public SiteMemberEntityBuilder isDisabledByLinking(final Boolean isDisabledByLinking) {
            this.isDisabledByLinking = isDisabledByLinking;
            return this;
        }

        public SiteMemberEntityBuilder isBanned(final Boolean isBanned) {
            this.isBanned = isBanned;
            return this;
        }

        public SiteMemberEntityBuilder isDeleted(final Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public SiteMemberEntityBuilder loggedInAt(final LocalDateTime loggedInAt) {
            this.loggedInAt = loggedInAt;
            return this;
        }

        public SiteMemberEntityBuilder memberEntity(final SiteMemberEntity member) {
            this.uuid = member.getUuid();
            this.nickname = member.getNickname();
            this.birthDate = member.getBirthDate();
            this.isActive = member.getIsActive();
            this.isDisabledByLinking = member.getIsDisabledByLinking();
            this.isBanned = member.getIsBanned();
            this.isDeleted = member.getIsDeleted();
            this.loggedInAt = member.getLoggedInAt();
            return this;
        }

        public SiteMemberEntity build() {
            return new SiteMemberEntity(this.uuid, this.nickname, this.birthDate, this.isActive, this.isDisabledByLinking, this.isBanned, this.isDeleted, this.loggedInAt);
        }
    }
}