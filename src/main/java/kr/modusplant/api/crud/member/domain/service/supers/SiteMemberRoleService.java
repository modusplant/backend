package kr.modusplant.api.crud.member.domain.service.supers;

import kr.modusplant.api.crud.member.domain.model.SiteMemberRole;
import kr.modusplant.global.enums.Role;

import java.util.List;

public interface SiteMemberRoleService extends SiteMemberCrudService<SiteMemberRole> {
    List<SiteMemberRole> getByRole(Role role);
}
