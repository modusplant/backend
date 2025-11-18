package kr.modusplant.framework.jpa.repository.supers;

import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.persistence.repository.UuidPrimaryKeyRepository;

import java.util.Optional;

public interface SiteMemberUuidPrimaryKeyJpaRepository<T> extends UuidPrimaryKeyRepository<T> {
    Optional<T> findByMember(SiteMemberEntity member);
}
