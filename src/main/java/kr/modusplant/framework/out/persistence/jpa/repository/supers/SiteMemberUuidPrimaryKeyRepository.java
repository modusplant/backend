package kr.modusplant.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.framework.out.persistence.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;

import java.util.Optional;

public interface SiteMemberUuidPrimaryKeyRepository<T> extends UuidPrimaryKeyRepository<T> {
    Optional<T> findByMember(SiteMemberEntity member);
}
