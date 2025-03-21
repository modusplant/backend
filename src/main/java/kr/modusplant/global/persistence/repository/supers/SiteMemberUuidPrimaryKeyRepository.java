package kr.modusplant.global.persistence.repository.supers;

import kr.modusplant.global.persistence.entity.SiteMemberEntity;

import java.util.Optional;

public interface SiteMemberUuidPrimaryKeyRepository<T> extends UuidPrimaryKeyRepository<T> {
    Optional<T> findByMember(SiteMemberEntity member);
}
