package kr.modusplant.framework.out.jpa.repository.supers;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;

import java.util.Optional;

public interface SiteMemberUuidPrimaryKeyJpaRepository<T> extends UuidPrimaryKeyRepository<T> {
    Optional<T> findByMember(SiteMemberEntity member);
}
