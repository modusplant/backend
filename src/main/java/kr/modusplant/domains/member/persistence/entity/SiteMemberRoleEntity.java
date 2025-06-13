package kr.modusplant.domains.member.persistence.entity;

import jakarta.persistence.*;
import kr.modusplant.global.enums.Role;
import kr.modusplant.global.persistence.annotation.DefaultValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

import static kr.modusplant.global.enums.Role.USER;
import static kr.modusplant.global.vo.TableName.SITE_MEMBER_ROLE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER_ROLE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SiteMemberRoleEntity {
    @Id
    private UUID uuid;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @MapsId
    @JoinColumn(nullable = false, name = "uuid", updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
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

    @PreUpdate
    public void preUpdate() {
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

        public SiteMemberRoleEntityBuilder memberRoleEntity(final SiteMemberRoleEntity memberRole) {
            this.member = memberRole.getMember();
            this.role = memberRole.getRole();
            return this;
        }

        public SiteMemberRoleEntity build() {
            return new SiteMemberRoleEntity(this.member, this.role);
        }
    }
}
