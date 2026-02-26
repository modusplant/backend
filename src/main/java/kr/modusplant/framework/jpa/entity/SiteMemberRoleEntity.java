package kr.modusplant.framework.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.persistence.annotation.DefaultValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

import static kr.modusplant.infrastructure.security.enums.Role.USER;
import static kr.modusplant.shared.persistence.constant.TableName.SITE_MEMBER_ROLE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER_ROLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class SiteMemberRoleEntity {
    @Id
    private UUID uuid;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId
    @JoinColumn(nullable = false, name = "uuid", updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private SiteMemberEntity member;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @DefaultValue
    private Role role;

    public void updateRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteMemberRoleEntity that)) return false;
        return new EqualsBuilder().append(getMember(), that.getMember()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMember()).toHashCode();
    }

    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = USER;
        }
    }

    private SiteMemberRoleEntity(SiteMemberEntity member, Role role) {
        this.member = member;
        this.role = role;
    }

    public static SiteMemberRoleEntityBuilder builder() {
        return new SiteMemberRoleEntityBuilder();
    }

    public static final class SiteMemberRoleEntityBuilder {
        private SiteMemberEntity member;
        private Role role;

        public SiteMemberRoleEntityBuilder member(final SiteMemberEntity member) {
            this.member = member;
            return this;
        }

        public SiteMemberRoleEntityBuilder role(final Role role) {
            this.role = role;
            return this;
        }

        public SiteMemberRoleEntityBuilder memberRole(final SiteMemberRoleEntity memberRole) {
            this.member = memberRole.getMember();
            this.role = memberRole.getRole();
            return this;
        }

        public SiteMemberRoleEntity build() {
            return new SiteMemberRoleEntity(this.member, this.role);
        }
    }
}
