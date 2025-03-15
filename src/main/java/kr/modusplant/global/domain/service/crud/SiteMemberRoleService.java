package kr.modusplant.global.domain.service.crud;

import kr.modusplant.global.domain.model.SiteMemberRole;
import kr.modusplant.global.domain.service.crud.supers.UuidCrudService;
import kr.modusplant.global.enums.Role;

import java.util.List;

public interface SiteMemberRoleService extends UuidCrudService<SiteMemberRole> {
    List<SiteMemberRole> getByRole(Role role);
}
