package kr.modusplant.domains.identity.social.framework.out.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.infrastructure.persistence.annotation.DefaultValue;
import kr.modusplant.infrastructure.security.enums.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

import static kr.modusplant.infrastructure.security.enums.Role.USER;
import static kr.modusplant.shared.persistence.constant.TableName.SITE_MEMBER_ROLE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER_ROLE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRoleEntity {
    @Id
    private UUID uuid;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId
    @JoinColumn(nullable = false, name = "uuid", updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private MemberEntity member;

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
        if (!(o instanceof MemberRoleEntity that)) return false;
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

    private MemberRoleEntity(MemberEntity member, Role role) {
        this.member = member;
        this.role = role;
    }

    public static SiteMemberRoleEntityBuilder builder() {
        return new SiteMemberRoleEntityBuilder();
    }

    public static final class SiteMemberRoleEntityBuilder {
        private MemberEntity member;
        private Role role;

        public SiteMemberRoleEntityBuilder member(final MemberEntity member) {
            this.member = member;
            return this;
        }

        public SiteMemberRoleEntityBuilder role(final Role role) {
            this.role = role;
            return this;
        }

        public SiteMemberRoleEntityBuilder memberRoleEntity(final MemberRoleEntity memberRole) {
            this.member = memberRole.getMember();
            this.role = memberRole.getRole();
            return this;
        }

        public MemberRoleEntity build() {
            return new MemberRoleEntity(this.member, this.role);
        }
    }
}
