package kr.modusplant.domains.identity.framework.out.persistence.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.identity.domain.vo.enums.UserRole;
import kr.modusplant.infrastructure.persistence.annotation.DefaultValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableName.SITE_MEMBER_ROLE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER_ROLE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRoleEntity {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @DefaultValue
    private UserRole role;

    public void updateRole(UserRole role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberRoleEntity that)) return false;
        return new EqualsBuilder().append(getUuid(), that.getUuid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getUuid()).toHashCode();
    }

    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = UserRole.USER;
        }
    }

    private MemberRoleEntity(UUID uuid, UserRole role) {
        this.uuid = uuid;
        this.role = role;
    }

    public static MemberRoleEntity.MemberRoleEntityBuilder builder() {
        return new MemberRoleEntity.MemberRoleEntityBuilder();
    }

    public static final class MemberRoleEntityBuilder {
        private UUID uuid;
        private UserRole role;

        public MemberRoleEntity.MemberRoleEntityBuilder uuid(final UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public MemberRoleEntity.MemberRoleEntityBuilder role(final UserRole role) {
            this.role = role;
            return this;
        }

        public MemberRoleEntity build() {
            return new MemberRoleEntity(this.uuid, this.role);
        }
    }
}
