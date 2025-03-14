package kr.modusplant.global.persistence.entity;

import jakarta.persistence.*;
import kr.modusplant.global.persistence.annotation.DefaultValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@NoArgsConstructor
public class SiteMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID uuid;

    @Setter
    @Column(nullable = false, length = 40)
    private String nickname;

    @Setter
    @Column(name = SNAKE_BIRTH_DATE, nullable = false)
    private LocalDate birthDate;

    @Setter
    @Column(name = SNAKE_IS_ACTIVE, nullable = false)
    @DefaultValue
    private Boolean isActive;

    @Setter
    @Column(name = SNAKE_IS_DISABLED_BY_LINKING, nullable = false)
    @DefaultValue
    private Boolean isDisabledByLinking;

    @Setter
    @Column(name = SNAKE_IS_BANNED, nullable = false)
    @DefaultValue
    private Boolean isBanned;

    @Setter
    @Column(name = SNAKE_IS_DELETED, nullable = false)
    @DefaultValue
    private Boolean isDeleted;

    @Setter
    @Column(name = SNAKE_LOGGED_IN_AT, nullable = false)
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

    public SiteMemberEntity(String nickname, LocalDate birthDate, Boolean isActive, Boolean isDisabledByLinking, Boolean isBanned, Boolean isDeleted, LocalDateTime loggedInAt) {
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
        private String nickname;
        private LocalDate birthDate;
        private Boolean isActive;
        private Boolean isDisabledByLinking;
        private Boolean isBanned;
        private Boolean isDeleted;
        private LocalDateTime loggedInAt;

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
            return new SiteMemberEntity(this.nickname, this.birthDate, this.isActive, this.isDisabledByLinking, this.isBanned, this.isDeleted, this.loggedInAt);
        }
    }
}