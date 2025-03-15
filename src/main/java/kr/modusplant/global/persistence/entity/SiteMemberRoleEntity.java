package kr.modusplant.global.persistence.entity;

import jakarta.persistence.*;
import kr.modusplant.global.enums.Role;
import kr.modusplant.global.persistence.annotation.DefaultValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

import static kr.modusplant.global.enums.Role.ROLE_USER;
import static kr.modusplant.global.vo.SnakeCaseWord.SNAKE_SITE_MEMBER_ROLE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SNAKE_SITE_MEMBER_ROLE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SiteMemberRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID uuid;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @DefaultValue
    private Role role;

    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = ROLE_USER;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.role == null) {
            this.role = ROLE_USER;
        }
    }

    public SiteMemberRoleEntity(UUID uuid, Role role) {
        this.uuid = uuid;
        this.role = role;
    }

    public static SiteMemberRoleEntityBuilder builder() {
        return new SiteMemberRoleEntityBuilder();
    }

    public static final class SiteMemberRoleEntityBuilder {
        private UUID uuid;
        private Role role;

        public SiteMemberRoleEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public SiteMemberRoleEntityBuilder role(final Role role) {
            this.role = role;
            return this;
        }

        public SiteMemberRoleEntityBuilder memberRoleEntity(final SiteMemberRoleEntity memberRole) {
            this.uuid = memberRole.getUuid();
            this.role = memberRole.getRole();
            return this;
        }

        public SiteMemberRoleEntity build() {
            return new SiteMemberRoleEntity(this.uuid, this.role);
        }
    }
}
