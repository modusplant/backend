package kr.modusplant.global.domain.service.crud.supers;

import kr.modusplant.global.domain.model.SiteMember;

import java.util.Optional;

public interface SiteMemberCrudService<T> extends UuidCrudService<T> {
    Optional<T> getByMember(SiteMember member);
}
