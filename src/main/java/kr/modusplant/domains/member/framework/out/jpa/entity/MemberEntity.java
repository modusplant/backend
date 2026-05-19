package kr.modusplant.domains.member.framework.out.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.shared.enums.Role;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.SITE_MEMBER;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class MemberEntity {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @Column(nullable = false, length = 40, unique = true)
    private String nickname;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_banned", nullable = false)
    private Boolean isBanned;

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
        if (this.isBanned == null) {
            this.isBanned = false;
        }
        if (this.role == null) {
            this.role = Role.USER;
        }
    }

    private MemberEntity(UUID uuid, String nickname, Role role, Boolean isActive, Boolean isBanned, LocalDateTime loggedInAt) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.role = role;
        this.isActive = isActive;
        this.isBanned = isBanned;
        this.loggedInAt = loggedInAt;
    }

    public static MemberEntityBuilder builder() {
        return new MemberEntityBuilder();
    }

    public static final class MemberEntityBuilder {
        private UUID uuid;
        private String nickname;
        private Role role;
        private Boolean isActive;
        private Boolean isBanned;
        private LocalDateTime loggedInAt;

        public MemberEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public MemberEntityBuilder nickname(final String nickname) {
            this.nickname = nickname;
            return this;
        }

        public MemberEntityBuilder role(final Role role) {
            this.role = role;
            return this;
        }

        public MemberEntityBuilder isActive(final Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public MemberEntityBuilder isBanned(final Boolean isBanned) {
            this.isBanned = isBanned;
            return this;
        }

        public MemberEntityBuilder loggedInAt(final LocalDateTime loggedInAt) {
            this.loggedInAt = loggedInAt;
            return this;
        }

        public MemberEntityBuilder member(final MemberEntity member) {
            this.uuid = member.getUuid();
            this.nickname = member.getNickname();
            this.isActive = member.getIsActive();
            this.isBanned = member.getIsBanned();
            this.loggedInAt = member.getLoggedInAt();
            return this;
        }

        public MemberEntity build() {
            return new MemberEntity(this.uuid, this.nickname, this.role, this.isActive, this.isBanned, this.loggedInAt);
        }
    }
}