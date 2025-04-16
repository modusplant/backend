package kr.modusplant.api.crud.member.domain.service.supers;

import kr.modusplant.api.crud.common.domain.supers.UuidCrudService;
import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.domain.model.SiteMemberRole;
import kr.modusplant.global.enums.Role;

import java.util.List;
import java.util.Optional;

public interface SiteMemberRoleCrudService extends UuidCrudService<SiteMemberRole> {
    List<SiteMemberRole> getByRole(Role role);

    Optional<SiteMemberRole> getByMember(SiteMember member);
}
