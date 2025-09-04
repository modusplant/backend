package kr.modusplant.domains.comment.framework.out.persistence.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.framework.out.persistence.jpa.entity.MemberEntity;
import kr.modusplant.framework.out.persistence.annotation.DefaultValue;
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

import static kr.modusplant.shared.persistence.vo.TableColumnName.*;
import static kr.modusplant.shared.persistence.vo.TableName.SITE_MEMBER;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentMemberEntity {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @Column(nullable = false, length = 40)
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

    @Column(name = IS_DELETED, nullable = false)
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
    private Long versionNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberEntity that)) return false;
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

    private CommentMemberEntity(UUID uuid, String nickname, LocalDate birthDate, Boolean isActive, Boolean isDisabledByLinking, Boolean isBanned, Boolean isDeleted, LocalDateTime loggedInAt) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.isActive = isActive;
        this.isDisabledByLinking = isDisabledByLinking;
        this.isBanned = isBanned;
        this.isDeleted = isDeleted;
        this.loggedInAt = loggedInAt;
    }

    public static CommentMemberEntity.CommentMemberEntityBuilder builder() {
        return new CommentMemberEntity.CommentMemberEntityBuilder();
    }

    public static final class CommentMemberEntityBuilder {
        private UUID uuid;
        private String nickname;
        private LocalDate birthDate;
        private Boolean isActive;
        private Boolean isDisabledByLinking;
        private Boolean isBanned;
        private Boolean isDeleted;
        private LocalDateTime loggedInAt;

        public CommentMemberEntity.CommentMemberEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public CommentMemberEntity.CommentMemberEntityBuilder nickname(final String nickname) {
            this.nickname = nickname;
            return this;
        }

        public CommentMemberEntity.CommentMemberEntityBuilder birthDate(final LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public CommentMemberEntity.CommentMemberEntityBuilder isActive(final Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public CommentMemberEntity.CommentMemberEntityBuilder isDisabledByLinking(final Boolean isDisabledByLinking) {
            this.isDisabledByLinking = isDisabledByLinking;
            return this;
        }

        public CommentMemberEntity.CommentMemberEntityBuilder isBanned(final Boolean isBanned) {
            this.isBanned = isBanned;
            return this;
        }

        public CommentMemberEntity.CommentMemberEntityBuilder isDeleted(final Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public CommentMemberEntity.CommentMemberEntityBuilder loggedInAt(final LocalDateTime loggedInAt) {
            this.loggedInAt = loggedInAt;
            return this;
        }

        public CommentMemberEntity.CommentMemberEntityBuilder CommentMemberEntity(final CommentMemberEntity member) {
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

        public CommentMemberEntity build() {
            return new CommentMemberEntity(this.uuid, this.nickname, this.birthDate, this.isActive, this.isDisabledByLinking, this.isBanned, this.isDeleted, this.loggedInAt);
        }
    }

}
