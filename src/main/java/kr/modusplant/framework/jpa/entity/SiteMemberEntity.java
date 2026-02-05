package kr.modusplant.framework.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.shared.persistence.annotation.DefaultValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.SITE_MEMBER;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class SiteMemberEntity {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @Column(nullable = false, length = 40, unique = true)
    private String nickname;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "is_active", nullable = false)
    @DefaultValue
    private Boolean isActive;

    @Column(name = "is_disabled_by_linking", nullable = false)
    @DefaultValue
    private Boolean isDisabledByLinking;

    @Column(name = "is_banned", nullable = false)
    @DefaultValue
    private Boolean isBanned;

    @Column(name = "is_deleted", nullable = false)
    @DefaultValue
    private Boolean isDeleted;

    @Column(name = "logged_in_at")
    private LocalDateTime loggedInAt;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = VER_NUM, nullable = false)
    @ToString.Exclude
    private Long versionNumber;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void updateIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void updateIsDisabledByLinking(Boolean isDisabledByLinking) {
        this.isDisabledByLinking = isDisabledByLinking;
    }

    public void updateIsBanned(Boolean isBanned) {
        this.isBanned = isBanned;
    }

    public void updateIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void updateLoggedInAt(LocalDateTime loggedInAt) {
        this.loggedInAt = loggedInAt;
    }

    public String getETagSource() {
        return getUuid() + "-" + getVersionNumber();
    }

    public LocalDateTime getLastModifiedAtAsTruncatedToSeconds() {
        return getLastModifiedAt().truncatedTo(ChronoUnit.SECONDS);
    }

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

        public SiteMemberEntityBuilder member(final SiteMemberEntity member) {
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