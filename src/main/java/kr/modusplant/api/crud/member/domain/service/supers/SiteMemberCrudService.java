package kr.modusplant.api.crud.member.domain.service.supers;

import kr.modusplant.api.crud.common.domain.supers.UuidCrudService;
import kr.modusplant.api.crud.member.domain.model.SiteMember;

import java.util.Optional;

public interface SiteMemberCrudService<T> extends UuidCrudService<T> {
    Optional<T> getByMember(SiteMember member);
}
