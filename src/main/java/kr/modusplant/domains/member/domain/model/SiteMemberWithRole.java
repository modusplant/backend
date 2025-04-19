package kr.modusplant.domains.member.domain.model;

import kr.modusplant.global.enums.Role;

import java.util.UUID;

public record SiteMemberWithRole(SiteMember member, SiteMemberRole role) {
    public UUID getMemberUuid() {
        return member.getUuid();
    }

    public String getNickname() {
        return member.getNickname();
    }

    public Role getRole() {
        return role.getRole();
    }
}