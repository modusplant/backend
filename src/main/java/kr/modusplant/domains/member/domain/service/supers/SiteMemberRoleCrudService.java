package kr.modusplant.domains.member.domain.service.supers;

import kr.modusplant.domains.commons.domain.supers.UuidCrudService;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.global.enums.Role;

import java.util.List;
import java.util.Optional;

public interface SiteMemberRoleCrudService extends UuidCrudService<SiteMemberRole> {
    List<SiteMemberRole> getByRole(Role role);

    Optional<SiteMemberRole> getByMember(SiteMember member);
}
